package org.ark.framework.jaf.expression;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ark.framework.jaf.IExpressionContext;
import org.ark.framework.jaf.PlaceHolder;

import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;


/**
 * @class org.ark.framework.jaf.expression.ExpressionParser
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:46:10 
 * @version V1.0
 */
public class ExpressionParser implements ExpressionParserConstants {

	private IExpressionContext context;
	public ExpressionParserTokenManager token_source;
	JavaCharStream jj_input_stream;

	private String condition = "";// 表达式
	
	public Token token;
	public Token jj_nt;
	private int jj_ntk;
	private Token jj_scanpos;
	private Token jj_lastpos;
	private int jj_la;
	private int jj_gen;
	private final int jj_la1[];
	private static int jj_la1_0[];
	private static int jj_la1_1[];
	private final JJCalls jj_2_rtns[];
	private boolean jj_rescan;
	private int jj_gc;
	private final LookaheadSuccess jj_ls;
	private List jj_expentries;
	private int jj_expentry[];
	private int jj_kind;
	private int jj_lasttokens[];
	private int jj_endpos;

	static {
		jj_la1_init_0();
		jj_la1_init_1();
	}
	static final class JJCalls {

		int gen;
		Token first;
		int arg;
		JJCalls next;

		JJCalls() {
		}
	}

	private static final class LookaheadSuccess extends Error {

		private LookaheadSuccess() {
		}

		LookaheadSuccess(LookaheadSuccess lookaheadsuccess) {
			this();
		}
	}

	public void setContext(IExpressionContext context) {
		this.context = context;
	}

	public final Object execute() throws ParseException {
		
		LogUtil.debug("表达式：" + this.condition);
		
		Object value = Expression();
		
		LogUtil.debug("当前值：" + value);
		
		jj_consume_token(0);
		return value;
	}

