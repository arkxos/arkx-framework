package com.arkxos.framework.core.scanner;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import com.arkxos.framework.annotation.Alias;
import com.arkxos.framework.annotation.util.PackageUtil;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.commons.util.ZipUtil;
import com.arkxos.framework.config.ExcludeClassScan;

/**
 * 编译后资源扫描器
 */
public class BuiltResourceScanner {
	private long lastTime;
	private IBuiltResourceVisitor visitor;
	private String WEBINFPath;

	public BuiltResourceScanner(IBuiltResourceVisitor visitor, String WEBINFPath) {
		this.visitor = visitor;
		this.WEBINFPath = WEBINFPath;
	}

	/**
	 * 扫描指定路径下的编译后资源
	 * @param annotationClass 
	 * 
	 * @param visitors 编译后资源遍历器列表
	 * @param WEBINFPath WEB-INF路径
	 */
	public void scan(long lastTime) {
		scan(lastTime, null);
	}
	
	public void scan(long lastTime, Class<? extends Annotation> annotationClass) {
		scan(lastTime, annotationClass, Arrays.asList("com.arkxos","com.xdreamaker"));
	}
	
	public void scan(long lastTime, Class<? extends Annotation> annotationClass, List<String> pageList) {
		
		List<String> classes = new ArrayList<String>();
		if (annotationClass != null) {
			for (String page : pageList) {
				classes.addAll(PackageUtil.findAnnotationedClasses(page, annotationClass));
			}
		} else {
			classes = PackageUtil.findAnnotationClasses("com/arkxos", Alias.class);
		}
		{
			String baseUiFacadeClassName = "io.arkx.framework.boot.BaseUIFacade";
			String fileName = StringUtil.replaceEx(baseUiFacadeClassName, ".", "/") + ".class";
			InputStream inputStream	= this.getClass().getResourceAsStream("/" + fileName);
			BuiltResource br = new BuiltResource(fileName, inputStream);
			try {
				scanOneResource(br);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (String clazzName : classes) {
			String fileName = StringUtil.replaceEx(clazzName, ".", "/") + ".class";
			InputStream inputStream	= this.getClass().getResourceAsStream("/" + fileName);
			BuiltResource br = new BuiltResource(fileName, inputStream);
			try {
				scanOneResource(br);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		this.lastTime = lastTime;
//		long t = System.currentTimeMillis();
//		String pluginPath = Config.getPluginPath();
//		if (WEBINFPath == null) {
//		} else {
//			pluginPath = WEBINFPath + "/";
//		}
//		Set<String> pluginPackageFiles = new HashSet<>();
//		List<PluginConfig> list = PluginManager.getInstance().getAllPluginConfig();
//		for (PluginConfig pc : list) {// 此处得到的列表是按依赖关系排序之后的
//			if(pluginPackageFiles.contains(pc.getPackageFile())) {
//				continue;
//			}
//			
//			pluginPackageFiles.add(pc.getPackageFile());
//			
//			if(pc.getPackageFile().endsWith(".jar")) {
//				scanJar(new File(pc.getPackageFile()));// jar中的文件优先级最低
//			}
//			for (String path : pc.getPluginFiles()) {
//				if (path.startsWith("[D]")) {
//					path = path.substring(3);
//					if (path.startsWith("JAVA")) {
//						path = pluginPath + "classes/" + path.substring(5);
//						path = FileUtil.normalizePath(path);
//						try {
//							scanOneDir(new File(path), pluginPath + "classes/");
//						} catch (Exception e) {
//							e.printStackTrace();
//							LogUtil.error("Load class directory failed:" + e.getMessage());
//						}
//					}
//				} else {
//					if (!path.endsWith(".java")) {
//						continue;
//					}
//					path = path.substring(5);
//					path = path.substring(0, path.lastIndexOf("."));
//					path = pluginPath + "classes/" + path + ".class";
//					File f = new File(path);
//					if (!f.exists() || f.lastModified() < lastTime) {
//						continue;
//					}
//					try {
//						BuiltResource br = new BuiltResource(path, null);
//						scanOneResource(br);
//					} catch (Exception e) {
//						e.printStackTrace();
//						LogUtil.error("Load single class failed:" + path);
//					}
//				}
//			}
//		}
//		if (lastTime == 0) {
//			LogUtil.info("----" + Config.getAppCode() + "(" + LangUtil.get(Config.getAppName()) + "): Scan class and resource used "
//					+ (System.currentTimeMillis() - t) + " ms----");
//		}
	}

	private void scanJar(File f) {
		try {
			if (!f.exists() || f.lastModified() < lastTime) {
				return;
			}
			Mapx<String, Long> files = ZipUtil.getFileListInZip(f.getAbsolutePath());
			for (String entryName : files.keySet()) {
				if (entryName.indexOf("$") > 0) {
					continue;
				}
				BuiltResource br = new BuiltResource(f.getAbsolutePath(), entryName);
				scanOneResource(br);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void scanOneDir(File p, String prefix) throws Exception {
		if (!p.exists()) {
			return;
		}
		String path = FileUtil.normalizePath(p.getAbsolutePath());
		if (!path.endsWith("/")) {
			path += "/";
		}
		path = path.substring(prefix.length());
		String exclude = ExcludeClassScan.getValue();
		if (ObjectUtil.notEmpty(exclude)) {
			for (String str : StringUtil.splitEx(exclude, ",")) {
				if (ObjectUtil.notEmpty(str) && path.startsWith(str)) {
					return;
				}
			}
		}
		File[] fs = p.listFiles();
		if (fs == null) {
			return;// linux下可能会有问题
		}
		for (File f : fs) {
			if (f.isFile()) {
				if (f.lastModified() > lastTime) {
					BuiltResource br = new BuiltResource(f.getAbsolutePath(), (String)null);
					if (br.getFileName().indexOf("$") > 0) {
						continue;
					}
					try {
						scanOneResource(br);
					} catch(Exception exception) {
						System.err.println("scanOneResource: " + br.getFileName() + " fail");
						throw exception;
					}
				}
			} else {
				scanOneDir(f, prefix);
			}
		}
	}

	public void scanOneResource(BuiltResource br) throws Exception {
		if (br.getFullName().indexOf("com/arkxos/framework/") >= 0 && br.getFullName().indexOf("io/arkx/framework/boot") == -1) {
			return;
		}
		if(br.getFullName().indexOf("LoginUI")!=-1) {
//			System.out.println("==========================");
		}
		ClassNode cn = null;
		if (visitor.match(br)) {
			if (cn == null) {
				if (br.isClass()) {
					InputStream is = br.getInputStream();
					if(is == null) {
						return;
					}
					try {
						ClassReader cr = new ClassReader(is);
						cn = new ClassNode();
						cr.accept(cn, 0);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						is.close();
					}
					visitor.visitClass(br, cn);
					for (InnerClassNode icn : (List<InnerClassNode>)cn.innerClasses) {
						if (icn.outerName == null || !icn.name.startsWith(cn.name)) {
							continue;
						}
						InputStream iis = br.getInnerClassInputStream(icn.name);
						try {
							if(iis != null) {
								ClassReader cr = new ClassReader(iis);
								ClassNode cn2 = new ClassNode();
								cr.accept(cn2, 0);
								visitor.visitInnerClass(br, cn, cn2);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if(iis != null) {
								iis.close();
							}
						}
					}
				} else {
					visitor.visitResource(br);
				}
			}
		}
	}
}
