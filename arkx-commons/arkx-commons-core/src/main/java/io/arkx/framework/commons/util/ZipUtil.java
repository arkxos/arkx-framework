package io.arkx.framework.commons.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.preloader.zip.ZipEntry;
import io.arkx.framework.preloader.zip.ZipFile;
import io.arkx.framework.preloader.zip.ZipOutputStream;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

/**
 * ZIP压缩工具类，也可以解压gzip文件
 *
 */
/**
 * @class org.ark.framework.utility.ZipUtil
 * @author Darkness
 * @date 2012-8-5 下午5:16:37
 * @version V1.0
 */
public class ZipUtil {

    /**
     * 以ZIP方式压缩二进制数组
     */
    public static byte[] zip(byte[] bs) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Deflater def = new Deflater();
        DeflaterOutputStream dos = new DeflaterOutputStream(bos, def);
        try {
            dos.write(bs);
            dos.finish();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] r = bos.toByteArray();
        return r;
    }

    /**
     * 将一批文件(文件夹)压缩到一个流里，压缩后的路径以base参数为起点。
     */
    public static void zipBatch(String base, String[] srcFiles, OutputStream destStream) throws Exception {
        File[] files = new File[srcFiles.length];
        for (int i = 0; i < srcFiles.length; i++) {
            files[i] = new File(srcFiles[i]);
        }
        zipBatch(base, files, destStream);
    }

    /**
     * 将一批文件(文件夹)压缩到一个ZIP文件里，压缩后的路径以base参数为起点。
     */
    public static void zipBatch(String base, File[] srcFiles, OutputStream destStream) throws Exception {
        zipBatch(base, srcFiles, destStream, true);
    }

    public static void zipBatch(String base, File[] srcFiles, OutputStream destStream, boolean filterSVN)
            throws Exception {
        ZipOutputStream zos = new ZipOutputStream(destStream);
        try {
            base = FileUtil.normalizePath(base);
            if (!base.endsWith("/")) {
                base += "/";
            }
            for (int k = 0; k < srcFiles.length; k++) {
                if (!srcFiles[k].exists()) {
                    continue;
                }
                List<File> fileList = getSubFiles(srcFiles[k], filterSVN);
                ZipEntry ze = null;
                byte[] buf = new byte[1024];
                int readLen = 0;
                for (int i = 0; i < fileList.size(); i++) {
                    File f = fileList.get(i);
                    // 创建一个ZipEntry，并设置Name和其它的一些属性
                    if (filterSVN) {
                        if (f.getName().equals(".svn") || f.getName().equals(".temp")) {
                            continue;
                        }
                    }
                    // 排除目录重复新建
                    if (f.isDirectory()) {
                        continue;
                    }
                    String name = f.getAbsolutePath();
                    name = FileUtil.normalizePath(name);
                    if (!name.startsWith(base)) {
                        return;
                    }
                    name = name.substring(base.length());
                    ze = new ZipEntry(name);
                    ze.setSize(f.length());
                    ze.setTime(f.lastModified());
                    LogUtil.info("Compressing:" + f.getPath());
                    // 将ZipEntry加到zos中，再写入实际的文件内容
                    if (f.isFile()) {
                        ze.setUnixMode(644);
                        zos.putNextEntry(ze);
                        InputStream is = new BufferedInputStream(new FileInputStream(f));
                        while ((readLen = is.read(buf, 0, 1024)) != -1) {
                            zos.write(buf, 0, readLen);
                        }
                        is.close();
                    } else if (f.isDirectory()) {
                        ze.setUnixMode(755);
                        zos.putNextEntry(ze);
                    }
                }
            }
        } finally {
            zos.close();
        }
    }

    /**
     * 将一批文件压缩到一个流里，压缩后的路径以各文件（文件夹）自身为起点。
     */
    public static void zipBatch(String[] srcFiles, OutputStream destStream) throws Exception {
        File[] files = new File[srcFiles.length];
        for (int i = 0; i < srcFiles.length; i++) {
            files[i] = new File(srcFiles[i]);
        }
        zipBatch(files, destStream);
    }

    /**
     * 以ZIP方式压缩文件
     */
    public static void zip(String srcFile, String destFile, LongTimeTask lt) throws Exception {
        OutputStream os = new FileOutputStream(destFile);
        zip(new File(srcFile), os, lt);
        os.flush();
        os.close();
    }

    /**
     * 以ZIP方式压缩文件或目录
     */
    public static void zip(String srcFile, String destFile) throws Exception {
        OutputStream os = new FileOutputStream(destFile);
        zip(new File(srcFile), os);
        os.flush();
        os.close();
    }

    /**
     * 以ZIP方式压缩整个文件或目录
     */
    public static void zip(File srcFile, OutputStream destStream) throws Exception {
        zip(srcFile, destStream, null);
    }

    /**
     * 以ZIP方式压缩文件或目录，并输出到指定流
     */
    public static void zip(File srcFile, OutputStream destStream, LongTimeTask ltt) throws Exception {
        zipBatch(srcFile.listFiles(), destStream, true);
    }

    /**
     * 将一批文件（文件夹）压缩到一个ZIP文件里，压缩后的路径以各文件（文件夹）自身为起点。
     */
    public static void zipBatch(File[] srcFiles, OutputStream destStream) throws Exception {
        zipBatch(srcFiles, destStream, true);
    }

    /**
     * 压缩多文件
     *
     * @author Darkness
     * @date 2012-9-3 下午4:31:33
     * @version V1.0
     */
    public static void zipBatch(File[] srcFiles, OutputStream destStream, boolean filterSVN) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(destStream);
        for (int k = 0; k < srcFiles.length; k++) {
            if (!srcFiles[k].exists()) {
                continue;
            }
            List<File> fileList = getSubFiles(srcFiles[k], filterSVN);
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            int readLen = 0;
            for (int i = 0; i < fileList.size(); i++) {
                File f = fileList.get(i);
                // 创建一个ZipEntry，并设置Name和其它的一些属性
                ze = new ZipEntry(getAbsFileName(srcFiles[k], f));
                ze.setSize(f.length());
                ze.setTime(f.lastModified());
                LogUtil.info("Compressing:" + f.getPath());
                // 将ZipEntry加到zos中，再写入实际的文件内容
                if (f.isFile()) {
                    ze.setUnixMode(644);// 解决linux乱码
                    zos.putNextEntry(ze);
                    InputStream is = new BufferedInputStream(new FileInputStream(f));
                    while ((readLen = is.read(buf, 0, 1024)) != -1) {
                        zos.write(buf, 0, readLen);
                    }
                    is.close();
                } else if (f.isDirectory()) {
                    ze.setUnixMode(755);// 解决linux乱码
                    zos.putNextEntry(ze);
                }
            }
        }
        zos.close();
    }

    /**
     * 将二进制数组解压缩
     */
    public static byte[] unzip(byte[] bs) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
        bos = new ByteArrayOutputStream();
        Inflater inf = new Inflater();
        InflaterInputStream dis = new InflaterInputStream(bis, inf);
        byte[] buf = new byte[1024];
        int c;
        try {
            while ((c = dis.read(buf)) != -1) {
                bos.write(buf, 0, c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] r = bos.toByteArray();
        return r;
    }

    public static void zipStream(InputStream is, OutputStream os, String fileName) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(os);
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        int readLen = 0;
        ze = new ZipEntry(fileName);
        ze.setTime(System.currentTimeMillis());
        // logger.info("Compressing stream:" + fileName);

        zos.putNextEntry(ze);
        long total = 0L;
        while ((readLen = is.read(buf, 0, 1024)) != -1) {
            zos.write(buf, 0, readLen);
            total += readLen;
        }
        ze.setSize(total);
        zos.flush();
        zos.close();
    }

    public static byte[] gzip(byte[] bs) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            GZIPOutputStream dos = new GZIPOutputStream(bos);
            dos.write(bs);
            dos.finish();
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] r = bos.toByteArray();
        return r;
    }

    /**
     * GZIP解压缩
     */
    public static byte[] ungzip(byte[] bs) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ByteArrayInputStream bis = new ByteArrayInputStream(bs);
        bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int c;
        try {
            GZIPInputStream gis = new GZIPInputStream(bis);
            while ((c = gis.read(buf)) != -1) {
                bos.write(buf, 0, c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] r = bos.toByteArray();
        return r;
    }

    /**
     * 文件解压缩
     */
    public static void unzip(String srcFileName, String destPath) throws Exception {
        ZipFile zipFile = new ZipFile(srcFileName);
        Enumeration<?> e = zipFile.getEntries();
        ZipEntry zipEntry = null;
        new File(destPath).mkdirs();
        while (e.hasMoreElements()) {
            zipEntry = (ZipEntry) e.nextElement();
            LogUtil.info("Uncompressing:" + zipEntry.getName());
            if (zipEntry.isDirectory()) {
                new File(destPath + File.separator + zipEntry.getName()).mkdirs();
            } else {
                File f = new File(destPath + File.separator + zipEntry.getName());
                f.getParentFile().mkdirs();
                InputStream in = zipFile.getInputStream(zipEntry);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                byte[] buf = new byte[1024];
                int c;
                while ((c = in.read(buf)) != -1) {
                    out.write(buf, 0, c);
                }
                out.close();
                in.close();
            }
        }
        zipFile.close();
    }

    /**
     * 获得ZIP文件内的文件清单，键值为文件大小
     */
    public static Mapx<String, Long> getFileListInZip(String zipFileName) throws Exception {
        Mapx<String, Long> map = new Mapx<>();
        try {
            ZipFile zipFile = new ZipFile(zipFileName, "GBK");// 解决歌华乱码问题
            Enumeration<?> e = zipFile.getEntries();

            while (e.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) e.nextElement();
                if (!zipEntry.isDirectory()) {
                    map.put(zipEntry.getName(), zipEntry.getSize());
                }
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 从zip文件中读取一个文件
     *
     * @param zipFileName
     * @param fileName
     * @return
     */
    public static byte[] readFileInZip(String zipFileName, String fileName) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFileName, "GBK");
            Enumeration<?> e = zipFile.getEntries();
            ZipEntry zipEntry = null;
            while (e.hasMoreElements()) {
                zipEntry = (ZipEntry) e.nextElement();
                if (!zipEntry.isDirectory() && zipEntry.getName().equals(fileName)) {
                    InputStream in = zipFile.getInputStream(zipEntry);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int c;
                    while ((c = in.read(buf)) != -1) {
                        out.write(buf, 0, c);
                    }
                    out.close();
                    in.close();
                    return out.toByteArray();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 递归获取一个文件夹下的所有文件及子文件
     */
    static List<File> getSubFiles(File baseDir, boolean filterSVN) {
        List<File> arr = new ArrayList<File>();
        arr.add(baseDir);
        if (baseDir.isDirectory()) {
            File[] tmp = baseDir.listFiles();
            for (File element : tmp) {
                if (element.getName().equals(".svn") || element.getName().equals(".temp")) {
                    continue;
                }
                arr.addAll(getSubFiles(element, filterSVN));
            }
        }
        return arr;
    }

    /**
     * 获取一个文件相对于某个目录的相对路径
     */
    static String getAbsFileName(File baseDir, File realFileName) {
        File real = realFileName;
        File base = baseDir;
        String ret = real.getName();
        if (real.isDirectory()) {
            ret += "/";
        }
        while (true) {
            if (real == base) {
                break;
            }
            real = real.getParentFile();
            if (real == null) {
                break;
            }
            if (real.equals(base)) {
                ret = real.getName() + "/" + ret;
                break;
            } else {
                ret = real.getName() + "/" + ret;
            }
        }
        return ret;
    }

    // ===================================

    public static void zipBatch(byte[][] files, String[] fileNames, OutputStream destStream) throws Exception {

        if (files == null || fileNames == null) {
            return;
        }

        if (files.length != fileNames.length) {
            return;
        }

        ZipOutputStream zos = new ZipOutputStream(destStream);
        for (int k = 0; k < files.length; k++) {
            ZipEntry ze = new ZipEntry(fileNames[k]);
            ze.setSize(files[k].length);
            ze.setTime(System.currentTimeMillis());
            // logger.info("正在压缩: " + fileNames[k]);

            zos.putNextEntry(ze);
            zos.write(files[k]);
        }
        zos.close();
        // logger.info("压缩完毕！");
    }

    public static void zipBatch(String base, String[] srcFiles, String destFile) throws Exception {
        OutputStream os = new FileOutputStream(destFile);
        zipBatch(base, srcFiles, os);
        os.flush();
        os.close();
    }

    public static void zipBatch(String[] srcFiles, String destFile) throws Exception {
        OutputStream os = new FileOutputStream(destFile);
        zipBatch(srcFiles, os);
        os.flush();
        os.close();
    }

    public static void unrar(String sourceRar, String destDir) throws Exception {
        Archive a = null;
        FileOutputStream fos = null;
        try {
            a = new Archive(new File(sourceRar));
            FileHeader fh = a.nextFileHeader();
            while (fh != null) {
                if (!fh.isDirectory()) {
                    String compressFileName = fh.getFileNameW().trim();
                    if (ObjectUtil.empty(compressFileName)) {
                        compressFileName = fh.getFileNameString();
                    }
                    String destFileName = destDir + "/" + compressFileName;
                    destFileName = FileUtil.normalizePath(destFileName);
                    String destDirName = destFileName.substring(0, destFileName.lastIndexOf("/"));

                    File dir = new File(destDirName);
                    if ((!dir.exists()) || (!dir.isDirectory())) {
                        dir.mkdirs();
                    }

                    fos = new FileOutputStream(new File(destFileName));
                    a.extractFile(fh, fos);
                    fos.close();
                    fos = null;
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (a != null)
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * 获取zip包中的所有文件，不包含目录
     *
     * @param zip文件路径名
     * @return 文件信息Map列表，格式为：{文件名：文件大小}
     *
     * @author Darkness
     * @date 2012-8-5 下午5:16:54
     * @version V1.0
     */

    public static Mapx<String, Long> getFileListInRar(String rarFileName) throws Exception {
        Mapx map = new Mapx();
        Archive a = null;
        try {
            a = new Archive(new File(rarFileName));
            FileHeader fh = a.nextFileHeader();
            while (fh != null) {
                if (!fh.isDirectory()) {
                    String compressFileName = fh.getFileNameW().trim();
                    if (ObjectUtil.empty(compressFileName)) {
                        compressFileName = fh.getFileNameString();
                    }
                    map.put(compressFileName, Long.valueOf(fh.getUnpSize()));
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
        } finally {
            if (a != null) {
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static byte[] readFileInRar(String rarFileName, String fileName) throws Exception {
        Archive a = null;
        try {
            a = new Archive(new File(rarFileName));
            FileHeader fh = a.nextFileHeader();
            while (fh != null) {
                if (!fh.isDirectory()) {
                    String compressFileName = fh.getFileNameW().trim();
                    if (ObjectUtil.empty(compressFileName)) {
                        compressFileName = fh.getFileNameString();
                    }
                    if (compressFileName.equals(fileName)) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        a.extractFile(fh, out);
                        out.close();
                        byte[] arrayOfByte = out.toByteArray();
                        return arrayOfByte;
                    }
                }
                fh = a.nextFileHeader();
            }
            a.close();
            a = null;
        } finally {
            if (a != null)
                try {
                    a.close();
                    a = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        if (a != null) {
            try {
                a.close();
                a = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void unzip(String srcFileName, String destPath, boolean isPath) throws Exception {
        if (isPath) {
            unzip(srcFileName, destPath);
        }
        ZipFile zipFile = new ZipFile(srcFileName);
        Enumeration e = zipFile.getEntries();
        ZipEntry zipEntry = null;
        new File(destPath).mkdirs();
        while (e.hasMoreElements()) {
            zipEntry = (ZipEntry) e.nextElement();
            // logger.info("Uncompressing:" + zipEntry.getName());
            if (!zipEntry.isDirectory()) {
                String fileName = zipEntry.getName();
                if (fileName.lastIndexOf("/") != -1) {
                    fileName = fileName.substring(fileName.lastIndexOf("/"));
                }
                File f = new File(destPath + "/" + fileName);
                InputStream in = zipFile.getInputStream(zipEntry);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(f));

                byte[] buf = new byte[1024];
                int c;
                while ((c = in.read(buf)) != -1) {
                    out.write(buf, 0, c);
                }
                out.flush();
                out.close();
                in.close();
            }
        }
        zipFile.close();
    }

    public static void main(String[] args) {
        byte[] bs = FileUtil.readByte("H:/shop.html");
        try {
            for (int i = 0; i < 10000; i++) {
                bs = zip(bs);
                bs = unzip(bs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
