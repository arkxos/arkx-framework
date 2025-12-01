package io.arkx.framework.data.fasttable.util;

import com.google.common.base.Joiner;
import io.arkx.framework.commons.util.lang.ReflectionUtil;
import io.arkx.framework.data.fasttable.annotation.FastColumn;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 *  
 * @author Darkness
 * @date 2016年11月10日 下午2:54:57
 * @version V1.0
 */
public class RecordConverterGenerator {
	
	public static String generate(Class<?> clazz) {
//		String generateFolder = System.getProperty("user.dir") + File.separator + "fastdb-converter-generate-temp";
//		FileUtil.mkdir(generateFolder);
		
		Field[] fields = ReflectionUtil.getDeclaredFields(clazz);
		
		List<Field> fastColumnFields = new ArrayList<>();
		for (Field field : fields) {
			FastColumn fastColumn = field.getDeclaredAnnotation(FastColumn.class);
			if(fastColumn!=null) {
				fastColumnFields.add(field);
			}
		}
		
		String text = generateText(clazz, fastColumnFields);
		return text;
	}
	
	private static String generateFastColumn(Field field) {
		FastColumn columnDefine = field.getAnnotation(FastColumn.class);
		String filedName = field.getName();
		if(field.getType() == String.class) {
			int length = columnDefine.length();
			return "	FastColumn "+filedName+"Column = new FastColumn(FastColumnType.String, \""+filedName+"\", "+length+");\r\n";
		} else if(field.getType() == int.class) {
			return "	FastColumn "+filedName+"Column = new FastColumn(FastColumnType.Int, \""+filedName+"\");\r\n";
		} else if(field.getType() == double.class) {
			return "	FastColumn "+filedName+"Column = new FastColumn(FastColumnType.Double, \""+filedName+"\");\r\n";
		} else if(field.getType() == float.class) {
			return "	FastColumn "+filedName+"Column = new FastColumn(FastColumnType.Float, \""+filedName+"\");\r\n";
		} else if(field.getType() == boolean.class) {
			return "	FastColumn "+filedName+"Column = new FastColumn(FastColumnType.Boolean, \""+filedName+"\");\r\n";
		} else if(field.getType() == LocalDate.class) {
			return "	FastColumn "+filedName+"Column = new FastColumn(FastColumnType.Date, \""+filedName+"\");\r\n";
		}
		throw new RuntimeException("不支持的FastColumn 类型:" + field.getType());
	}
	
	private static String firstUpper(String value) {
		return value.substring(0,1).toUpperCase() + value.substring(1);
	}
	
