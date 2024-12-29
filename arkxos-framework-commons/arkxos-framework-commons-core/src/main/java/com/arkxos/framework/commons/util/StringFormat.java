package com.arkxos.framework.commons.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 字符串格式化类，类似于参数化SQL，也可以使用{0},{1}指定顺序。<br>
 * 例如：
 * 
 * <pre>
 * StringFormat sf = new StringFormat(&quot;欢迎?于?访问本站.&quot;);
 * sf.add(&quot;wyuch&quot;);
 * sf.add(&quot;2006-10-11&quot;);
 * System.out.println(sf);
 * </pre>
 * 
 * 执行后输出：欢迎wyuch于2006-10-11访问本站.<br>
 * <br>
 */
public class StringFormat {
	private String source;
	private ArrayList<Object> params = new ArrayList<>();

	public StringFormat(String str) {
		source = str;
	}

	public StringFormat(String str, Object... vs) {
		source = str;
		add(vs);
	}

	public static String format(String str, Object... vs) {
		return new StringFormat(str, vs).toString();
	}

	public static String format(String str, Collection<?> c) {// NO_UCD
		StringFormat sf = new StringFormat(str);
		sf.params.addAll(c);
		return sf.toString();
	}

	public void add(Object obj) {
		add(new Object[] { obj });
	}

	public void add(Object... vs) {
		if (vs == null) {
			return;
		}
		for (Object v : vs) {
			if (v == null) {
				params.add(null);
				continue;
			}
			params.add(v);
		}
	}

	@Override
	public String toString() {
		if (params.size() == 0) {
			return source;
		}
		StringBuilder sb = new StringBuilder(source.length() * 2);
		int lastIndex = 0;
		for (int i = 0, j = 0; i < source.length(); i++) {
			char c = source.charAt(i);
			if (c == '?') {
				if (i > 0 && source.charAt(i - 1) == '\\') {
					sb.append(source.substring(lastIndex, i - 1));
					sb.append(c);
					lastIndex = i + 1;
					continue;
				}
				sb.append(source.substring(lastIndex, i));
				if (params.size() >= j) {
					sb.append(params.get(j++));
				} else {
					sb.append("null");
				}
				lastIndex = i + 1;
			} else if (c == '{') {
				if (i > 0 && source.charAt(i - 1) == '\\') {
					sb.append(source.substring(lastIndex, i - 1));
					sb.append(c);
					lastIndex = i + 1;
					continue;
				}
				for (int k = i + 1; k < source.length(); k++) {
					if (!Character.isDigit(source.charAt(k))) {
						if (source.charAt(k) == '}' && k > i + 1) {
							sb.append(source.substring(lastIndex, i));
							int index = Integer.parseInt(source.substring(i + 1, k));
							if (params.size() >= index) {
								sb.append(params.get(index));
							} else {
								sb.append("null");
							}
							lastIndex = k + 1;
						}
						i = k;
						break;
					}
				}
			}
		}
		sb.append(source.substring(lastIndex));
		return sb.toString();
	}
	
	public static void main(String[] args) {
		long t = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			System.out.println(format("c \\?? b ? a ", new Object[] { 1, 2, 3 }));
		}
		
		System.out.println(System.currentTimeMillis() - t);
	}
}
