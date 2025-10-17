package org.ark.framework.orm.schema;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.arkx.framework.Config;
import io.arkx.framework.commons.util.ExportProgressTracker;
import io.arkx.framework.commons.util.FileUtil;

import io.arkx.framework.data.db.connection.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;

/**   
 * @class org.ark.framework.orm.schema.SchemaGenerator
 * @author Darkness
 * @date 2012-10-22 下午10:40:20 
 * @version V1.0   
 */
@Slf4j
public abstract class SchemaGenerator {

//	private static Logger logger = log.getLogger(SchemaGenerator.class);
	public static final String PACKAGE = "org.ark.framework.orm.db";
	
	protected String outputDir;
	protected String namespace;
	protected boolean isOracle = false;

	protected String databaseName;

	protected boolean enablePerfMon = true; // 性能监控开关

	// 进度追踪相关
	protected String taskId;  // 任务ID，用于跟踪进度

	protected ConnectionConfig connectionConfig;

	public ConnectionConfig getConnectionConfig() {
		return connectionConfig;
	}

	public void setConnectionConfig(ConnectionConfig connectionConfig) {
		this.connectionConfig = connectionConfig;
	}


	// 性能计数器
	private ConcurrentHashMap<String, AtomicLong> perfCounters = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Long> tableGenerateTimes = new ConcurrentHashMap<>();


	public SchemaGenerator(String databaseName) {
		this.databaseName = databaseName;
		namespace = PACKAGE + ".t" + databaseName + ".schema";
		outputDir = getDefaultPath();
	}

	private String getDefaultPath() {
		String prefix = Config.getContextRealPath();

		// 如果 prefix 为空或长度不足，直接返回空字符串或默认值
		if (prefix == null || prefix.isEmpty()) {
			return "";
		}

		// 去掉末尾的一个字符（如果是 "/"）
		if (prefix.endsWith("/")) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}

		// 查找最后一个 "/"，并截取路径
		int lastSlash = prefix.lastIndexOf("/");
		if (lastSlash == -1) {
			return ""; // 没有 "/" 说明路径格式错误，返回空
		}

		prefix = prefix.substring(0, lastSlash + 1);

		// 构造 Java 路径
		// 使用File对象确保路径分隔符一致
		File javaPathDir = new File(prefix, "Java");
		String[] namespaceParts = namespace.split("\\.");
		for (String part : namespaceParts) {
			javaPathDir = new File(javaPathDir, part);
		}
		String javapath = javaPathDir.getPath();

		// 创建目录并删除已有的 .java 文件
		FileUtil.mkdir(javapath);
		// 替换 deleteEx 方法，使用 Hutool 的方法删除所有匹配的 Java 文件
		File dir = new File(javapath);
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && file.getName().matches(".+java")) {
					FileUtil.delete(file);
				}
			}
		}

