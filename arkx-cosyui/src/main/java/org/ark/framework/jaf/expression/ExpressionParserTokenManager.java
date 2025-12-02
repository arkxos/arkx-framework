package org.ark.framework.jaf.expression;

import java.io.IOException;
import java.io.PrintStream;

import io.arkx.framework.commons.util.LogUtil;

/**
 * @class org.ark.framework.jaf.expression.ExpressionParserTokenManager
 * @author Darkness
 * @date 2013-1-31 下午12:47:15
 * @version V1.0
 */
public class ExpressionParserTokenManager implements ExpressionParserConstants {

	public void setDebugStream(PrintStream ds) {
		debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0) {
		switch (pos) {
			case 0: // '\0'
				if ((active0 & 0x7000000L) != 0L) {
					jjmatchedKind = 33;
					return 20;
				}
				return (active0 & 0x200000L) == 0L ? -1 : 1;

			case 1: // '\001'
				if ((active0 & 0x7000000L) != 0L) {
					jjmatchedKind = 33;
					jjmatchedPos = 1;
					return 20;
				}
				else {
					return -1;
				}

			case 2: // '\002'
				if ((active0 & 0x7000000L) != 0L) {
					jjmatchedKind = 33;
					jjmatchedPos = 2;
					return 20;
				}
				else {
					return -1;
				}

			case 3: // '\003'
				if ((active0 & 0x2000000L) != 0L) {
					jjmatchedKind = 33;
					jjmatchedPos = 3;
					return 20;
				}
				return (active0 & 0x5000000L) == 0L ? -1 : 20;
		}
		return -1;
	}

	private final int jjStartNfa_0(int pos, long active0) {
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
	}

	private int jjStopAtPos(int pos, int kind) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private int jjMoveStringLiteralDfa0_0() {

		switch (curChar) {
			case 33: // '!'
				LogUtil.debug("匹配表达式符合:'!'");
				jjmatchedKind = NOT;
				return jjMoveStringLiteralDfa1_0(16384L);

			case 37: // '%'
				return jjStopAtPos(0, 12);

			case 38: // '&'
				return jjMoveStringLiteralDfa1_0(32L);

			case 40: // '('
				return jjStopAtPos(0, 22);

			case 41: // ')'
				return jjStopAtPos(0, 23);

			case 42: // '*'
				return jjStopAtPos(0, 11);

			case 43: // '+'
				return jjStopAtPos(0, 8);

			case 44: // ','
				return jjStopAtPos(0, 20);

			case 45: // '-'
				return jjStopAtPos(0, 9);

			case 46: // '.'
				return jjStartNfaWithStates_0(0, 21, 1);

			case 47: // '/'
				return jjStopAtPos(0, 10);

			case 58: // ':'
				return jjStopAtPos(0, 19);

			case 60: // '<'
				jjmatchedKind = 16;
				return jjMoveStringLiteralDfa1_0(0x40000L);

			case 61: // '='
				return jjMoveStringLiteralDfa1_0(8192L);

			case 62: // '>'
				jjmatchedKind = 15;
				return jjMoveStringLiteralDfa1_0(0x20000L);

			case 102: // 'f'
				return jjMoveStringLiteralDfa1_0(0x2000000L);

			case 110: // 'n'
				return jjMoveStringLiteralDfa1_0(0x4000000L);

			case 116: // 't'
				return jjMoveStringLiteralDfa1_0(0x1000000L);

			case 124: // '|'
				return jjMoveStringLiteralDfa1_0(64L);
		}

		return jjMoveNfa_0(0, 0);
	}

	private int jjMoveStringLiteralDfa1_0(long active0) {
		try {
			curChar = input_stream.readChar();
		}
		catch (IOException e) {
			jjStopStringLiteralDfa_0(0, active0);
			return 1;
		}
		switch (curChar) {
			default:
				break;

			case 38: // '&'
				if ((active0 & 32L) != 0L)
					return jjStopAtPos(1, 5);
				break;

			case 61: // '='

				LogUtil.debug("匹配表达式符合:'!='");

				if ((active0 & 8192L) != 0L)
					return jjStopAtPos(1, 13);
				if ((active0 & 16384L) != 0L)
					return jjStopAtPos(1, 14);
				if ((active0 & 0x20000L) != 0L)
					return jjStopAtPos(1, 17);
				if ((active0 & 0x40000L) != 0L)
					return jjStopAtPos(1, 18);
				break;

			case 97: // 'a'
				return jjMoveStringLiteralDfa2_0(active0, 0x2000000L);

			case 114: // 'r'
				return jjMoveStringLiteralDfa2_0(active0, 0x1000000L);

			case 117: // 'u'
				return jjMoveStringLiteralDfa2_0(active0, 0x4000000L);

			case 124: // '|'
				if ((active0 & 64L) != 0L)
					return jjStopAtPos(1, 6);
				break;
		}
		return jjStartNfa_0(0, active0);
	}

