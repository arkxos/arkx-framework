package io.arkx.framework.commons.crawl;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author Darkness
 * @date 2015-1-9 下午6:27:46
 * @version V1.0
 */
public class HttpClientDownloader {

    public Page download(Request request, Site site) {
        Page page = new Page(request);

        Document doc = null;
        try {
            doc = connect(request);
        } catch (IOException e) {
            if (e instanceof HttpStatusException) {
                HttpStatusException se = (HttpStatusException) e;
                if (se.getStatusCode() == 404) {// 页面不存在
                    return page;
                }
            } // else if(e instanceof SocketTimeoutException) {
            System.out.println(request.getUrl());
            if (request.getRetryCount() <= 3) {
                request.addRetryCount();
                page.addTargetRequest(request);
            }
            // } else {
            // e.printStackTrace();
            // }
        }

        if (doc == null) {
            return page;
        }

        page.setHtml(doc);

        return page;
    }

    private Document connect(Request request) throws IOException {
        Connection connection = Jsoup.connect(request.getUrl());
        if (headers != null) {
            for (String key : headers.keySet()) {
                connection.header(key, headers.get(key));
            }
        }
        return connection.ignoreContentType(true).get();
    }

    String sessionId;

    public void login(String loginUrl, String usernameField, String username, String passwordField, String password) {
        Connection.Response res;
        try {
            res = Jsoup.connect(loginUrl).data(usernameField, username, passwordField, password).method(Method.POST)
                    .execute();

            // Document doc = res.parse();
            // 这儿的SESSIONID需要根据要登录的目标网站设置的session Cookie名字而定
            sessionId = res.cookie("SESSIONID");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getJsonString(String urlPath, Map<String, String> headers) throws Exception {
        URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        for (String key : headers.keySet()) {
            connection.setRequestProperty(key, headers.get(key));
        }
        connection.connect();
        InputStream inputStream = connection.getInputStream();
        // 对应的字符编码转换
        Reader reader = new InputStreamReader(inputStream);// , "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(reader);
        String str = null;
        StringBuffer sb = new StringBuffer();
        while ((str = bufferedReader.readLine()) != null) {
            sb.append(str);
        }
        reader.close();
        connection.disconnect();
        return sb.toString();
    }

    public static void main(String[] args) {
        String url = "http://xueqiu.com/stock/cata/stocklist.json?page=1&size=30&order=desc&orderby=percent&type=11%2C12&_=1429586302936";

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        headers.put("Accept-Encoding", "gzip,deflate,sdch");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8");
        headers.put("Connection", "keep-alive");
        headers.put("Accept", "text/*, application/json, or application/xhtml+xml");
        headers.put("Mimetype", "application/json");
        // headers.put("charset", "UTF-8");
        headers.put("Host", "xueqiu.com");
        headers.put("Cookie",
                "xq_a_token=9863acf1874182f3de6ea80874b21b2f0d568cd2; xq_r_token=35e0f3cd17d111a2d21f0aa9f857788ba4ee2cc5; __utmt=1; __utma=1.424814075.1429533521.1429533521.1429586295.2; __utmb=1.1.10.1429586295; __utmc=1; __utmz=1.1429533521.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_1db88642e346389874251b5a1eded6e3=1429533521,1429586296; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1429586296");// .cookie("SESSIONID",
                                                                                                                                                                                                                                                                                                                                                                                                                            // sessionId);
        // try {
        // String json = getJsonString(url, headers);
        // System.out.println(json);
        // } catch (Exception e1) {
        // e1.printStackTrace();
        // }
        Connection connection = Jsoup.connect(url);
        for (String key : headers.keySet()) {
            connection.header(key, headers.get(key));
        }
        try {
            Document doc = connection.ignoreContentType(true).get();
            String data = doc.body().text();
            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Map<String, String> headers;

    public void headers(Map<String, String> headers) {
        this.headers = headers;
    }

    boolean isJson;

    public void setTypeJson() {
        this.isJson = true;
    }

}