//		FileUtil.deleteEx(javapath + "/.+java");
		return javapath;
	}

	public SchemaGenerator(String namespace, String outputDir) {
		this.namespace = namespace;
		this.outputDir = outputDir;
	}

	/**
	 * 设置任务ID，用于进度跟踪
	 * @param taskId 任务ID
	 * @return 当前实例，用于链式调用
	 */
	public SchemaGenerator withTaskId(String taskId) {
		this.taskId = taskId;
		return this;
	}

	public abstract SchemaGenerator.SchemaTable[] getSchemaTables();

	/**
	 * 执行并记录耗时
	 * @param name 操作名称
	 * @param runnable 要执行的操作
	 */
	private void timeOperation(String name, Runnable runnable) {
		if (!enablePerfMon) {
			runnable.run();
			return;
		}

		long start = System.currentTimeMillis();
		try {
			runnable.run();
		} finally {
			long elapsed = System.currentTimeMillis() - start;
			perfCounters.computeIfAbsent(name, k -> new AtomicLong(0))
					.addAndGet(elapsed);
		}
	}

	/**
	 * 输出性能统计信息
	 */
	private void reportPerformance() {
		if (!enablePerfMon) return;

		log.info("===== 性能统计信息 =====");
		log.info("总生成耗时: {}ms", perfCounters.getOrDefault("generate", new AtomicLong(0)).get());

		// 输出表生成耗时排名
		log.info("===== 表生成耗时排名 =====");
		tableGenerateTimes.entrySet().stream()
				.sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 从高到低排序
				.forEach(entry -> {
					log.info("表[{}]生成耗时: {}ms", entry.getKey(), entry.getValue());
				});

		log.info("===== 操作耗时统计 =====");
		perfCounters.entrySet().stream()
				.filter(entry -> !entry.getKey().equals("generate"))
				.sorted((e1, e2) -> Long.compare(e2.getValue().get(), e1.getValue().get()))
				.forEach(entry -> {
					log.info("{}: {}ms", entry.getKey(), entry.getValue().get());
				});
	}

	public void generate() {
		// 如果有taskId，初始化进度跟踪
		final SchemaTable[] schemaTables = getSchemaTables();
		if (taskId != null && !taskId.isEmpty()) {
			ExportProgressTracker.getInstance().updateStage(taskId, ExportProgressTracker.STAGE_SCHEMA_GENERATION);
			ExportProgressTracker.getInstance().startTask(taskId, schemaTables.length);
		}

		timeOperation("generate", () -> {
			// 优化线程池大小：对于IO密集型任务，线程数设置为处理器核心数的4倍
			int processors = Runtime.getRuntime().availableProcessors();
			int threadCount = processors * 4; // IO密集型任务推荐配置
			ListeningExecutorService executorService = MoreExecutors.listeningDecorator(
					Executors.newFixedThreadPool(threadCount));

			List<ListenableFuture<?>> futures = new ArrayList<>();
			int i = 1;

			// 提交所有任务到线程池
			for (SchemaTable schemaTable : schemaTables) {
				final int index = i++;
				System.out.println("["+index+"]生成表：" + schemaTable.tableCode);

				// 更新正在处理的表名
				if (taskId != null && !taskId.isEmpty()) {
					ExportProgressTracker.getInstance().updateCurrentTable(taskId, schemaTable.tableCode);
				}

				// 使用Guava的ListenableFuture提交任务
				ListenableFuture<?> future = executorService.submit(() -> {
					try {
						long start = System.currentTimeMillis();
						generateOneSechma(schemaTable.tableName, schemaTable.tableCode,
								schemaTable.tableComment, schemaTable.schemaColumns);
						long elapsed = System.currentTimeMillis() - start;

						if (enablePerfMon) {
							tableGenerateTimes.put(schemaTable.tableCode, elapsed);
						}

						// 更新进度
						if (taskId != null && !taskId.isEmpty()) {
							ExportProgressTracker.getInstance().incrementTableCount(taskId);
						}
					} catch (Exception e) {
						log.error("生成表[" + schemaTable.tableCode + "]时出错: " + e.getMessage(), e);
						throw new RuntimeException(e);
					}
				});

				futures.add(future);
			}

			try {
				// 使用Guava的Futures工具等待所有任务完成
				Futures.allAsList(futures).get();
			} catch (Exception e) {
				log.error("等待表生成完成时出错: " + e.getMessage(), e);
				throw new RuntimeException("表生成过程中发生错误", e);
			} finally {
				// 关闭线程池
				executorService.shutdown();
			}
		});

		// 输出性能统计
		reportPerformance();
	}

	/**
	 * 直接写入文件，避免FileUtil.writeText的内容检查和多次读取
	 *
	 * @param filePath 文件路径
	 * @param content 文件内容
	 * @return 是否写入成功
	 */
	private boolean writeTextDirect(String filePath, String content) {
		AtomicLong result = new AtomicLong(0);
		timeOperation("writeTextDirect", () -> {
			File file = new File(filePath);
			// 确保父目录存在
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}

			try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
				writer.write(content);
				result.set(1);
			} catch (IOException e) {
				log.error("写入文件失败: " + filePath, e);
				result.set(0);
			}
		});
		return result.get() == 1;
	}

	protected void generateOneSechma(String tableName, String tableCode, String tableComment, SchemaColumn[] scs) {
		// 首先对tableCode做标准化处理，以保证后续Lambda中使用一致的值
		final String origTableCode = tableCode;
		tableCode = tableCode.substring(0, 1).toUpperCase() + tableCode.substring(1);
		final String finalTableCode = tableCode;

		timeOperation("generateOneSechma(" + finalTableCode + ").checkStep", () -> {
			List<String> ingoreList = Arrays.asList("");

			if (ingoreList.contains(tableName)) {
				return;
			}

			if (!checkCode(origTableCode, "表代码")) {
				System.err.println("错误的表代码：" + tableName);
				throw new RuntimeException("错误的表代码：[" + tableName + "]");
			}
		});

		// 预分配更大的初始容量，减少扩容次数
		StringBuilder sb = new StringBuilder(8192);
		StringBuilder dsb = new StringBuilder(4096);
		StringBuilder hsb = new StringBuilder(4096);
		StringBuilder svsb = new StringBuilder(1024);
		StringBuilder gvsb = new StringBuilder(1024);
		StringBuilder csb = new StringBuilder(16384); // 字段相关代码量大，分配更多空间
		StringBuilder isb = new StringBuilder(512);
		StringBuilder insertsb = new StringBuilder(1024);
		StringBuilder updatesb = new StringBuilder(1024);
		StringBuilder pksb = new StringBuilder(256);
		StringBuilder keysb = new StringBuilder(256);

		// 生成代码部分
		timeOperation("generateOneSechma(" + finalTableCode + ").codeGeneration", () -> {
			sb.append("package ").append(namespace).append(";\n\n");

			isb.append("import org.ark.framework.orm.Schema;\n");
			isb.append("import org.ark.framework.orm.SchemaColumn;\n");
			isb.append("import org.ark.framework.orm.SchemaSet;\n");
			isb.append("import io.arkx.framework.commons.collection.DataTypes;\n");
			isb.append("import io.arkx.framework.data.jdbc.Query;\n");

			hsb.append("\tpublic static final SchemaColumn[] _Columns = new SchemaColumn[] {\n");

			svsb.append("\tpublic void setV(int i, Object v) {\n");
			gvsb.append("\tpublic Object getV(int i) {\n");

			insertsb.append("\tprotected static final String _InsertAllSQL = \"insert into ").append(finalTableCode).append(" (");
			updatesb.append("\tprotected static final String _UpdateAllSQL = \"update ").append(finalTableCode).append(" set ");
			pksb.append(" where ");

			boolean dateFlag = false;
			boolean firstPKFlag = true;
			boolean firstColumnFlag = true;
			for (int i = 0; i < scs.length; i++) {
				String code = scs[i].Code;
				if (firstColumnFlag) {
					insertsb.append(code);
					updatesb.append(code).append("=?");
					firstColumnFlag = false;
				} else {
					insertsb.append(",").append(code);
					updatesb.append(",").append(code).append("=?");
				}
				if (scs[i].isPrimaryKey) {
					if (firstPKFlag) {
						pksb.append(code).append("=?");
						keysb.append(code);
						firstPKFlag = false;
					} else {
						pksb.append(" and ").append(code).append("=?");
						keysb.append(", ").append(code);
					}
				}
				if (!checkCode(code, "表" + finalTableCode + "的字段")) {
					throw new RuntimeException("非法的表【" + finalTableCode + "】的字段【" + code+"】");
				}
				String dataType = scs[i].DataType;
				if ((dataType == null) || (dataType.equals(""))) {
					log.error("表" + finalTableCode + "的字段" + code + "的数据类型未定义!");
					throw new RuntimeException("表" + finalTableCode + "的字段" + code + "的数据类型未定义!");
				}
				String type = dataType.toLowerCase().trim();
				String ctype = null;
				String vtype = null;
				if ((type.startsWith("nvarchar")) || (type.startsWith("varchar"))
						|| (type.startsWith("char")) || (type.startsWith("nchar"))
						|| (type.startsWith("bpchar"))
						|| (type.startsWith("enum"))) {
					type = "String";
					ctype = "STRING";
					vtype = type;
				} else if ((type.startsWith("long varchar"))
						|| (type.startsWith("ntext")) || (type.startsWith("text"))
						|| (type.startsWith("mediumtext"))
						|| (type.startsWith("longtext"))
						|| (type.startsWith("clob")
						|| (type.startsWith("jsonb")))) {
					type = "String";
					ctype = "CLOB";
					vtype = type;
				} else if ((type.startsWith("int")) || (type.startsWith("bit"))
						|| (type.startsWith("smallint"))
						|| (type.startsWith("tinyint"))
						|| (type.startsWith("mediumint"))) {
					type = "int";
					ctype = "INTEGER";
					vtype = "Integer";
					if ((isOracle) && (type.startsWith("int"))) {
						type = "long";
						ctype = "LONG";
						vtype = "Long";
					}
				} else if ((type.startsWith("long")) || (type.startsWith("bigint"))) {
					type = "long";
					ctype = "LONG";
					vtype = "Long";
					if ((isOracle) && (type.startsWith("long"))) {
						type = "String";
						ctype = "STRING";
						vtype = type;
					}
				} else if (type.startsWith("boolean")) {
					type = "boolean";
					ctype = "boolean";
					vtype = "boolean";
				} else if (type.startsWith("float")) {
					type = "float";
					ctype = "FLOAT";
					vtype = "Float";
				} else if ((type.startsWith("double"))
						|| (type.startsWith("decimal"))
						|| (type.startsWith("number"))
						|| (type.startsWith("money"))
						|| (type.startsWith("numeric"))) {
					type = "double";
					ctype = "DOUBLE";
					vtype = "Double";
				} else if (type.endsWith("blob") || (type.startsWith("blob")) || (type.startsWith("image"))) {
					type = "byte[]";
					ctype = "BLOB";
					vtype = type;
				} else if ((type.startsWith("date")) || (type.startsWith("time"))) {
					type = "Date";
					ctype = "DATETIME";
					vtype = type;
					dateFlag = true;
				} else {
					log.error(finalTableCode + "：不支持的数据类型" + type);
					throw new RuntimeException(finalTableCode + "：不支持的数据类型" + type);
				}
				dsb.append("\tprivate ").append(vtype).append(" ").append(scs[i].Code).append(";\n\n");
				if (code.length() < 1) {
					throw new RuntimeException("错误的代码：" + code);
				}
				String firstCode = code.substring(0, 1);
				String uCode = firstCode.toUpperCase() + code.substring(1);

				csb.append("\t/**\n");
				csb.append("\t* 获取字段").append(code).append("的值，该字段的<br>\n");
				csb.append("\t* 字段名称 :").append(scs[i].Name).append("<br>\n");
				csb.append("\t* 数据类型 :").append(scs[i].DataType).append("<br>\n");
				csb.append("\t* 是否主键 :").append(scs[i].isPrimaryKey).append("<br>\n");
				csb.append("\t* 是否必填 :").append(scs[i].Mandatory).append("<br>\n");
				if (scs[i].Comment != null) {
					csb.append("\t* 备注信息 :<br>\n");
					splitComment(csb, scs[i].Comment, "\t");
				}
				csb.append("\t*/\n");
				csb.append("\tpublic ").append(type).append(" get").append(uCode).append("() {\n");
				if ((vtype.equals("Float")) || (vtype.equals("Integer"))
						|| (vtype.equals("Long")) || (vtype.equals("Double"))) {
					csb.append("\t\tif(").append(code).append("==null){return 0;}\n");
					csb.append("\t\treturn ").append(code).append(".").append(type).append("Value();\n");
				} else {
					csb.append("\t\treturn ").append(code).append(";\n");
				}
				csb.append("\t}\n\n");

				csb.append("\t/**\n");
				csb.append("\t* 设置字段").append(code).append("的值，该字段的<br>\n");
				csb.append("\t* 字段名称 :").append(scs[i].Name).append("<br>\n");
				csb.append("\t* 数据类型 :").append(scs[i].DataType).append("<br>\n");
				csb.append("\t* 是否主键 :").append(scs[i].isPrimaryKey).append("<br>\n");
				csb.append("\t* 是否必填 :").append(scs[i].Mandatory).append("<br>\n");
				if (scs[i].Comment != null) {
					csb.append("\t* 备注信息 :<br>\n");
					splitComment(csb, scs[i].Comment, "\t");
				}
				csb.append("\t*/\n");

				String tCode = code.substring(0, 1).toLowerCase() + code.substring(1);
				csb.append("\tpublic void set").append(uCode).append("(").append(type).append(" ").append(tCode).append(") {\n");
				if ((vtype.equals("Float")) || (vtype.equals("Integer"))
						|| (vtype.equals("Long")) || (vtype.equals("Double")))
					csb.append("\t\tthis.").append(code).append(" = new ").append(vtype).append("(").append(tCode).append(");\n");
				else {
					csb.append("\t\tthis.").append(code).append(" = ").append(tCode).append(";\n");
				}
				csb.append("    }\n\n");

				if ((vtype.equals("Float")) || (vtype.equals("Integer"))
						|| (vtype.equals("Long")) || (vtype.equals("Double"))) {
					csb.append("\t/**\n");
					csb.append("\t* 设置字段").append(code).append("的值，该字段的<br>\n");
					csb.append("\t* 字段名称 :").append(scs[i].Name).append("<br>\n");
					csb.append("\t* 数据类型 :").append(scs[i].DataType).append("<br>\n");
					csb.append("\t* 是否主键 :").append(scs[i].isPrimaryKey).append("<br>\n");
					csb.append("\t* 是否必填 :").append(scs[i].Mandatory).append("<br>\n");
					if (scs[i].Comment != null) {
						csb.append("\t* 备注信息 :<br>\n");
						splitComment(csb, scs[i].Comment, "\t");
					}
					csb.append("\t*/\n");
					csb.append("\tpublic void set").append(uCode).append("(String ").append(tCode).append(") {\n");
					csb.append("\t\tif (").append(tCode).append(" == null){\n");
					csb.append("\t\t\tthis.").append(code).append(" = null;\n");
					csb.append("\t\t\treturn;\n");
					csb.append("\t\t}\n");
					csb.append("\t\tthis.").append(code).append(" = new ").append(vtype).append("(").append(tCode).append(");\n");
					csb.append("    }\n\n");
				}

				hsb.append("\t\tnew SchemaColumn(\"").append(code).append("\", DataTypes.")
						.append(ctype).append(".code(), ").append(i).append(", ").append(scs[i].Length).append(" , ")
						.append(scs[i].Precision).append(" , ").append(scs[i].Mandatory).append(" , ")
						.append(scs[i].isPrimaryKey).append(",\"").append(scs[i].Comment).append("\")");
				if (i < scs.length - 1)
					hsb.append(",\n");
				else {
					hsb.append("\n");
				}

				if ((vtype.equals("Float")) || (vtype.equals("Integer"))
						|| (vtype.equals("Long")) || (vtype.equals("Double")))
					svsb.append("\t\tif (i == ").append(i).append("){if(v==null){").append(code)
							.append(" = null;}else{").append(code).append(" = new ").append(vtype)
							.append("(v.toString());}return;}\n");
				else {
					svsb.append("\t\tif (i == ").append(i).append("){").append(code).append(" = (").append(vtype)
							.append(")v;return;}\n");
				}
				gvsb.append("\t\tif (i == ").append(i).append("){return ").append(code).append(";}\n");
			}

			if (dateFlag) {
				isb.append("import java.util.Date;\n");
			}
			isb.append("\n");
			sb.append(isb);

			sb.append("/**\n");
			sb.append(" * 表名称：").append(tableName);
			sb.append("<br>\n * 表代码：").append(finalTableCode);
			if (tableComment != null) {
				sb.append("<br>\n * 表备注：<br>\n");
				splitComment(sb, tableComment, "");
			}
			sb.append("<br>\n * 表主键：").append(keysb);
			sb.append("<br>\n */\n");
			sb.append("public class ").append(finalTableCode).append("Schema extends Schema {\n");

			sb.append(dsb);

			hsb.append("\t};\n\n");

			hsb.append("\tpublic static final String _TableCode = \"").append(finalTableCode).append("\";\n\n");
			hsb.append("\tpublic static final String _NameSpace = \"").append(namespace).append("\";\n\n");
			insertsb.append(") values(");
			firstColumnFlag = true;
			for (int i = 0; i < scs.length; i++) {
				if (firstColumnFlag) {
					insertsb.append("?");
					firstColumnFlag = false;
				} else {
					insertsb.append(",?");
				}
			}
			insertsb.append(")\";\n\n");
			updatesb.append("");
			updatesb.append(pksb);
			updatesb.append("\";\n\n");
			hsb.append(insertsb);
			hsb.append(updatesb);
			hsb.append("\tprotected static final String _DeleteSQL = \"delete from ").append(finalTableCode).append(" ").append(pksb.toString()).append("\";\n\n");
			hsb.append("\tprotected static final String _FillAllSQL = \"select * from ").append(finalTableCode).append(" ").append(pksb.toString()).append("\";\n\n");

			hsb.append("\tpublic ").append(finalTableCode).append("Schema(){\n");
			hsb.append("\t\tTableCode = _TableCode;\n");
			hsb.append("\t\tTableComment = \"").append(tableComment).append("\";\n");
			hsb.append("\t\tNameSpace = _NameSpace;\n");
			hsb.append("\t\tColumns = _Columns;\n");
			hsb.append("\t\tInsertAllSQL = _InsertAllSQL;\n");
			hsb.append("\t\tUpdateAllSQL = _UpdateAllSQL;\n");
			hsb.append("\t\tDeleteSQL = _DeleteSQL;\n");
			hsb.append("\t\tFillAllSQL = _FillAllSQL;\n");
			hsb.append("\t\tHasSetFlag = new boolean[").append(scs.length).append("];\n");
			hsb.append("\t}\n\n");

			hsb.append("\tpublic Schema newInstance(){\n");
			hsb.append("\t\treturn new ").append(finalTableCode).append("Schema();\n");
			hsb.append("\t}\n\n");

			hsb.append("\tpublic ").append(finalTableCode).append("Set newSet(){\n");
			hsb.append("\t\treturn new ").append(finalTableCode).append("Set();\n");
			hsb.append("\t}\n\n");

			hsb.append("\tpublic ").append(finalTableCode).append("Set query() {\n");
			hsb.append("\t\treturn query(null, -1, -1);\n");
			hsb.append("\t}\n\n");

			hsb.append("\tpublic ").append(finalTableCode).append("Set query(Query qb) {\n");
			hsb.append("\t\treturn query(qb, -1, -1);\n");
			hsb.append("\t}\n\n");

			hsb.append("\tpublic ").append(finalTableCode).append("Set query(int pageSize, int pageIndex) {\n");
			hsb.append("\t\treturn query(null, pageSize, pageIndex);\n");
			hsb.append("\t}\n\n");

			hsb.append("\tpublic ").append(finalTableCode).append("Set query(Query qb , int pageSize, int pageIndex){\n");
			hsb.append("\t\treturn (").append(finalTableCode).append("Set)querySet(qb , pageSize , pageIndex);\n");
			hsb.append("\t}\n\n");

			svsb.append("\t}\n\n");
			gvsb.append("\t\treturn null;\n");
			gvsb.append("\t}\n\n");
			sb.append(hsb);
			sb.append(svsb);
			sb.append(gvsb);
			sb.append(csb);
			sb.append("}");
		});

		// 文件写入部分
		timeOperation("generateOneSechma(" + finalTableCode + ").fileIO", () -> {
			// 直接写入文件，避免FileUtil.writeText的内容检查
			String schemaFilePath = outputDir + "/" + finalTableCode + "Schema.java";
			writeTextDirect(schemaFilePath, sb.toString());

			// 生成Set类并直接写入文件
			generateSet(finalTableCode);
		});
	}
	
	/**
	 * 生成SchemaSet
	 * 
	 * @param tableCode
	 */
	private void generateSet(String tableCode) {
		final String finalTableCode = tableCode; // 创建一个final引用
		timeOperation("generateSet(" + finalTableCode + ")", () -> {
			// 使用StringBuilder代替StringBuffer，并预分配合理容量
			StringBuilder sb = new StringBuilder(2048);
			sb.append("package ").append(namespace).append(";\n\n");
			sb.append("import ").append(namespace).append(".").append(finalTableCode).append("Schema;\n");
			sb.append("import org.ark.framework.orm.SchemaSet;\n\n");
			sb.append("public class ").append(finalTableCode).append("Set extends SchemaSet<").append(finalTableCode).append("Schema> {\n");
			sb.append("\tpublic ").append(finalTableCode).append("Set() {\n");
			sb.append("\t\tthis(10,0);\n");
			sb.append("\t}\n\n");

			sb.append("\tpublic ").append(finalTableCode).append("Set(int initialCapacity) {\n");
			sb.append("\t\tthis(initialCapacity,0);\n");
			sb.append("\t}\n\n");

			sb.append("\tpublic ").append(finalTableCode)
					.append("Set(int initialCapacity,int capacityIncrement) {\n");
			sb.append("\t\tsuper(initialCapacity,capacityIncrement);\n");
			sb.append("\t\tColumns = ").append(finalTableCode).append("Schema._Columns;\n");
			sb.append("\t}\n\n");

			sb.append("\tpublic ").append(finalTableCode).append("Schema[] createSchemaSet(int initialCapacity){")
					.append("\n\t\treturn new ").append(finalTableCode).append("Schema[initialCapacity];\n")
					.append("\t}\n\n");

			sb.append("\tprotected ").append(finalTableCode).append("Set newInstance(){\n");
			sb.append("\t\treturn new ").append(finalTableCode).append("Set();\n");
			sb.append("\t}\n\n");

			sb.append("}\n ");

			// 直接写入文件，避免FileUtil.writeText的内容检查
			String setFilePath = outputDir + "/" + finalTableCode + "Set.java";
			writeTextDirect(setFilePath, sb.toString());
		});
	}
	
	private void splitComment(StringBuilder sb, String comment, String tab) {
		String[] a = comment.split("\n");
		for (int i = 0; i < a.length; i++) {
			if (a[i].trim().equals("")) {
				continue;
			}
			sb.append(tab);
			sb.append(a[i].trim());
			sb.append("<br>\n");
		}
	}
	

	/**
	 * 检测是否是合法的JAVA标识符
	 * @param code
	 * @param msgPrefix
	 * @return
	 */
	private boolean checkCode(String code, String msgPrefix) {
		char[] ca = code.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			boolean isLegal = (i == 0) ? Character.isJavaIdentifierStart(ca[i]) : Character.isJavaIdentifierPart(ca[i]);

			if (!isLegal) {
				log.error(msgPrefix + code + "不是合适的Java标志名");
				return false;
			}
		}

		return true;
	}

	public class SchemaTable {
		public String tableName;
		public String tableCode;
		public String tableComment;
		public SchemaColumn[] schemaColumns;
	}

	class SchemaColumn {
		public String ID;
		public String Name;
		public String Code;
		public String Comment;
		public String DataType;
		public int Length;
		public int Precision;
		public boolean Mandatory;
		public boolean isPrimaryKey;

		SchemaColumn() {
		}

		public void setMandatory(String mandatory) {
			if ((mandatory == null) || (mandatory.equals("")) || (mandatory.equals("0") || "NO".equalsIgnoreCase(mandatory)|| "N".equalsIgnoreCase(mandatory)))
				Mandatory = false;
			else {
				Mandatory = true;
			}
		}

		public void setPrecision(String precision) {
			try {
				if ((precision != null) && (!precision.equals("")))
					Precision = Integer.parseInt(precision);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setLength(String length) {
			try {
				if ((length != null) && (!length.equals("")))
					Length = Integer.parseInt(length);
			} catch (Exception e) {
				//e.printStackTrace();
				log.warn("列长度"+length+"超出了范围...");
			}
		}
	}
}

