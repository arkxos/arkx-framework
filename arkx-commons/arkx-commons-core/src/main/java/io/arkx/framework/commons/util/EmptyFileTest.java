package io.arkx.framework.commons.util;

import java.io.*;

/**
 * @author Darkness
 * @date 2019-07-10 10:34:36
 * @version V1.0
 */
public class EmptyFileTest {

	public static void main(String[] args) throws IOException {
		// File file = new File("E:\\OneDrive -
		// business\\004-fun\\电视剧\\我的大叔\\[我的大叔][第09集].mp4");
		// System.out.println(file.length());
		//
		// System.out.println(file.getTotalSpace());
		// System.out.println(file.getUsableSpace());
		// System.out.println(file.getFreeSpace());
		//
		// FileInputStream fis = new FileInputStream(file);
		//
		// System.out.println(fis.available()); //结果即是文件自身占用空间大小
		// System.out.println(readByte(fis).length);

		IntegerValue integerValue = new IntegerValue();
		String inputFolderPath = "H:\\OneDrive - business";
		handleFile(inputFolderPath, new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (isOneDriveEmpty(file)) {
					integerValue.add();
					System.out.println(file.getAbsolutePath());
					file.delete();
				}
				return true;
			}
		});

		System.out.println("empty file count: " + integerValue.get());
	}

	public static void handleFile(String inputFolderPath, FileFilter fileFilter) {
		File inputFolder = new File(inputFolderPath);
		if (inputFolder.isDirectory()) {
			File[] files = inputFolder.listFiles();

			for (File file : files) {
				handleFile(file.getPath(), fileFilter);
			}
		}
		else {
			if (fileFilter != null) {
				if (fileFilter.accept(inputFolder)) {
				}
			}
			else {
			}
		}
	}

	public static boolean isOneDriveEmpty(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] data = testIsEmptyByte(fis);
			// if (data.length == 0) {
			// return true;
			// }
			// return false;
		}
		catch (Exception e) {
			if ("云文件提供程序未运行。".equals(e.getMessage())) {
				return true;
			}
			else {
				e.printStackTrace();
			}
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static boolean isEmpty(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] data = readByte(fis);
			if (data.length == 0) {
				return true;
			}
			return false;
		}
		catch (Exception e) {
			if ("云文件提供程序未运行。".equals(e.getMessage())) {
				return true;
			}
			else {
				e.printStackTrace();
			}
		}
		finally {
			if (fis != null) {
				try {
					fis.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static byte[] readByte(InputStream is) {
		byte[] buffer = new byte[8192];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		while (true) {
			int bytesRead = -1;
			try {
				bytesRead = is.read(buffer);
			}
			catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
			if (bytesRead == -1) {
				break;
			}
			try {
				os.write(buffer, 0, bytesRead);
			}
			catch (Exception e) {
				throw new RuntimeException("File.readByte() failed");
			}
		}
		buffer = null;
		byte[] bytes = os.toByteArray();
		if (os != null) {
			try {
				os.close();
			}
			catch (IOException e) {
			}
		}
		return bytes;
	}

	public static byte[] testIsEmptyByte(InputStream is) {
		byte[] buffer = new byte[8192];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		while (true) {
			int bytesRead = -1;
			try {
				bytesRead = is.read(buffer);
			}
			catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
			if (bytesRead == -1) {
				break;
			}
			break;
		}
		buffer = null;
		byte[] bytes = os.toByteArray();
		if (os != null) {
			try {
				os.close();
			}
			catch (IOException e) {
			}
		}
		return bytes;
	}

}
