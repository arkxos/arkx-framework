package org.ark.framework.orm.xtable;

/**
 * @author Darkness
 * @date 2012-4-7 上午10:22:29
 * @version V1.0
 */
// #region SimpleTypes，提供对数据库中的某列表现在类当中以何种类型变量来体现
// / <summary>
// / SimpleTypes，提供对数据库中的某列表现在类当中以何种类型变量来体现
// / </summary>
public class SimpleTypes {

	private int code;

	private SimpleTypes(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static SimpleTypes code(int code) {
		for (int i = 0; i < values.length; i++) {
			if (values[i].code == code) {
				return values[i];
			}
		}
		throw new RuntimeException("不支持的类型");
	}

	// / <summary>
	// / char 型 或 varchar 型
	// / </summary>
	public static final SimpleTypes VARCHAR = new SimpleTypes(0);

	// / <summary>
	// / text 型
	// / </summary>
	public static final SimpleTypes TEXT = new SimpleTypes(1);

	// / <summary>
	// / datetime 型
	// / </summary>
	public static final SimpleTypes DATETIME = new SimpleTypes(2);

	// / <summary>
	// / tinyint,int,bit,bigint
	// / </summary>
	public static final SimpleTypes INT = new SimpleTypes(3);

	// / <summary>
	// / real,money,float,decimal,numeric
	// / </summary>
	public static final SimpleTypes DOUBLE = new SimpleTypes(4);

	private static SimpleTypes[] values = new SimpleTypes[] { VARCHAR, TEXT, DATETIME, INT, DOUBLE };

}
