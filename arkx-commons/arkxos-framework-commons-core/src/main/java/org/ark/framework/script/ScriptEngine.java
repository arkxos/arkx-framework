package org.ark.framework.script;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.LogUtil;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @class org.ark.framework.script.ScriptEngine
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:23:30 
 * @version V1.0
 */
public class ScriptEngine {
	public static final int LANG_JAVASCRIPT = 0;
	public static final int LANG_JAVA = 1;
	private int language;
	private ArrayList<String> carr = new ArrayList();

	private ArrayList<String> parr = new ArrayList();

	private Mapx<String, Object> funcMap = new Mapx();
	private Mapx<String, Object> exceptionMap = new Mapx();

	private Mapx<String, Object> varMap = new Mapx();
	private boolean isNeedCheck;
	private static final Pattern JavaLineInfoPattern = Pattern.compile("error at line (\\d*?)\\, column (\\d*?)\\.", 34);

	public ScriptEngine(int language) {
		this.language = language;
	}

	public void importClass(String className) {
		this.carr.add(className);
	}

	public void importPackage(String pacckageName) {
		this.parr.add(pacckageName);
	}

	public void compileFunction(String funcName, String script) throws EvalException {
		if ((this.isNeedCheck) && (!SecurityChecker.check(script))) {
			EvalException ee = new EvalException("Script can't use forbidden package or class!", "", "", 0, 0);
			this.exceptionMap.put(funcName, ee);
			throw ee;
		}
		this.exceptionMap.remove(funcName);

		StringBuilder sb = new StringBuilder();
		if (this.language == 1) {
			for (int i = 0; i < this.carr.size(); i++) {
				sb.append("import " + (String) this.carr.get(i) + ";\n");
			}
			for (int i = 0; i < this.parr.size(); i++) {
				sb.append("import " + (String) this.parr.get(i) + ".*;\n");
			}
			sb.append(funcName);
			sb.append("(){\n");
			sb.append(script);
			sb.append("}\n");
			Interpreter itp = new Interpreter();
			try {
				itp.eval(sb.toString());
			} catch (EvalError e) {
				e.printStackTrace();
				String message = e.getMessage();
				Matcher m = JavaLineInfoPattern.matcher(message);
				int row = 0;
				int col = 0;
				String lineSource = "";
				if (m.find()) {
					row = Integer.parseInt(m.group(1));
					if (row <= this.carr.size()) {
						message = "Import class failed";
						lineSource = ((String) this.carr.get(row - 1)).toString();
					} else if (row <= this.parr.size()) {
						message = "Import package failed";
						lineSource = ((String) this.parr.get(row - 1)).toString();
					} else {
						row = row - this.carr.size() - this.parr.size() - 1;
						lineSource = script.split("\\n")[(row - 1)];
						col = Integer.parseInt(m.group(2));
					}
				}
				throw new EvalException("Row " + row + " exists error:" + lineSource, message, lineSource, row, col);
			}
			this.funcMap.put(funcName, itp);
		} else {
			for (int i = 0; i < this.carr.size(); i++) {
				sb.append("importClass(Packages.");
				sb.append((String) this.carr.get(i));
				sb.append(");\n");
			}
			for (int i = 0; i < this.parr.size(); i++) {
				sb.append("importPackage(Packages.");
				sb.append((String) this.parr.get(i));
				sb.append(");\n");
			}
			sb.append("function ").append(funcName).append("(){\n");
			sb.append(script);
			sb.append("}\n");
			sb.append(funcName).append("();\n");
			Context ctx = Context.enter();
			ctx.setOptimizationLevel(1);
			Script compiledScript = null;
			try {
				compiledScript = ctx.compileString(sb.toString(), "", 1, null);
			} catch (EvaluatorException e) {
				int row = e.lineNumber() - 1;
				throw new EvalException("Row " + row + " exsits error:" + e.lineSource(), e.getMessage(), e.lineSource(), row, e.columnNumber());
			}
			this.funcMap.put(funcName, compiledScript);
		}
	}

	public Object executeFunction(String funcName) throws EvalException {
		Object ee = this.exceptionMap.get(funcName);
		if (ee != null) {
			throw ((EvalException) ee);
		}
		Object o = this.funcMap.get(funcName);
		if (this.language == 1)
			try {
				Interpreter itp = (Interpreter) o;
				for (String key : this.varMap.keySet()) {
					itp.set(key, this.varMap.get(key));
				}
				return itp.eval(funcName + "();");
			} catch (EvalError e) {
				e.printStackTrace();
				String message = e.getMessage();
				int col = 0;
				int row = e.getErrorLineNumber() - 1;
				throw new EvalException("Row " + row + " exists error:" + e.getErrorText(), message, e.getErrorText(), row, col);
			}
		try {
			Script compiledScript = (Script) o;
			Context ctx = Context.enter();
			ImporterTopLevel scope = new ImporterTopLevel(ctx);
			for (String key : this.varMap.keySet()) {
				ScriptableObject.putProperty(scope, key, this.varMap.get(key));
			}
			return compiledScript.exec(ctx, scope);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setVar(String name, Object value) {
		this.varMap.put(name, value);
	}

	public int getLanguage() {
		return this.language;
	}

	public static Object evalJavaScript(String js) throws EvalException {
		Context ctx = Context.enter();
		ImporterTopLevel scope = new ImporterTopLevel(ctx);
		ctx.setOptimizationLevel(1);
		Script compiledScript = null;
		int row;
		try {
			compiledScript = ctx.compileString(js, "", 1, null);
			return compiledScript.exec(ctx, scope);
		} catch (EvaluatorException e) {
			row = e.lineNumber() - 1;
			throw new EvalException("第" + row + "行有语法错误: " + e.lineSource(), e.getMessage(), e.lineSource(), row, e.columnNumber());
		}
		
	}

	public static Object evalJava(String java) throws EvalException {
		Interpreter itp = new Interpreter();
		String message;
		int row;
		int col;
		String lineSource;
		try {
			return itp.eval(java);
		} catch (EvalError e) {
			message = e.getMessage();
			Matcher m = JavaLineInfoPattern.matcher(message);
			row = 0;
			col = 0;
			lineSource = "";
			if (m.find()) {
				row = Integer.parseInt(m.group(1));
				lineSource = java.split("\\n")[(row - 1)];
				col = Integer.parseInt(m.group(2));
			}
		}
		throw new EvalException("第" + row + "行有语法错误: " + lineSource, message, lineSource, row, col);
	}

	public void exit() {
		if (this.language == 0)
			Context.exit();
	}

	public boolean isNeedCheck() {
		return this.isNeedCheck;
	}

	public void setNeedCheck(boolean isNeedCheck) {
		this.isNeedCheck = isNeedCheck;
	}

	public static void main(String[] args) {
		ScriptEngine se = new ScriptEngine(1);
		se.setNeedCheck(false);
		se.importPackage("org.ark.framework.cache");
		se.importPackage("org.ark.framework.data");
		se.importPackage("org.ark.framework.utility");
		se.importPackage("com.ark.statical");
		se.importPackage("com.ark.cms.template");
		se.importPackage("com.ark.cms.pub");
		se.importPackage("com.ark.cms.site");
		se.importPackage("com.ark.cms.document");
		String script = FileUtil.readText("H:/Script.txt");
		try {
			se.compileFunction("a", script);
			LogUtil.info(se.executeFunction("a"));
		} catch (EvalException e) {
			e.printStackTrace();
		}
	}
}