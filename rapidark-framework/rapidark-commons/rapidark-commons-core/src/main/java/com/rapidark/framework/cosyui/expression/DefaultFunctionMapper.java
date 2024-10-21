package com.rapidark.framework.cosyui.expression;

import com.rapidark.framework.commons.collection.CaseIgnoreMapx;
import com.rapidark.framework.cosyui.expression.function.CharWidth;
import com.rapidark.framework.cosyui.expression.function.ClearHtmlTag;
import com.rapidark.framework.cosyui.expression.function.Contains;
import com.rapidark.framework.cosyui.expression.function.ContainsIgnoreCase;
import com.rapidark.framework.cosyui.expression.function.EndsWith;
import com.rapidark.framework.cosyui.expression.function.EscapeXml;
import com.rapidark.framework.cosyui.expression.function.Eval;
import com.rapidark.framework.cosyui.expression.function.Format;
import com.rapidark.framework.cosyui.expression.function.IndexOf;
import com.rapidark.framework.cosyui.expression.function.JavaEncode;
import com.rapidark.framework.cosyui.expression.function.Join;
import com.rapidark.framework.cosyui.expression.function.LastIndexOf;
import com.rapidark.framework.cosyui.expression.function.Length;
import com.rapidark.framework.cosyui.expression.function.Match;
import com.rapidark.framework.cosyui.expression.function.Now;
import com.rapidark.framework.cosyui.expression.function.Replace;
import com.rapidark.framework.cosyui.expression.function.Split;
import com.rapidark.framework.cosyui.expression.function.StartsWith;
import com.rapidark.framework.cosyui.expression.function.Substring;
import com.rapidark.framework.cosyui.expression.function.SubstringAfter;
import com.rapidark.framework.cosyui.expression.function.SubstringBefore;
import com.rapidark.framework.cosyui.expression.function.ToLowerCase;
import com.rapidark.framework.cosyui.expression.function.ToUpperCase;
import com.rapidark.framework.cosyui.expression.function.Trim;
import com.rapidark.framework.cosyui.expression.function.UrlEncode;

/**
 * 默认函数查找器，不区分函数名大小写，已经内置了常用函数
 * 
 */
public class DefaultFunctionMapper implements IFunctionMapper {
	private static DefaultFunctionMapper instance = new DefaultFunctionMapper();

	private CaseIgnoreMapx<String, CaseIgnoreMapx<String, IFunction>> all = new CaseIgnoreMapx<String, CaseIgnoreMapx<String, IFunction>>(
			true);

	/**
	 * @return 唯一实例
	 */
	public static DefaultFunctionMapper getInstance() {
		return instance;
	}

	private DefaultFunctionMapper() {
		registerFunction(new CharWidth());
		registerFunction(new ClearHtmlTag());
		registerFunction(new Contains());
		registerFunction(new ContainsIgnoreCase());
		registerFunction(new EndsWith());
		registerFunction(new StartsWith());
		registerFunction(new EscapeXml());
		registerFunction(new JavaEncode());
		registerFunction(new UrlEncode());
		registerFunction(new Format());
		registerFunction(new IndexOf());
		registerFunction(new Join());
		registerFunction(new Length());
		registerFunction(new Replace());
		registerFunction(new Split());
		registerFunction(new Substring());
		registerFunction(new SubstringAfter());
		registerFunction(new SubstringBefore());
		registerFunction(new ToLowerCase());
		registerFunction(new ToUpperCase());
		registerFunction(new Trim());
		registerFunction(new Match());
		registerFunction(new LastIndexOf());
		registerFunction(new Now());
		registerFunction(new Eval());
	}

	@Override
	public IFunction resolveFunction(String prefix, String name) {
		if (prefix == null) {
			prefix = "";
		}
		CaseIgnoreMapx<String, IFunction> map = all.get(prefix);
		if (map == null) {
			return null;
		}
		return map.get(name);
	}

	@Override
	public void registerFunction(IFunction f) {
		String prefix = f.getFunctionPrefix();
		if (prefix == null) {
			prefix = "";
		}
		CaseIgnoreMapx<String, IFunction> map = all.get(prefix);
		if (map == null) {
			map = new CaseIgnoreMapx<String, IFunction>(true);
			all.put(prefix, map);
		}
		map.put(f.getFunctionName(), f);
	}

}
