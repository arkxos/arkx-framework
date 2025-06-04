package io.arkx.framework.commons.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class org.ark.framework.utility.lang.TypeConvertUtil
 * @author Darkness
 * @date 2012-3-13 下午1:49:27
 * @version V1.0
 */
public class TypeConvertUtil {

	private static Logger logger = LoggerFactory.getLogger(TypeConvertUtil.class);

	/**
	 * NOT_SUPPORTED 不支持的参数类型
	 */
	public static final int NOT_SUPPORTED = 0; // Unkown

	public static final int INTEGER = 1; // Integer

	public static final int LONG = 2; // Long

	public static final int STRING = 3; // String

	public static final int DECIMAL = 4; // BigDecimal

	public static final int DATE = 5; // Date

	public static final int BASIC_INT = 6; // int

	public static final int BASIC_LONG = 7; // long

	public static final int BASIC_FLOAT = 8; // float

	public static final int BASIC_DOUBLE = 9; // double
	
	public static final int BASIC_SHORT = 10; // short
	public static final int BOOLEAN = 11; // short
	public static final int LIST = 12; // LIST
	public static final int FLOAT = 13; // Float
	
	public static final SimpleDateFormat datePaser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 获取参数类型对应的常量值
	 * 
	 * @param type
	 *            Class - 参数类型
	 * @return int - 对应的常量值
	 */
	private static int getIntType(Class<?> type) {
		if ("java.lang.Integer".equals(type.getName())) {
			return INTEGER;
		}
		if ("java.lang.Long".equals(type.getName())) {
			return LONG;
		}
		if ("java.lang.Float".equals(type.getName())) {
			return FLOAT;
		}
		if ("java.lang.String".equals(type.getName())) {
			return STRING;
		}
		if ("java.math.BigDecimal".equals(type.getName())) {
			return DECIMAL;
		}
		if ("int".equals(type.getName())) {
			return BASIC_INT;
		}
		if ("long".equals(type.getName())) {
			return BASIC_LONG;
		}
		if ("float".equals(type.getName())) {
			return BASIC_FLOAT;
		}
		if ("double".equals(type.getName())) {
			return BASIC_DOUBLE;
		}
		if ("java.lang.Double".equals(type.getName())) {
			return BASIC_DOUBLE;
		}
		if ("short".equals(type.getName())) {
			return BASIC_SHORT;
		}
		if ("java.util.Date".equals(type.getName()) || "java.sql.Date".equals(type.getName())) {
			return DATE;
		}
		if ("boolean".equals(type.getName()) || "java.lang.Boolean".equals(type.getName())) {
			return BOOLEAN;
		}
		if (type.isAssignableFrom(List.class)) {
			return LIST;
		}
		
		return NOT_SUPPORTED;
	}

	/**
	 * 获取相应类型的JavaBean属性值
	 * 
	 * @param type
	 *            Class 类型
	 * @param value
	 *            String 属性字串值
	 * @return Object 属性值
	 */
	public static Object convertValue(Class<?> type, Object value) {
		if (getIntType(type) == STRING) {
			return (value + "").trim();
		}
		if (getIntType(type) == LIST) {
			return value;
		}
		try {
			if (value == null || "".equals(value) || "null".equals(value)) {
				return null;
			}

			switch (getIntType(type)) {
			case INTEGER: {
				return Integer.valueOf(value+"");
			}
			case LONG: {
				return Long.valueOf(value+"");
			}
			case FLOAT: {
				return new Float(value+"");
			}
			case DECIMAL: {
				return new BigDecimal(value+"");
			}
			case BASIC_INT: {
				return Integer.valueOf(value+"");
			}
			case BASIC_LONG: {
				return Long.valueOf(value+"");
			}
			case BASIC_FLOAT: {
				return new Float(value+"");
			}
			case BASIC_DOUBLE: {
				return new Double(value+"");
			}
			case BASIC_SHORT: {
				return new Short(value+"");
			}
			case BOOLEAN: {
				if ("true".equalsIgnoreCase(value+"")) {
					return true;
				}
				if ("false".equalsIgnoreCase(value+"")) {
					return false;
				}
				if ("Y".equalsIgnoreCase(value+"")) {
					return true;
				}
				if ("N".equalsIgnoreCase(value+"")) {
					return false;
				}
				if ("Yes".equalsIgnoreCase(value+"")) {
					return true;
				}
				if ("No".equalsIgnoreCase(value+"")) {
					return false;
				}
				try {
					int intValue = Integer.parseInt(value+"");
					if(intValue > 0) {
						return true;
					}
				} catch (Exception e) {
					return false;
				}
				return false;
			}
			
			case DATE: {
				
				if(value instanceof Date) {
					return value;
				}
				
				String dateStr = value + "";
				if (dateStr.length() == 10) {
					dateStr += " 00:00:00";
				}

				return datePaser.parse(dateStr);

			}
			default: {
				return null;
			}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return null;
		}

	}

	public static int getIntegerValue(Object value) {
		if (value == null)
			return 0;
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).intValue();
		}
		return 0;
	}

	public static StringBuffer converBlobToString(Blob blob) {
		StringBuffer sb = new StringBuffer();
		String temp = "";
		try {

			BufferedReader bf = new BufferedReader(new InputStreamReader(blob.getBinaryStream()));
			while ((temp = bf.readLine()) != null) {
				sb.append(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Blob->String转化异常");
		}
		return sb;

	}
	//
	// public static BLOB convertStringToBlob(String str) {
	// try {
	// java.sql.Blob bl = new SerialBlob(str.getBytes());
	// oracle.sql.BLOB blob=(oracle.sql.BLOB)bl;
	// return blob;
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error("String->Blob转化异常");
	// }
	// return null;
	//
	// }

	// ///需要添加由Blob转String\Img\等方法.....

}
