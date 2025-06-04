package io.arkx.framework.commons.util;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @class org.ark.framework.collection.BufferedRandomAccessFile
 * 有缓存的随机文件读取类
 * 
 * @author Darkness
 * @date 2012-8-6 下午9:57:25 
 * @version V1.0
 */
@Deprecated
public class BufferedRandomAccessFile extends RandomAccessFile {
	
	private String fileName;

	public BufferedRandomAccessFile(String fileName, String mode) throws IOException {
		super(fileName, mode);
		this.fileName = fileName;
	}

	public void delete() {
		FileUtil.delete(this.fileName);
	}
}
