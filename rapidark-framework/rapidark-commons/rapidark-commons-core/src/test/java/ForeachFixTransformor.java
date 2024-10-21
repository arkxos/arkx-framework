
import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rapidark.framework.commons.util.FileUtil;
import com.rapidark.framework.commons.util.StringUtil;

/**
 * 
 * @author darkness
 * @date 2018-09-17 16:21:19
 * @version 1.0
 * @since 4.0
 */
public class ForeachFixTransformor {
//	public static final Pattern PatternField2 = Pattern.compile("c:forEach([\\w\\\.\\s]+?)items=\"<c:out\\svalue=\"${TAG_DICT_RESULT.children}\"\s/>\"");
	//<c:forEach var="trace" items="<c:out value="${pageContext.exception.stackTrace}" />">
	public static final Pattern PatternField = Pattern.compile("<c:forEach([\\s\\S])*?items=\"(([\\s\\S]*?)/>)\"");
//	public static final Pattern PatternField = Pattern.compile("c:forEach([\\w\\.\\s]+?)items=\"<c:out value=\"\\$\\{([\\w\\.\\s]+?)\\} />\"");
//	public static final Pattern PatternField = Pattern.compile("\\$\\{([\\w\\.\\s]+?)\\}");
	private static int totalCount = 0;

	public static void main(String[] args) {
		System.out.println("readfiles");
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

//		 String fixedHtml = fixedHtml(new
//		 File("D:\\svn\\source_test\\cmis\\jlsc\\WebRoot\\jsp\\appeal_manage\\appeal_addorupdatesglr.jsp"));
//		 System.out.println(fixedHtml);
	}

	private static String fixedHtml(File file) {
		// String file =
		// "D:\\svn\\source_test\\cmis\\jlsc\\WebRoot\\jsp\\case_manage\\accept_check_list.jsp";
		String html = FileUtil.readText(file);
		
		Set<String> coutSet = new HashSet<>();
		Matcher matcher = PatternField.matcher(html);
		int lastEndIndex = 0;
		while (matcher.find(lastEndIndex)) {
//			System.out.println("[0]" + matcher.group(0).trim());
//			System.out.println("[1]" + matcher.group(1).trim());
//			System.out.println("[2]" + matcher.group(2).trim());
			coutSet.add(matcher.group(2).trim());
			lastEndIndex = matcher.end();
		}
		
		for (String cout : coutSet) {
			String newcout = cout.replace("<c:out value=\"", "").replace("\" />", "");
			html = replaceAllIgnoreCase(html, cout, newcout);
		}
		
		// System.out.println(html);
		return html;
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
