import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.StringUtil;

/**
 * 
 * @author darkness
 * @date 2018-09-17 16:21:19
 * @version 1.0
 * @since 4.0
 */
public class RequestGetParam2OutTransformor {
	public static final Pattern PatternField = Pattern.compile("<%=(\\s*request.getParameter\\(\"([\\w\\.\\s]+?)\"\\)\\s*)%>");
	private static int totalCount = 0;

	public static void main2(String[] args) {
		//<%=request.getParameter("WorkID")%>
		String content = "<input type=\"hidden\" name=\"subId\" value=\"<%=request.getParameter(\"subId\") %>\"/>";
		Matcher matcher = PatternField.matcher(content);
		int lastEndIndex = 0;
		while (matcher.find(lastEndIndex)) {
			System.out.println("=" + matcher.group(1) + "=");
			String reg = "";//"<%=\\s*request.getParameter(\""+matcher.group(1).trim()+"\")\\s*%>";
			System.out.println(reg);
			reg = "<%="+matcher.group(1)+"%>";
			System.out.println(content.replace(reg, "<c:out:/>"));
			lastEndIndex = matcher.end();
		}
		
	}
	
	public static void main(String[] args) {
		String folder = "D:\\svn\\source_test";

		// System.err.println("var auditFlag =
		// \"${param.auditFlag}\";".split("\\$\\{param.auditFlag\\}").length);
		//
		// if(true) {
		// return;
		// }
		List<File> jspFiles = FileUtil.listFiles(folder, new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".jsp");
			}
		});

		int index = 0;
		for (File file : jspFiles) {
			String fixedHtml = fixedHtml(file);
			if (StringUtil.isEmpty(fixedHtml)) {
				continue;
			}
			FileUtil.writeText(file.getAbsolutePath(), fixedHtml);
			System.out.println("[" + index++ + "/" + jspFiles.size() + "], fix:" + totalCount);
		}

//		 String fixedHtml = fixedHtml(new File("D:\\svn\\source_test\\cmis\\ep\\WebContent\\jsp\\tree\\ctrlrole\\selectadmin.jsp"));
//		 System.out.println(fixedHtml);
	}

	private static String fixedHtml(File file) {
		// String file =
		// "D:\\svn\\source_test\\cmis\\jlsc\\WebRoot\\jsp\\case_manage\\accept_check_list.jsp";
		String html = FileUtil.readText(file);
		// System.out.println(html);
		List<String> params = extractParam(html);
		totalCount += params.size();

		Set<String> paramsSet = new HashSet<>(params);

		Map<String, String> replacedParams = new HashMap<>();
		for (String placeHolder : paramsSet) {
			if (placeHolder.indexOf("=") != -1) {
				System.out.println(placeHolder);
			}
			String key = placeHolder.replace("request.getParameter(\"", "").replace("\")", "").trim();
			replacedParams.put(placeHolder, "<c:out value=\"${" + key + "}\" />");
		}

		String resultHtml = replaceParam(html, replacedParams);
		return resultHtml;
	}

	public static List<String> extractParam(String content) {
		List<String> result = new ArrayList<String>();

		if (StringUtil.isEmpty(content)) {
			return result;
		}

		Matcher matcher = PatternField.matcher(content);
		int lastEndIndex = 0;
		while (matcher.find(lastEndIndex)) {
			result.add(matcher.group(1));
			lastEndIndex = matcher.end();
		}

		return result;
	}
	
	

	public static String replaceParam(String content, Map<String, String> paramValues) {
		List<String> params = extractParam(content);
		Set<String> paramsSet = new HashSet<>(params);
		for (String param : paramsSet) {
			content = replaceAllIgnoreCase(content, "<%="+ param +"%>", paramValues.get(param));
		}

		return content;
	}

	public static String replaceEx(String str, String subStr, String reStr) {
		if (str == null || str.length() == 0 || reStr == null) {
			return str;
		}
		if (subStr == null || subStr.length() == 0 || subStr.length() > str.length()) {
			return str;
		}
		StringBuilder sb = null;
		int lastIndex = 0;
		while (true) {
			int index = str.indexOf(subStr, lastIndex);
			if (index < 0) {
				break;
			} else {
				if (sb == null) {
					sb = new StringBuilder();
				}
				sb.append(str.substring(lastIndex, index));
				sb.append(reStr);
			}
			lastIndex = index + subStr.length();
		}
		if (lastIndex == 0) {
			return str;
		}
		sb.append(str.substring(lastIndex));
		return sb.toString();
	}

	public static String replaceAllIgnoreCase(String source, String oldstring, String newstring) {
		String[] parts = StringUtil.splitEx(source, oldstring);
		if (parts.length == 0 || parts.length == 1) {
			return source;
		}
		String result = "";
		for (int i = 0; i < parts.length - 1; i++) {
			result += parts[i] + newstring;
		}
		return result + parts[parts.length - 1];
	}

}