	private static String generateText(Class<?> clazz, List<Field> fastColumnFields) {
		String classSimpleName = clazz.getSimpleName();
		String result = "import java.nio.ByteBuffer;\r\n" + 
				"import java.time.LocalDate;\r\n" + 
				"\r\n" + 
				"import com.rapidark.infinity.persistence.fastdb.FastColumn;\r\n" + 
				"import com.rapidark.infinity.persistence.fastdb.FastColumnType;\r\n" + 
				"import com.rapidark.infinity.persistence.fastdb.RecordConverter;\r\n" + 
		"\r\n" + 
		"public class "+classSimpleName+"RecordConverter extends RecordConverter<"+classSimpleName+"> {\r\n" + 
		"\r\n";
		
		for (Field field : fastColumnFields) {
			result += generateFastColumn(field);
		}
		
		result += "	FastColumn[] columns = new FastColumn[]{\r\n";
		
		List<String> columnName = new ArrayList<>();
		for (Field field : fastColumnFields) {
			columnName.add(field.getName() + "Column");
		}
		result += "\t\t"+ Joiner.on(",").join(columnName);
		result += "\r\n";
		result += 
		"	};\r\n" + 
		"	\r\n";
		
		result +=
		"	@Override\r\n" + 
		"	public Class<"+classSimpleName+"> acceptEntityClass() {\r\n" + 
		"		return "+classSimpleName+".class;\r\n" + 
		"	}\r\n" + 
		"	\r\n" + 
		"	@Override\r\n" + 
		"	public FastColumn[] getColumns() {\r\n" + 
		"		return columns;\r\n" + 
		"	}\r\n" + 
		"\r\n";
		
		result +=
		"	@Override\r\n" + 
		"	public void writeEntity2Buffer("+classSimpleName+" entity, ByteBuffer recordBuffer) {\r\n";
		
		for (Field field : fastColumnFields) {
			String fieldName = field.getName();
			//String typeName = field.getType().getName();
			String getMethodName = "get" + firstUpper(fieldName) + "";
			if(field.getType() == boolean.class) {
				getMethodName = fieldName;
			}
			//result +="		String name = entity.getName();\r\n" + 
			
			if(field.getType() == String.class) {
				result += "		String "+fieldName+" = entity."+getMethodName+"();\r\n";
				result += "		writeString(recordBuffer, "+fieldName+", "+fieldName+"Column.getLength());\r\n";
			} else if(field.getType() == int.class) {
				result += "		recordBuffer.putInt(entity."+getMethodName+"());// int\r\n";
			} else if(field.getType() == double.class) {
				result += "		recordBuffer.putDouble(entity."+getMethodName+"());// double\r\n";
			} else if(field.getType() == float.class) {
				result += "		recordBuffer.putFloat(entity."+getMethodName+"());// float\r\n";
			} else if(field.getType() == boolean.class) {
				result += "		recordBuffer.put((byte)(entity."+getMethodName+"() ? 1 : 0));// boolean\r\n";
			} else if(field.getType() == LocalDate.class) {
				result += "		recordBuffer.putLong(entity."+getMethodName+"().toEpochDay());// LocalDate\r\n";
			}
		}
		
		result +=
		
		"	}\r\n" + 
		"\r\n";
		
		result +=
		"	public "+classSimpleName+" builderObject(ByteBuffer recordBuffer) {\r\n" +
		"		"+classSimpleName+" entity = new "+classSimpleName+"();\r\n";
		result += "\r\n";
		
		for (Field field : fastColumnFields) {
			String fieldName = field.getName();
			//String typeName = field.getType().getName();
			String setMethodName = "set" + firstUpper(fieldName) + "";
			if(field.getType() == boolean.class) {
				if (fieldName.startsWith("is")) {
					setMethodName = "set" + firstUpper(fieldName.substring(2));
				} else {
					setMethodName = "set" + fieldName;
				}
			}
			//result +="		String name = entity.getName();\r\n" + 
			
			if (field.getType() == String.class) {
				result += "		String " + fieldName + " = readString(recordBuffer, " + fieldName + "Column.getLength());\r\n";
				result += "		entity."+setMethodName+"(" + fieldName + ");\r\n";
			} else if (field.getType() == int.class) {
				result += "		int "+fieldName+" = recordBuffer.getInt();\r\n";
				result += "		entity."+setMethodName+"("+fieldName+");\r\n";
			} else if (field.getType() == double.class) {
				result += "		double "+fieldName+" = recordBuffer.getDouble();\r\n";
				result += "		entity."+setMethodName+"("+fieldName+");\r\n";
			} else if (field.getType() == float.class) {
				result += "		float "+fieldName+" = recordBuffer.getFloat();\r\n";
				result += "		entity."+setMethodName+"("+fieldName+");\r\n";
			} else if (field.getType() == boolean.class) {
				result += "		boolean "+fieldName+" = recordBuffer.get() == 1;\r\n";
				result += "		entity."+setMethodName+"("+fieldName+");\r\n";
			} else if (field.getType() == LocalDate.class) {
				result += "		long "+fieldName+" = recordBuffer.getLong();\r\n";
				result += "		entity."+setMethodName+"(LocalDate.ofEpochDay("+fieldName+"));\r\n";
			}
			result +="		\r\n" ;
		}
		
		result +=
		"		return entity;\r\n" + 
		"	}\r\n" + 
		"\r\n" + 
		"}\r\n" + 
		"";
		return
				result;
	}

}
