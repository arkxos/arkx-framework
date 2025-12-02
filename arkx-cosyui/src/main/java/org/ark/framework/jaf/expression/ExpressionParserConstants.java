package org.ark.framework.jaf.expression;

/**
 * @class org.ark.framework.jaf.expression.ExpressionParserConstants
 * @author Darkness
 * @date 2013-1-31 下午12:47:05
 * @version V1.0
 */
public interface ExpressionParserConstants {

	public static final int EOF = 0;

	public static final int AND = 5;

	public static final int OR = 6;

	public static final int NOT = 7;

	public static final int PLUS = 8;

	public static final int MINUS = 9;

	public static final int DIVIDE = 10;

	public static final int MULTIPLY = 11;

	public static final int MOD = 12;

	public static final int EQ = 13;

	public static final int NE = 14;

	public static final int GT = 15;

	public static final int LT = 16;

	public static final int GE = 17;

	public static final int LE = 18;

	public static final int COLON = 19;

	public static final int COMMA = 20;

	public static final int DOT = 21;

	public static final int LPAR = 22;

	public static final int RPAR = 23;

	public static final int TRUE = 24;

	public static final int FALSE = 25;

	public static final int NULL = 26;

	public static final int INT = 27;

	public static final int FLOAT = 28;

	public static final int EXPONENT = 29;

	public static final int STRING = 30;

	public static final int BADLY_ESCAPED_STRING_LITERAL = 31;

	public static final int HOLDER = 32;

	public static final int ID = 33;

	public static final int WORD = 34;

	public static final int DEFAULT = 0;

	public static final String tokenImage[] = { "<EOF>", "\" \"", "\"\\r\"", "\"\\t\"", "\"\\n\"", "\"&&\"", "\"||\"",
			"\"!\"", "\"+\"", "\"-\"", "\"/\"", "\"*\"", "\"%\"", "\"==\"", "\"!=\"", "\">\"", "\"<\"", "\">=\"",
			"\"<=\"", "\":\"", "\",\"", "\".\"", "\"(\"", "\")\"", "\"true\"", "\"false\"", "\"null\"", "<INT>",
			"<FLOAT>", "<EXPONENT>", "<STRING>", "<BADLY_ESCAPED_STRING_LITERAL>", "<HOLDER>", "<ID>", "<WORD>" };

}
