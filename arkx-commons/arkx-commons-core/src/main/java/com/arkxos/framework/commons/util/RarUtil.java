// Decompiled by DJ v3.12.12.101 Copyright 2016 Atanas Neshkov  Date: 2016/5/16 13:41:16
// Home Page:  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RarUtil.java

package com.arkxos.framework.commons.util;

import java.io.File;
import java.io.FileOutputStream;

import com.arkxos.framework.commons.collection.Mapx;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

public class RarUtil {

	public RarUtil() {
	}

	public static void unrar(String sourceRar, String destDir) throws Exception {
		Archive a;
		FileOutputStream fos;
		a = null;
		fos = null;
		a = new Archive(new File(sourceRar));
		for (FileHeader fh = a.nextFileHeader(); fh != null; fh = a.nextFileHeader())
			if (!fh.isDirectory()) {
				String compressFileName = fh.getFileNameW().trim();
				if (ObjectUtil.empty(compressFileName))
					compressFileName = fh.getFileNameString();
				String destFileName = (new StringBuilder(String.valueOf(destDir))).append("/").append(compressFileName).toString();
				destFileName = FileUtil.normalizePath(destFileName);
				String destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));
				File dir = new File(destDirName);
				if (!dir.exists() || !dir.isDirectory())
					dir.mkdirs();
				fos = new FileOutputStream(new File(destFileName));
				a.extractFile(fh, fos);
				fos.close();
				fos = null;
			}

		a.close();
		a = null;
		if (fos != null)
			try {
				fos.close();
				fos = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (a != null)
			try {
				a.close();
				a = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (fos != null)
			try {
				fos.close();
				fos = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (a != null)
			try {
				a.close();
				a = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return;
	}

	public static Mapx getFileListInRar(String rarFileName) throws Exception {
		Mapx map;
		Archive a;
		map = new Mapx();
		a = null;
		a = new Archive(new File(rarFileName));
		for (FileHeader fh = a.nextFileHeader(); fh != null; fh = a.nextFileHeader())
			if (!fh.isDirectory()) {
				String compressFileName = fh.getFileNameW().trim();
				if (ObjectUtil.empty(compressFileName))
					compressFileName = fh.getFileNameString();
				map.put(compressFileName, Long.valueOf(fh.getUnpSize()));
			}

		a.close();
		a = null;
		if (a != null)
			try {
				a.close();
				a = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (a != null)
			try {
				a.close();
				a = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return map;
	}

	private static byte[] readFileInRar(String rarFileName, String fileName) throws Exception {
		return null;
		// Archive a = null;
		// FileHeader fh;
		// a = new Archive(new File(rarFileName));
		// fh = a.nextFileHeader();
		// goto _L1
		// _L3:
		// byte abyte0[];
		// if(fh.isDirectory())
		// continue; /* Loop/switch isn't completed */
		// String compressFileName = fh.getFileNameW().trim();
		// if(ObjectUtil.empty(compressFileName))
		// compressFileName = fh.getFileNameString();
		// if(!compressFileName.equals(fileName))
		// continue; /* Loop/switch isn't completed */
		// ByteArrayOutputStream out = new ByteArrayOutputStream();
		// a.extractFile(fh, out);
		// out.close();
		// abyte0 = out.toByteArray();
		// if(a != null)
		// try
		// {
		// a.close();
		// a = null;
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
		// return abyte0;
		// fh = a.nextFileHeader();
		// _L1:
		// if(fh != null) goto _L3; else goto _L2
		// _L2:
		// a.close();
		// a = null;
		// break MISSING_BLOCK_LABEL_159;
		// Exception exception;
		// exception;
		// if(a != null)
		// try
		// {
		// a.close();
		// a = null;
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
		// throw exception;
		// if(a != null)
		// try
		// {
		// a.close();
		// a = null;
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
		// return null;
	}
}
