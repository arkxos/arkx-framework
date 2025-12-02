import io.arkx.framework.commons.util.StringUtil;

public class dd {

	public static void main(String[] args) throws Exception {
		// String folder =
		// "C:\\Users\\Administrator\\git\\rapid-ark-v2\\rapid-ark-web\\src\\main\\webapp\\WEB-INF\\lib"
		// ;
		// File[] children = new File(folder).listFiles();
		// for (File child : children) {
		// System.out.println(child);
		//// ZipUtil.unzip(child.getAbsolutePath(), folder);
		//// JarUtil.readJar(child.getAbsolutePath());
		// ZipUtil.getFileListInZip(child.getAbsolutePath());
		// }

		// System.out.println(LocalDate.parse("2018-08-01").getDayOfWeek().getValue());
		// System.out.println(DayOfWeek.MONDAY.getValue());
		// System.out.println(DateUtil.firstDayOfWeek(LocalDate.parse("2018-08-01")));
		// System.out.println(DateUtil.lastDayOfWeek(LocalDate.parse("2018-08-01")));

		System.out.println("\"hidden\" name=\"subId\" value=\"<%=request.getParameter(\"subId\") %>\"/>"
			.split("<%=request\\.getParameter(\"subId\") %>")[0]);
		System.out
			.println(StringUtil.splitEx("\"hidden\" name=\"subId\" value=\"<%=request.getParameter(\"subId\") %>\"/>",
					"<%=request.getParameter(\"subId\") %>")[0]);
	}

}