	public final Object Expression() throws ParseException {
		
		Object value = OR();
		
		LogUtil.debug("当前值：" + value);
		
		label0: do
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			default:
				jj_la1[0] = jj_gen;
				break label0;

			case OR: // '\006'
				jj_consume_token(OR);
				Object i = OR();
				value = Operators.or(value, i);
				break;
			}
		while (true);
		return value;
	}

	public final Object OR() throws ParseException {
		
		Object value = AND();
		
		LogUtil.debug("当前值：" + value);
		
		label0: do
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			default:
				jj_la1[1] = jj_gen;
				break label0;

			case 5: // '\005'
				jj_consume_token(5);
				Object i = AND();
				value = Operators.and(value, i);
				break;
			}
		while (true);
		return value;
	}

	public final Object AND() throws ParseException {
		
		Object value = COMPARE();
		
		LogUtil.debug("当前值：" + value);
		
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
		case 13: // '\r'
		case 14: // '\016'
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 13: // '\r'
			{
				jj_consume_token(13);
				Object i = COMPARE();
				value = Operators.eq(value, i);
				break;
			}

			case 14: // '\016'
			{
				jj_consume_token(14);
				Object i = COMPARE();
				value = Operators.ne(value, i);
				break;
			}

			default: {
				jj_la1[2] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
			}
			break;

		default:
			jj_la1[3] = jj_gen;
			break;
		}
		return value;
	}

	public final Object COMPARE() throws ParseException {
		
		Object value = PlusMinus();
		
		LogUtil.debug("当前值：" + value);
		
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
		case 15: // '\017'
		case 16: // '\020'
		case 17: // '\021'
		case 18: // '\022'
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			case 15: // '\017'
			{
				jj_consume_token(15);
				Object i = PlusMinus();
				value = Operators.gt(value, i);
				break;
			}

			case 17: // '\021'
			{
				jj_consume_token(17);
				Object i = PlusMinus();
				value = Operators.ge(value, i);
				break;
			}

			case 16: // '\020'
			{
				jj_consume_token(16);
				Object i = PlusMinus();
				value = Operators.lt(value, i);
				break;
			}

			case 18: // '\022'
			{
				jj_consume_token(18);
				Object i = PlusMinus();
				value = Operators.le(value, i);
				break;
			}

			default: {
				jj_la1[4] = jj_gen;
				jj_consume_token(-1);
				throw new ParseException();
			}
			}
			break;

		default:
			jj_la1[5] = jj_gen;
			break;
		}
		return value;
	}

	public final Object PlusMinus() throws ParseException {
		
		Object value = MultiplyDivide();
		
		LogUtil.debug("当前值：" + value);
		
		label0: do
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			default:
				jj_la1[6] = jj_gen;
				break label0;

			case 8: // '\b'
			case 9: // '\t'
				switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				case 8: // '\b'
				{
					jj_consume_token(8);
					Object i = MultiplyDivide();
					value = Operators.plus(value, i);
					break;
				}

				case 9: // '\t'
				{
					jj_consume_token(9);
					Object i = MultiplyDivide();
					value = Operators.minus(value, i);
					break;
				}

				default: {
					jj_la1[7] = jj_gen;
					jj_consume_token(-1);
					throw new ParseException();
				}
				}
				break;
			}
		while (true);
		return value;
	}

	public final Object MultiplyDivide() throws ParseException {
		
		Object value = Element();
		
		LogUtil.debug("当前值：" + value);
		
		label0: do
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			default:
				jj_la1[8] = jj_gen;
				break label0;

			case 10: // '\n'
			case 11: // '\013'
			case 12: // '\f'
				switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
				case 11: // '\013'
				{
					jj_consume_token(11);
					Object i = Element();
					value = Operators.multiply(value, i);
					break;
				}

				case 10: // '\n'
				{
					jj_consume_token(10);
					Object i = Element();
					value = Operators.divide(value, i);
					break;
				}

				case 12: // '\f'
				{
					jj_consume_token(12);
					Object i = Element();
					value = Operators.mod(value, i);
					break;
				}

				default: {
					jj_la1[9] = jj_gen;
					jj_consume_token(-1);
					throw new ParseException();
				}
				}
				break;
			}
		while (true);
		return value;
	}

	public final Object Element() throws ParseException {
		
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
		case INT: // '\033'
		{
			Token t = jj_consume_token(INT);
			return new Long(t.image);
		}

		case FLOAT: // '\034'
		{
			Token t = jj_consume_token(FLOAT);
			return new Double(t.image);
		}

		case TRUE: // '\030'
		{
			Token t = jj_consume_token(TRUE);
			return Boolean.TRUE;
		}

		case FALSE: // '\031'
		{
			Token t = jj_consume_token(FALSE);
			return Boolean.FALSE;
		}

		case NULL: // '\032'
		{
			Token t = jj_consume_token(NULL);
			return null;
		}

		case STRING: // '\036'
		{
			Token t = jj_consume_token(STRING);
			if(StringUtil.isEmpty(t.image)) {
				return "";
			}
			try {
				return t.image.substring(1, t.image.length() - 1);
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
		}

		case LPAR: // '\026'
		{
			jj_consume_token(LPAR);
			Object value = Expression();
			jj_consume_token(23);
			return value;
		}

		case RPAR: // '\027'
		case EXPONENT: // '\035'
		default: {
			jj_la1[10] = jj_gen;
			break;
		}
		}
		if (jj_2_1(Integer.MAX_VALUE)) {
			Object value = Function();
			return value;
		}
		if (jj_2_2(Integer.MAX_VALUE)) {
			Object value = FunctionNoArgs();
			return value;
		}
		switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
		case 32: // ' '
		{
			Token t = jj_consume_token(32);
			String holder = t.image;
			return context.eval(new PlaceHolder(holder));
		}

		case 9: // '\t'
		{
			jj_consume_token(9);
			Object value = Element();
			return Operators.minus(value);
		}

		case 7: // '\007'
		{
			jj_consume_token(7);
			Object value = Element();
			return Operators.not(value);
		}
		}
		jj_la1[11] = jj_gen;
		jj_consume_token(-1);
		throw new ParseException();
	}

	public final Object Function() throws ParseException {
		Token t = jj_consume_token(HOLDER);
		String holder = t.image;
		Object obj = context.eval(new PlaceHolder(holder));
		jj_consume_token(21);
		t = jj_consume_token(33);
		String method = t.image;
		jj_consume_token(22);
		ArrayList args = Arguments();
		jj_consume_token(23);
		return org.ark.framework.jaf.expression.Function.invoke(obj, method, args);
	}

	public final Object FunctionNoArgs() throws ParseException {
		Token t = jj_consume_token(32);
		String holder = t.image;
		Object obj = context.eval(new PlaceHolder(holder));
		jj_consume_token(21);
		t = jj_consume_token(33);
		String method = t.image;
		jj_consume_token(22);
		jj_consume_token(23);
		return org.ark.framework.jaf.expression.Function.invoke(obj, method, new ArrayList());
	}

	public final ArrayList Arguments() throws ParseException {
		ArrayList list = new ArrayList();
		Object value = Expression();
		list.add(value);
		label0: do
			switch (jj_ntk != -1 ? jj_ntk : jj_ntk()) {
			default:
				jj_la1[12] = jj_gen;
				break label0;

			case 20: // '\024'
				jj_consume_token(20);
				value = Expression();
				list.add(value);
				break;
			}
		while (true);
		return list;
	}

	private boolean jj_2_1(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		
		if(jj_scanpos == null) {
			return false;
		}
		boolean flag;
		try {
			flag = !jj_3_1();
		} catch (LookaheadSuccess ls) {
			jj_save(0, xla);
			return true;
		}
		jj_save(0, xla);
		return flag;
	}

	private boolean jj_2_2(int xla) {
		jj_la = xla;
		jj_lastpos = jj_scanpos = token;
		if(jj_scanpos == null) {
			return false;
		}
		boolean flag;
		try {
			flag = !jj_3_2();
		} catch (LookaheadSuccess ls) {
			jj_save(1, xla);
			return true;
		}
		jj_save(1, xla);
		return flag;
	}

	private boolean jj_3R_43() {
		if (jj_scan_token(12))
			return true;
		return jj_3R_25();
	}

	private boolean jj_3R_11() {
		if (jj_3R_13())
			return true;
		Token xsp = jj_scanpos;
		if (jj_3R_14())
			jj_scanpos = xsp;
		return false;
	}

	private boolean jj_3R_42() {
		if (jj_scan_token(10))
			return true;
		return jj_3R_25();
	}

	private boolean jj_3R_41() {
		if (jj_scan_token(11))
			return true;
		return jj_3R_25();
	}

	private boolean jj_3R_26() {
		Token xsp = jj_scanpos;
		if (jj_3R_41()) {
			jj_scanpos = xsp;
			if (jj_3R_42()) {
				jj_scanpos = xsp;
				if (jj_3R_43())
					return true;
			}
		}
		return false;
	}

	private boolean jj_3R_19() {
		if (jj_3R_25())
			return true;
		
		if(jj_scanpos == null)  {
			return false;
		}
		Token xsp;
		do
			xsp = jj_scanpos;
		while (!jj_3R_26());
		jj_scanpos = xsp;
		return false;
	}

	private boolean jj_3R_12() {
		if (jj_scan_token(5))
			return true;
		return jj_3R_11();
	}

	private boolean jj_3R_44() {
		if (jj_scan_token(32))
			return true;
		if (jj_scan_token(21))
			return true;
		if (jj_scan_token(33))
			return true;
		if (jj_scan_token(22))
			return true;
		if (jj_3R_6())
			return true;
		return jj_scan_token(23);
	}

	private boolean jj_3R_9() {
		if(jj_scanpos == null) {
			return false;
		}
		if (jj_3R_11())
			return true;
		Token xsp;
		do
			xsp = jj_scanpos;
		while (!jj_3R_12());
		jj_scanpos = xsp;
		return false;
	}

	private boolean jj_3R_28() {
		if (jj_scan_token(9))
			return true;
		return jj_3R_19();
	}

	private boolean jj_3R_27() {
		if (jj_scan_token(8))
			return true;
		return jj_3R_19();
	}

	private boolean jj_3R_40() {
		if (jj_scan_token(7))
			return true;
		return jj_3R_25();
	}

	private boolean jj_3R_20() {
		Token xsp = jj_scanpos;
		if (jj_3R_27()) {
			jj_scanpos = xsp;
			if (jj_3R_28())
				return true;
		}
		return false;
	}

	private boolean jj_3R_10() {
		if (jj_scan_token(6))
			return true;
		return jj_3R_9();
	}

	private boolean jj_3R_39() {
		if (jj_scan_token(9))
			return true;
		return jj_3R_25();
	}

	private boolean jj_3R_15() {
		if (jj_3R_19())
			return true;
		Token xsp;
		do
			xsp = jj_scanpos;
		while (!jj_3R_20());
		jj_scanpos = xsp;
		return false;
	}

	private boolean jj_3_2() {
		if (jj_scan_token(32))
			return true;
		if (jj_scan_token(21))
			return true;
		if (jj_scan_token(33))
			return true;
		if (jj_scan_token(22))
			return true;
		return jj_scan_token(23);
	}

	private boolean jj_3R_7() {
		if (jj_3R_9())
			return true;
		if(jj_scanpos == null) {
			return false;
		}
		Token xsp;
		do
			xsp = jj_scanpos;
		while (!jj_3R_10());
		jj_scanpos = xsp;
		return false;
	}

	private boolean jj_3_1() {
		if (jj_scan_token(32))
			return true;
		if (jj_scan_token(21))
			return true;
		if (jj_scan_token(33))
			return true;
		if (jj_scan_token(22))
			return true;
		if (jj_3R_6())
			return true;
		return jj_scan_token(23);
	}

	private boolean jj_3R_38() {
		return jj_scan_token(32);
	}

	private boolean jj_3R_37() {
		return jj_3R_45();
	}

	private boolean jj_3R_24() {
		if (jj_scan_token(18))
			return true;
		return jj_3R_15();
	}

	private boolean jj_3R_36() {
		return jj_3R_44();
	}

	private boolean jj_3R_23() {
		if (jj_scan_token(16))
			return true;
		return jj_3R_15();
	}

	private boolean jj_3R_35() {
		if (jj_scan_token(22))
			return true;
		if (jj_3R_7())
			return true;
		return jj_scan_token(23);
	}

	private boolean jj_3R_22() {
		if (jj_scan_token(17))
			return true;
		return jj_3R_15();
	}

	private boolean jj_3R_34() {
		return jj_scan_token(30);
	}

	private boolean jj_3R_21() {
		if (jj_scan_token(15))
			return true;
		return jj_3R_15();
	}

	private boolean jj_3R_8() {
		if (jj_scan_token(20))
			return true;
		return jj_3R_7();
	}

	private boolean jj_3R_33() {
		return jj_scan_token(26);
	}

	private boolean jj_3R_16() {
		Token xsp = jj_scanpos;
		if (jj_3R_21()) {
			jj_scanpos = xsp;
			if (jj_3R_22()) {
				jj_scanpos = xsp;
				if (jj_3R_23()) {
					jj_scanpos = xsp;
					if (jj_3R_24())
						return true;
				}
			}
		}
		return false;
	}

	private boolean jj_3R_32() {
		return jj_scan_token(25);
	}

	private boolean jj_3R_13() {
		if (jj_3R_15())
			return true;
		Token xsp = jj_scanpos;
		if (jj_3R_16())
			jj_scanpos = xsp;
		return false;
	}

	private boolean jj_3R_6() {
		if (jj_3R_7())
			return true;
		Token xsp;
		if(jj_scanpos == null) {
			return false;
		}
		do
			xsp = jj_scanpos;
		while (!jj_3R_8());
		jj_scanpos = xsp;
		return false;
	}

	private boolean jj_3R_31() {
		return jj_scan_token(24);
	}

	private boolean jj_3R_30() {
		return jj_scan_token(28);
	}

	private boolean jj_3R_25() {
		Token xsp = jj_scanpos;
		if (jj_3R_29()) {
			jj_scanpos = xsp;
			if (jj_3R_30()) {
				jj_scanpos = xsp;
				if (jj_3R_31()) {
					jj_scanpos = xsp;
					if (jj_3R_32()) {
						jj_scanpos = xsp;
						if (jj_3R_33()) {
							jj_scanpos = xsp;
							if (jj_3R_34()) {
								jj_scanpos = xsp;
								if (jj_3R_35()) {
									jj_scanpos = xsp;
									if (jj_3R_36()) {
										jj_scanpos = xsp;
										if (jj_3R_37()) {
											jj_scanpos = xsp;
											if (jj_3R_38()) {
												jj_scanpos = xsp;
												if (jj_3R_39()) {
													jj_scanpos = xsp;
													if (jj_3R_40())
														return true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean jj_3R_29() {
		return jj_scan_token(27);
	}

	private boolean jj_3R_18() {
		if (jj_scan_token(14))
			return true;
		return jj_3R_13();
	}

	private boolean jj_3R_17() {
		if (jj_scan_token(13))
			return true;
		return jj_3R_13();
	}

	private boolean jj_3R_14() {
		Token xsp = jj_scanpos;
		if (jj_3R_17()) {
			jj_scanpos = xsp;
			if (jj_3R_18())
				return true;
		}
		return false;
	}

	private boolean jj_3R_45() {
		if (jj_scan_token(32))
			return true;
		if (jj_scan_token(21))
			return true;
		if (jj_scan_token(33))
			return true;
		if (jj_scan_token(22))
			return true;
		return jj_scan_token(23);
	}

	private static void jj_la1_init_0() {
		jj_la1_0 = (new int[] { 64, 32, 24576, 24576, 0x78000, 0x78000, 768, 768, 7168, 7168, 0x5f400000, 640, 0x100000 });
	}

	private static void jj_la1_init_1() {
		jj_la1_1 = (new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 });
	}

	public ExpressionParser(InputStream stream) {
		this(stream, null);
	}

	public ExpressionParser(InputStream stream, String encoding) {
		jj_la1 = new int[13];
		jj_2_rtns = new JJCalls[2];
		jj_rescan = false;
		jj_gc = 0;
		jj_ls = new LookaheadSuccess(null);
		jj_expentries = new ArrayList();
		jj_kind = -1;
		jj_lasttokens = new int[100];
		try {
			jj_input_stream = new JavaCharStream(stream, encoding, 1, 1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source = new ExpressionParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 13; i++)
			jj_la1[i] = -1;

		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();

	}

	public void ReInit(InputStream stream) {
		ReInit(stream, null);
	}

	public void ReInit(InputStream stream, String encoding) {
		try {
			jj_input_stream.ReInit(stream, encoding, 1, 1);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 13; i++)
			jj_la1[i] = -1;

		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();

	}

	public ExpressionParser(Reader stream) {
		jj_la1 = new int[13];
		jj_2_rtns = new JJCalls[2];
		jj_rescan = false;
		jj_gc = 0;
		jj_ls = new LookaheadSuccess(null);
		jj_expentries = new ArrayList();
		jj_kind = -1;
		jj_lasttokens = new int[100];
		jj_input_stream = new JavaCharStream(stream, 1, 1);
		token_source = new ExpressionParserTokenManager(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 13; i++)
			jj_la1[i] = -1;

		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();

	}

	public void ReInit(Reader stream) {
		jj_input_stream.ReInit(stream, 1, 1);
		token_source.ReInit(jj_input_stream);
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 13; i++)
			jj_la1[i] = -1;

		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();

	}

	public ExpressionParser(ExpressionParserTokenManager tm) {
		jj_la1 = new int[13];
		jj_2_rtns = new JJCalls[2];
		jj_rescan = false;
		jj_gc = 0;
		jj_ls = new LookaheadSuccess(null);
		jj_expentries = new ArrayList();
		jj_kind = -1;
		jj_lasttokens = new int[100];
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 13; i++)
			jj_la1[i] = -1;

		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();

	}

	public ExpressionParser(String condition) {
		this(new StringReader(condition));
		this.condition = condition;
	}

	public void ReInit(ExpressionParserTokenManager tm) {
		token_source = tm;
		token = new Token();
		jj_ntk = -1;
		jj_gen = 0;
		for (int i = 0; i < 13; i++)
			jj_la1[i] = -1;

		for (int i = 0; i < jj_2_rtns.length; i++)
			jj_2_rtns[i] = new JJCalls();

	}

	private Token jj_consume_token(int kind) throws ParseException {
		Token oldToken;
		if(token == null) {
			return null;
		}
		if ((oldToken = token).next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		
		if(token == null) {
			return null;
		}
		if (token.kind == kind) {
			jj_gen++;
			if (++jj_gc > 100) {
				jj_gc = 0;
				for (int i = 0; i < jj_2_rtns.length; i++) {
					for (JJCalls c = jj_2_rtns[i]; c != null; c = c.next)
						if (c.gen < jj_gen)
							c.first = null;

				}

			}
			return token;
		} else {
//			token = oldToken;
//			jj_kind = kind;
//			throw generateParseException();
			return this.token;
		}
	}

	private boolean jj_scan_token(int kind) {
		if(jj_scanpos == null) {
			return false;
		}
		if (jj_scanpos == jj_lastpos) {
			jj_la--;
			if (jj_scanpos.next == null)
				jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
			else
				jj_lastpos = jj_scanpos = jj_scanpos.next;
		} else {
			jj_scanpos = jj_scanpos.next;
		}
		if (jj_rescan) {
			int i = 0;
			Token tok;
			for (tok = token; tok != null && tok != jj_scanpos; tok = tok.next)
				i++;

			if (tok != null)
				jj_add_error_token(kind, i);
		}
		if(jj_scanpos == null) {
			return true;
		}
		if (jj_scanpos.kind != kind)
			return true;
		if (jj_la == 0 && jj_scanpos == jj_lastpos)
			throw jj_ls;
		else
			return false;
	}

	public final Token getNextToken() {
		if (token.next != null)
			token = token.next;
		else
			token = token.next = token_source.getNextToken();
		jj_ntk = -1;
		jj_gen++;
		return token;
	}

	public final Token getToken(int index) {
		Token t = token;
		for (int i = 0; i < index; i++)
			if (t.next != null)
				t = t.next;
			else
				t = t.next = token_source.getNextToken();

		return t;
	}

	private int jj_ntk() {
		if ((jj_nt = token.next) == null)
			return jj_ntk = (token.next = token_source.getNextToken()).kind;
		else
			return jj_ntk = jj_nt.kind;
	}

	private void jj_add_error_token(int kind, int pos) {
		if (pos >= 100)
			return;
		if (pos == jj_endpos + 1)
			jj_lasttokens[jj_endpos++] = kind;
		else if (jj_endpos != 0) {
			jj_expentry = new int[jj_endpos];
			for (int i = 0; i < jj_endpos; i++)
				jj_expentry[i] = jj_lasttokens[i];

			Iterator it = jj_expentries.iterator();
			label0: while (it.hasNext()) {
				int oldentry[] = (int[]) it.next();
				if (oldentry.length != jj_expentry.length)
					continue;
				for (int i = 0; i < jj_expentry.length; i++)
					if (oldentry[i] != jj_expentry[i])
						continue label0;

				jj_expentries.add(jj_expentry);
				break;
			}
			if (pos != 0)
				jj_lasttokens[(jj_endpos = pos) - 1] = kind;
		}
	}

	public ParseException generateParseException() {
		jj_expentries.clear();
		boolean la1tokens[] = new boolean[35];
		if (jj_kind >= 0) {
			la1tokens[jj_kind] = true;
			jj_kind = -1;
		}
		for (int i = 0; i < 13; i++)
			if (jj_la1[i] == jj_gen) {
				for (int j = 0; j < 32; j++) {
					if ((jj_la1_0[i] & 1 << j) != 0)
						la1tokens[j] = true;
					if ((jj_la1_1[i] & 1 << j) != 0)
						la1tokens[32 + j] = true;
				}

			}

		for (int i = 0; i < 35; i++)
			if (la1tokens[i]) {
				jj_expentry = new int[1];
				jj_expentry[0] = i;
				jj_expentries.add(jj_expentry);
			}

		jj_endpos = 0;
		jj_rescan_token();
		jj_add_error_token(0, 0);
		int exptokseq[][] = new int[jj_expentries.size()][];
		for (int i = 0; i < jj_expentries.size(); i++)
			exptokseq[i] = (int[]) jj_expentries.get(i);

		return new ParseException(token, exptokseq, tokenImage);
	}

	public final void enable_tracing() {
	}

	public final void disable_tracing() {
	}

	private void jj_rescan_token() {
		jj_rescan = true;
		for (int i = 0; i < 2; i++)
			try {
				JJCalls p = jj_2_rtns[i];
				do {
					if (p.gen > jj_gen) {
						jj_la = p.arg;
						jj_lastpos = jj_scanpos = p.first;
						switch (i) {
						case 0: // '\0'
							jj_3_1();
							break;

						case 1: // '\001'
							jj_3_2();
							break;
						}
					}
					p = p.next;
				} while (p != null);
			} catch (LookaheadSuccess lookaheadsuccess) {
			}

		jj_rescan = false;
	}

	private void jj_save(int index, int xla) {
		JJCalls p;
		for (p = jj_2_rtns[index]; p.gen > jj_gen; p = p.next) {
			if (p.next != null)
				continue;
			p = p.next = new JJCalls();
			break;
		}

		p.gen = (jj_gen + xla) - jj_la;
		p.first = token;
		p.arg = xla;
	}

}
