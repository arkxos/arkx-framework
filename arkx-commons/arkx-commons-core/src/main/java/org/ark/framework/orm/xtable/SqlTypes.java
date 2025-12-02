package org.ark.framework.orm.xtable;

/**
 * SqlTypes，在数据库当中以何种类型存储
 *
 * @author Darkness
 * @date 2012-4-7 上午10:22:29
 * @version V1.0
 */
public class SqlTypes {

	public static final SqlTypes INT = new SqlTypes(56);

	public static final SqlTypes TINYINT = new SqlTypes(48);

	public static final SqlTypes TEXT = new SqlTypes(35);

	public static final SqlTypes BIT = new SqlTypes(104);

	public static final SqlTypes FLOAT = new SqlTypes(62);

	public static final SqlTypes DATETIME = new SqlTypes(61);

	public static final SqlTypes MONEY = new SqlTypes(60);

	public static final SqlTypes REAL = new SqlTypes(59);

	public static final SqlTypes CHAR = new SqlTypes(175);

	public static final SqlTypes VARCHAR = new SqlTypes(167);

	public static final SqlTypes BIGINT = new SqlTypes(127);

	public static final SqlTypes NUMERIC = new SqlTypes(108);

	public static final SqlTypes DECIMAL = new SqlTypes(106);

	private static SqlTypes[] values = new SqlTypes[] { INT, TINYINT, TEXT, BIT, FLOAT, DATETIME, MONEY, REAL, CHAR,
			VARCHAR, BIGINT, NUMERIC, DECIMAL };

	private int code;

	private SqlTypes(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static SqlTypes code(int code) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].code == code) {
				return values[i];
			}
		}
		throw new RuntimeException("不支持的类型");
	}

}
