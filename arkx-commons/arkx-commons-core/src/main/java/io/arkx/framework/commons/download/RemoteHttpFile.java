package io.arkx.framework.commons.download;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.arkx.framework.commons.util.FileUtil;

public class RemoteHttpFile {

    private String url;
    private String fileName;
    private int size;
    private boolean isAcceptRanges;

    private int retryCount = 0;
    private int maxRetryCount = 10;

    public RemoteHttpFile(String url) {
        this.url = url;
        init();
    }

    /*
     * 为一个HttpURLConnection模拟请求头，伪装成一个浏览器发出的请求
     */
    private void setHeader(HttpURLConnection con) {
        con.setRequestProperty("User-Agent",
                "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
        con.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        con.setRequestProperty("Accept-Encoding", "aa");
        con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.setRequestProperty("Keep-Alive", "300");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
        con.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
        con.setRequestProperty("Cache-Control", "max-age=0");
        // con.setRequestProperty("Referer",
        // "http://quotes.money.163.com/trade/lsjysj_002693.html");
    }

    private HttpURLConnection createConnection() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            setHeader(connection);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void init() {
        try {
            HttpURLConnection connection = createConnection();

            connection.setRequestProperty("Range", "bytes=" + 0 + "-");
            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                isAcceptRanges = false;
            } else if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                isAcceptRanges = true;
            } else {
                if (retryCount++ < maxRetryCount) {
                    init();
                } else {
                    throw new RuntimeException("UnSupported response code:" + responseCode);
                }
            }

            String raw = connection.getHeaderField("Content-Disposition");
            if (raw != null && raw.indexOf("=") != -1) {
                String fileName = raw.split("=")[1]; // getting value after '='
                this.fileName = fileName;
            } else {
                // fall back to random generated file name?
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                this.fileName = fileName;
            }

            this.size = connection.getContentLength();

            if (this.size == -1) {
                // System.out.println("========unget file size, url["+url+"], retry " +
                // retryCount);
                if (retryCount++ < maxRetryCount) {
                    init();
                } else {
                    // throw new RuntimeException("UnSupported response code:" + responseCode);
                }
            } else {
                // System.out.println("========get file size, url["+url+"], retry " +
                // retryCount);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getRange(long start, long end, int retryCount) throws Exception {
        try {
            HttpURLConnection connection = createConnection();
            connection.setRequestMethod("GET");
            // 请求服务器下载部分的文件的指定位置
            connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            connection.setConnectTimeout(5000);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
            } else if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
            } else {
                if (retryCount < maxRetryCount) {
                    getRange(start, end, retryCount++);
                } else {
                    throw new RuntimeException("UnSupported response code:" + responseCode);
                }
            }
            InputStream is = connection.getInputStream();// 返回资源
            return is;
        } catch (Exception e) {
            if (retryCount < maxRetryCount) {
                return getRange(start, end, retryCount++);
            } else {
                throw e;
            }
        }
    }

    public boolean download(String path) {
        return download(path, getFileName(), 0, getSize() - 1);
    }

    public boolean download(String path, String fileName) {
        return download(path, fileName, 0, getSize() - 1);
    }

    public boolean download(String path, long startIndex, long endIndex) {
        return download(path, getFileName(), startIndex, endIndex);
    }

    public boolean download(String path, String fileName, long startIndex, long endIndex) {
        try {
            FileUtil.mkdir(path);
            File file = new File(path + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            // 随机写文件的时候从哪个位置开始写
            raf.seek(startIndex);// 定位文件

            InputStream is = getRange(startIndex, endIndex, 5);// 返回资源

            int downloadedLength = 0;
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                raf.write(buffer, 0, len);

                downloadedLength += len;
            }

            is.close();
            raf.close();

            long needDownloadLength = endIndex - startIndex + 1;
            if (downloadedLength != needDownloadLength) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public int getSize() {
        return size;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isAcceptRanges() {
        return isAcceptRanges;
    }

    @Override
    public String toString() {
        String sizeString = "";
        int kb = this.getSize() / 1024;
        if (kb > 1024) {
            sizeString = kb / 1024 + "M";
        } else {
            sizeString = kb + "K";
        }
        String result = "fileName: " + this.getFileName() + ", fileSize[" + getSize() + "]: " + sizeString
                + ", isSupportRange:" + this.isAcceptRanges();
        return result;
    }

    public static void main(String[] args) {
        String url = "http://quotes.money.163.com/service/chddata.html?code=1000991&start=20120731&end=20171016&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
        // String url =
        // "http://quotes.money.163.com/service/chddata.html?code=1002693&start=20120731&end=20171012&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;TURNOVER;VOTURNOVER;VATURNOVER;TCAP;MCAP";
        RemoteHttpFile remoteHttpFile = new RemoteHttpFile(url);
        System.out.println(remoteHttpFile);
    }

}
