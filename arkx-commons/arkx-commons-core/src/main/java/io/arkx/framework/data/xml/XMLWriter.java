package io.arkx.framework.data.xml;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import io.arkx.framework.commons.util.FileUtil;

/**
 * XML写入工具类
 * 
 */
public class XMLWriter {
	/**
	 * 将XML文档写入到目标文件
	 * 
	 * @param doc XML文档
	 * @param f 目标文件
	 */
	public static void writeTo(XMLDocument doc, File f) {
		FileUtil.writeText(f.getAbsolutePath(), doc.toString(), doc.getEncoding());
	}
	
	public static void writeTo(XMLDocument doc, OutputStream os) {
		try {
			os.write(doc.toString().getBytes(doc.getEncoding()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeTo(XMLDocument doc, Writer writer) {
		try {
			writer.write(doc.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
