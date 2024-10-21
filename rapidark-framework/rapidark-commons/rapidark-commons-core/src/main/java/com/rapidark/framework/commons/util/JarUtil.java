package com.rapidark.framework.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;


/**
 * @class org.ark.framework.util.JarUtil
 * @author Darkness
 * @date 2011-10-18 下午05:07:21
 * @version V1.0
 */
public class JarUtil {
	/**
	 * readJar("c:\\test.jar"); 写文件到jar包中 例子中，读取一个文件，并将这个文件存储到jar包中的文件中
	 * 同时新建一个新的文件
	 * 
	 * @param inputFileName
	 * @param outputFileName
	 * @throws Exception
	 */
	public static void readJar(String inputFileName) throws Exception {

		JarInputStream in = new JarInputStream(new FileInputStream(
				inputFileName));
		Manifest manifest = in.getManifest();
		Attributes atts = manifest.getMainAttributes();
		// 输入所有的manifest信息
		Iterator ite = atts.keySet().iterator();
		while (ite.hasNext()) {
			Object key = ite.next();
			System.out.println(key + ":" + atts.getValue(key.toString()));
		}
		ZipEntry entry = null;
		while ((entry = in.getNextEntry()) != null) {
			// 输入每个文件的名称
			System.out.println(entry.getName());
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {// 输出文件内容
//				System.out.println(new String(buffer));
			}
			in.closeEntry();
		}
		in.close();
	}

	/**
	 * writeJar("c:\\1.txt", "c:\\打包.jar"); 写文件到jar包中
	 * 例子中，读取一个文件，并将这个文件存储到jar包中的文件中 同时新建一个新的文件
	 * 
	 * @param inputFileName
	 * @param outputFileName
	 * @throws Exception
	 */
	public static void writeJar(String inputFolderName, String outputFileName)
			throws Exception {
		
		File inputFolder = new File(inputFolderName);
		if (!inputFolder.isDirectory()) {
			throw new RuntimeException("文件" + inputFolderName + "必须是一个目录");
		}
		
		// Mainifest是jar包特有的说明文件，不能通过手动编写实现
		// 它可以帮助你实现META-INF的目录保存了一个叫MANIFEST.MF的文件，记录版本，入口程序等信息
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
		manifest.getMainAttributes().putValue("author", "darkness");
		// JarOutputStream和JarInputStream是jar包生成时特有封装stream
		File outfile = new File(outputFileName);
		JarOutputStream out = new JarOutputStream(new FileOutputStream(outfile), manifest);
		
		List<File> files = FileUtil.getAllFile(inputFolderName);

		for (File file : files) {
			
			/************************ 将输入文件读取写入jar outputstream中 **********************************/
			// JarEntry 是jar包目录类
			String fileName = file.getPath().replace(inputFolderName + "\\", "").replace("\\", "/");
			System.out.println(fileName);
			JarEntry entry = new JarEntry(fileName);
			// 将目录加入到out中
			out.putNextEntry(entry);
			FileInputStream in = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int n = in.read(buffer);
			while (n != -1) {
				out.write(buffer, 0, n);
				n = in.read(buffer);
			}
			in.close();
			out.closeEntry();// 关闭目录
		}

		out.flush();
		// 注意关闭输出文件流
		out.close();
	}

	public static void main(String[] args) throws Exception {
		writeJar("E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\bbs_schema\\classes", "E:\\Projects\\MyEclipse9_32\\JAVA.NET_B_ORM\\schema_jar\\bbs_schema.jar");
	}
}