	private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
		if ((active0 &= old0) == 0L)
			return jjStartNfa_0(0, old0);
		try {
			curChar = input_stream.readChar();
		}
		catch (IOException e) {
			jjStopStringLiteralDfa_0(1, active0);
			return 2;
		}
		switch (curChar) {
			case 108: // 'l'
				return jjMoveStringLiteralDfa3_0(active0, 0x6000000L);

			case 117: // 'u'
				return jjMoveStringLiteralDfa3_0(active0, 0x1000000L);
		}
		return jjStartNfa_0(1, active0);
	}

	private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
		if ((active0 &= old0) == 0L)
			return jjStartNfa_0(1, old0);
		try {
			curChar = input_stream.readChar();
		}
		catch (IOException e) {
			jjStopStringLiteralDfa_0(2, active0);
			return 3;
		}
		switch (curChar) {
			default:
				break;

			case 101: // 'e'
				if ((active0 & 0x1000000L) != 0L)
					return jjStartNfaWithStates_0(3, 24, 20);
				break;

			case 108: // 'l'
				if ((active0 & 0x4000000L) != 0L)
					return jjStartNfaWithStates_0(3, 26, 20);
				break;

			case 115: // 's'
				return jjMoveStringLiteralDfa4_0(active0, 0x2000000L);
		}
		return jjStartNfa_0(2, active0);
	}

	private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
		if ((active0 &= old0) == 0L)
			return jjStartNfa_0(2, old0);
		try {
			curChar = input_stream.readChar();
		}
		catch (IOException e) {
			jjStopStringLiteralDfa_0(3, active0);
			return 4;
		}
		switch (curChar) {
			case 101: // 'e'
				if ((active0 & 0x2000000L) != 0L)
					return jjStartNfaWithStates_0(4, 25, 20);
				break;
		}
		return jjStartNfa_0(3, active0);
	}

	private int jjStartNfaWithStates_0(int pos, int kind, int state) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		try {
			curChar = input_stream.readChar();
		}
		catch (IOException e) {
			return pos + 1;
		}
		return jjMoveNfa_0(state, pos + 1);
	}

	private static final int Left_Brace = 9;// {

	private int jjMoveNfa_0(int startState, int curPos) {

		int startsAt = 0;
		jjnewStateCnt = 45;
		int i = 1;
		jjstateSet[0] = startState;
		int kind = Integer.MAX_VALUE;

		do {
			if (++jjround == Integer.MAX_VALUE)
				ReInitRounds();

			if (curChar < '@') {
				long l = 1L << curChar;
				do
					switch (jjstateSet[--i]) {
						case 0: // '\0'
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 27)
									kind = 27;
								jjCheckNAddStates(0, 4);
							}
							else if (curChar == '\'')
								jjCheckNAddStates(5, 9);
							else if (curChar == '$') {
								if (kind > ID)
									kind = ID;
								jjCheckNAdd(20);
							}
							else if (curChar == '"')
								jjCheckNAddTwoStates(6, 7);
							else if (curChar == '.')
								jjCheckNAdd(1);
							if (curChar == '$')
								jjstateSet[jjnewStateCnt++] = Left_Brace;
							break;

						case 1: // '\001'
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 28)
									kind = 28;
								jjCheckNAddTwoStates(1, 2);
							}
							break;

						case 3: // '\003'
							if ((0x280000000000L & l) != 0L)
								jjCheckNAdd(4);
							break;

						case 4: // '\004'
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 28)
									kind = 28;
								jjCheckNAdd(4);
							}
							break;

						case 5: // '\005'
							if (curChar == '"')
								jjCheckNAddTwoStates(6, 7);
							break;

						case 6: // '\006'
							if ((0xfffffffbffffffffL & l) != 0L)
								jjCheckNAddTwoStates(6, 7);
							break;

						case 8: // '\b'
							if ((0xfffffffbffffffffL & l) != 0L && kind > 31)
								kind = 31;
							break;

						case 10: // '\n'
							if ((0x3ff400000000000L & l) != 0L)
								jjCheckNAddStates(10, 13);
							break;

						case 11: // '\013'
							if (curChar == '.')
								jjCheckNAdd(12);
							break;

						case 12: // '\f'
							if ((0x3ff400000000000L & l) != 0L)
								jjCheckNAddStates(14, 16);
							break;

						case 14: // '\016'
							if ((0x3ff400000000000L & l) != 0L)
								jjCheckNAddTwoStates(14, 15);
							break;

						case 15: // '\017'
							if (curChar == '=')
								jjCheckNAdd(16);
							break;

						case 16: // '\020'
							if ((0xdfffffffffffffffL & l) != 0L)
								jjCheckNAddStates(17, 19);
							break;

						case 18: // '\022'
							if (curChar == '$')
								jjstateSet[jjnewStateCnt++] = 9;
							break;

						case 19: // '\023'
							if (curChar == '$') {
								if (kind > 33)
									kind = 33;
								jjCheckNAdd(20);
							}
							break;

						case 20: // '\024'
							if ((0x3ff400000000000L & l) != 0L) {
								if (kind > 33)
									kind = 33;
								jjCheckNAdd(20);
							}
							break;

						case 21: // '\025'
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 27)
									kind = 27;
								jjCheckNAddStates(0, 4);
							}
							break;

						case 22: // '\026'
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 27)
									kind = 27;
								jjCheckNAdd(22);
							}
							break;

						case 23: // '\027'
							if ((0x3ff000000000000L & l) != 0L)
								jjCheckNAddTwoStates(23, 24);
							break;

						case 24: // '\030'
							if (curChar == '.') {
								if (kind > 28)
									kind = 28;
								jjCheckNAddTwoStates(25, 26);
							}
							break;

						case 25: // '\031'
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 28)
									kind = 28;
								jjCheckNAddTwoStates(25, 26);
							}
							break;

						case 27: // '\033'
							if ((0x280000000000L & l) != 0L)
								jjCheckNAdd(28);
							break;

						case 28: // '\034'
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 28)
									kind = 28;
								jjCheckNAdd(28);
							}
							break;

						case 29: // '\035'
							if ((0x3ff000000000000L & l) != 0L)
								jjCheckNAddTwoStates(29, 30);
							break;

						case 31: // '\037'
							if ((0x280000000000L & l) != 0L)
								jjCheckNAdd(32);
							break;

						case 32: // ' '
							if ((0x3ff000000000000L & l) != 0L) {
								if (kind > 28)
									kind = 28;
								jjCheckNAdd(32);
							}
							break;

						case 33: // '!'
							if (curChar == '\'')
								jjCheckNAddStates(5, 9);
							break;

						case 34: // '"'
							if ((0xffffff7bffffdbffL & l) != 0L)
								jjCheckNAddStates(20, 22);
							break;

						case 36: // '$'
							if ((0x8400000000L & l) != 0L)
								jjCheckNAddStates(20, 22);
							break;

						case 37: // '%'
							if (curChar == '\'' && kind > 30)
								kind = 30;
							break;

						case 38: // '&'
							if ((0xff000000000000L & l) != 0L)
								jjCheckNAddStates(23, 26);
							break;

						case 39: // '\''
							if ((0xff000000000000L & l) != 0L)
								jjCheckNAddStates(20, 22);
							break;

						case 40: // '('
							if ((0xf000000000000L & l) != 0L)
								jjstateSet[jjnewStateCnt++] = 41;
							break;

						case 41: // ')'
							if ((0xff000000000000L & l) != 0L)
								jjCheckNAdd(39);
							break;

						case 42: // '*'
							if ((0xffffff7fffffffffL & l) != 0L)
								jjCheckNAddTwoStates(42, 43);
							break;

						case 44: // ','
							if ((0xffffff7fffffffffL & l) != 0L && kind > 31)
								kind = 31;
							break;
					}
				while (i != startsAt);
			}
			else if (curChar < '\200') {
				long l = 1L << (curChar & 0x3f);
				do
					switch (jjstateSet[--i]) {
						case 0: // '\0'
						case 20: // '\024'
							if ((0x7fffffe87fffffeL & l) != 0L) {
								if (kind > 33)
									kind = 33;
								jjCheckNAdd(20);
							}
							break;

						case 2: // '\002'
							if ((0x2000000020L & l) != 0L)
								jjAddStates(27, 28);
							break;

						case 6: // '\006'
							if ((0xffffffffefffffffL & l) != 0L)
								jjAddStates(29, 30);
							break;

						case 7: // '\007'
							if (curChar == '\\')
								jjstateSet[jjnewStateCnt++] = 8;
							break;

						case 8: // '\b'
						case 44: // ','
							if ((0xffffffffefffffffL & l) != 0L && kind > 31)
								kind = 31;
							break;

						case Left_Brace: // '\t'
							if (curChar == '{')
								jjCheckNAdd(10);
							break;

						case 10: // '\n'
							if ((0x7fffffe87fffffeL & l) != 0L)
								jjCheckNAddStates(10, 13);
							break;

						case 12: // '\f'
							if ((0x7fffffe87fffffeL & l) != 0L)
								jjCheckNAddStates(14, 16);
							break;

						case 13: // '\r'
							if (curChar == '|')
								jjCheckNAdd(14);
							break;

						case 14: // '\016'
							if ((0x7fffffe87fffffeL & l) != 0L)
								jjCheckNAddTwoStates(14, 15);
							break;

						case 16: // '\020'
							if ((0xdfffffffffffffffL & l) != 0L)
								jjCheckNAddStates(17, 19);
							break;

						case 17: // '\021'
							if (curChar == '}' && kind > HOLDER)
								kind = HOLDER;
							break;

						case 26: // '\032'
							if ((0x2000000020L & l) != 0L)
								jjAddStates(31, 32);
							break;

						case 30: // '\036'
							if ((0x2000000020L & l) != 0L)
								jjAddStates(33, 34);
							break;

						case 34: // '"'
							if ((0xffffffffefffffffL & l) != 0L)
								jjCheckNAddStates(20, 22);
							break;

						case 35: // '#'
							if (curChar == '\\')
								jjAddStates(35, 37);
							break;

						case 36: // '$'
							if ((0x14404410000000L & l) != 0L)
								jjCheckNAddStates(20, 22);
							break;

						case 42: // '*'
							if ((0xffffffffefffffffL & l) != 0L)
								jjAddStates(38, 39);
							break;

						case 43: // '+'
							if (curChar == '\\')
								jjstateSet[jjnewStateCnt++] = 44;
							break;
					}
				while (i != startsAt);
			}
			else {
				int hiByte = curChar >> 8;
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 0x3f);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 0x3f);
				do
					switch (jjstateSet[--i]) {
						case 6: // '\006'
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
								jjAddStates(29, 30);
							break;

						case 8: // '\b'
						case 44: // ','
							if (jjCanMove_0(hiByte, i1, i2, l1, l2) && kind > 31)
								kind = 31;
							break;

						case 16: // '\020'
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
								jjAddStates(17, 19);
							break;

						case 34: // '"'
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
								jjAddStates(20, 22);
							break;

						case 42: // '*'
							if (jjCanMove_0(hiByte, i1, i2, l1, l2))
								jjAddStates(38, 39);
							break;
					}
				while (i != startsAt);
			}
			if (kind != Integer.MAX_VALUE) {
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = Integer.MAX_VALUE;
			}
			curPos++;
			if ((i = jjnewStateCnt) == (startsAt = 45 - (jjnewStateCnt = startsAt)))
				return curPos;
			try {
				curChar = input_stream.readChar();
			}
			catch (IOException e) {
				return curPos;
			}
		}
		while (true);
	}

	private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
		switch (hiByte) {
			case 0: // '\0'
				return (jjbitVec2[i2] & l2) != 0L;
		}
		return (jjbitVec0[i1] & l1) != 0L;
	}

	public ExpressionParserTokenManager(JavaCharStream stream) {
		debugStream = System.out;
		jjrounds = new int[45];
		jjstateSet = new int[90];
		curLexState = 0;
		defaultLexState = 0;
		input_stream = stream;
	}

	public ExpressionParserTokenManager(JavaCharStream stream, int lexState) {
		this(stream);
		SwitchTo(lexState);
	}

	public void ReInit(JavaCharStream stream) {
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = stream;
		ReInitRounds();
	}

	private void ReInitRounds() {
		jjround = 0x80000001;
		for (int i = 45; i-- > 0;)
			jjrounds[i] = 0x80000000;

	}

	public void ReInit(JavaCharStream stream, int lexState) {
		ReInit(stream);
		SwitchTo(lexState);
	}

	public void SwitchTo(int lexState) {
		if (lexState >= 1 || lexState < 0) {
			throw new TokenMgrError((new StringBuilder("Error: Ignoring invalid lexical state : ")).append(lexState)
				.append(". State unchanged.")
				.toString(), 2);
		}
		else {
			curLexState = lexState;
			return;
		}
	}

	/**
	 * 获取当前匹配的Token
	 *
	 * @author Darkness
	 * @date 2012-9-8 下午4:50:48
	 * @version V1.0
	 */
	protected Token jjFillToken() {
		String im = jjstrLiteralImages[jjmatchedKind];
		String curTokenImage = im != null ? im : input_stream.GetImage();
		int beginLine = input_stream.getBeginLine();
		int beginColumn = input_stream.getBeginColumn();
		int endLine = input_stream.getEndLine();
		int endColumn = input_stream.getEndColumn();
		Token t = Token.newToken(jjmatchedKind, curTokenImage);
		t.beginLine = beginLine;
		t.endLine = endLine;
		t.beginColumn = beginColumn;
		t.endColumn = endColumn;
		return t;
	}

	/**
	 * 获取下一个token
	 *
	 * @author Darkness
	 * @date 2012-9-8 下午6:56:07
	 * @version V1.0
	 */
	public Token getNextToken() {
		int curPos = 0;
		do {
			do {
				try {
					curChar = input_stream.BeginToken();
				}
				catch (IOException e) {
					jjmatchedKind = 0;
					Token matchedToken = jjFillToken();
					return matchedToken;
				}
				try {
					input_stream.backup(0);
					for (; curChar <= ' ' && (0x100002600L & 1L << curChar) != 0L; curChar = input_stream.BeginToken())
						;
					break;
				}
				catch (IOException e1) {
				}
			}
			while (true);

			jjmatchedKind = Integer.MAX_VALUE;
			jjmatchedPos = 0;

			// 读取匹配token
			curPos = jjMoveStringLiteralDfa0_0();

			if (jjmatchedKind != Integer.MAX_VALUE) {
				if (jjmatchedPos + 1 < curPos)
					input_stream.backup(curPos - jjmatchedPos - 1);
				if ((jjtoToken[jjmatchedKind >> 6] & 1L << (jjmatchedKind & 0x3f)) != 0L) {
					Token matchedToken = jjFillToken();

					LogUtil.debug("匹配token：" + matchedToken);

					return matchedToken;
				}
			}
			else {
				if (curChar == '\0') {
					return Token.newToken(EOF);
				}
				int error_line = input_stream.getEndLine();
				int error_column = input_stream.getEndColumn();
				String error_after = null;
				boolean EOFSeen = false;
				try {
					input_stream.readChar();
					input_stream.backup(1);
				}
				catch (IOException e1) {
					EOFSeen = true;
					error_after = curPos > 1 ? input_stream.GetImage() : "";
					if (curChar == '\n' || curChar == '\r') {
						error_line++;
						error_column = 0;
					}
					else {
						error_column++;
					}
				}
				if (!EOFSeen) {
					input_stream.backup(1);
					error_after = curPos > 1 ? input_stream.GetImage() : "";
				}
				throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, 0);
			}
		}
		while (true);
	}

	private void jjCheckNAdd(int state) {
		if (jjrounds[state] != jjround) {
			jjstateSet[jjnewStateCnt++] = state;
			jjrounds[state] = jjround;
		}
	}

	private void jjAddStates(int start, int end) {
		do
			jjstateSet[jjnewStateCnt++] = jjnextStates[start];
		while (start++ != end);
	}

	private void jjCheckNAddTwoStates(int state1, int state2) {
		jjCheckNAdd(state1);
		jjCheckNAdd(state2);
	}

	private void jjCheckNAddStates(int start, int end) {
		do
			jjCheckNAdd(jjnextStates[start]);
		while (start++ != end);
	}

	public PrintStream debugStream;
	static final long jjbitVec0[] = { -2L, -1L, -1L, -1L };
	static final long jjbitVec2[] = { 0, 0, -1L, -1L };
	static final int jjnextStates[] = { 22, 23, 24, 29, 30, 34, 35, 37, 42, 43, 10, 11, 13, 17, 12, 13, 17, 14, 16, 17,
			34, 35, 37, 34, 35, 39, 37, 3, 4, 6, 7, 27, 28, 31, 32, 36, 38, 40, 42, 43 };

	public static final String[] jjstrLiteralImages = { "", "0", "0", "0", "0", "&&", "||", "!", "+", "-", "/", "*",
			"%", "==", "!=", ">", "<", ">=", "<=", ":", ",", ".", "(", ")", "true", "false", "null", null, null, null,
			null, null, null, null, "<STRING>" };

	public static final String lexStateNames[] = { "DEFAULT" };
	static final long jjtoToken[] = { 0x3dfffffe1L };
	static final long jjtoSkip[] = { 30L };

	protected JavaCharStream input_stream;

	private final int jjrounds[];

	private final int jjstateSet[];

	protected char curChar;

	int curLexState;

	int defaultLexState;

	int jjnewStateCnt;

	int jjround;

	int jjmatchedPos;

	int jjmatchedKind;

}
