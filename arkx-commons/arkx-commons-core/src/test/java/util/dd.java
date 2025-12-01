package util;

import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.ZipUtil;

import java.io.File;
import java.util.List;

public class dd {

	public static void main(String[] args) throws Exception {
		List<File> files = FileUtil.getAllFile("C:\\Program Files (x86)\\ZvingSoft\\ZCMS2x\\tomcat\\webapps\\zcms\\WEB-INF\\plugins\\lib");
		for (File file : files) {
			ZipUtil.unzip(file.getAbsolutePath(), "C:\\Program Files (x86)\\ZvingSoft\\ZCMS2x\\jar-src\\" + file.getName());
		}
	}
}
