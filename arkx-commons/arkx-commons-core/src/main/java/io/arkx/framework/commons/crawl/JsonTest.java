package io.arkx.framework.commons.crawl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * @author Darkness
 * @date 2015-4-21 下午12:19:09
 * @version V1.0
 */
public class JsonTest {

	public final static void main(String[] args) throws Exception {

		String url = "http://xueqiu.com/stock/cata/stocklist.json?page=1&size=30&order=desc&orderby=percent&type=11%2C12&_=1429586302936";

		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
		headers.put("Accept-Encoding", "gzip,deflate,sdch");
		// headers.put("Accept-Language", "zh-CN,zh;q=0.8");
		headers.put("Connection", "keep-alive");
		// headers.put("Accept", "text/*, application/json, or application/xhtml+xml");
		headers.put("Mimetype", "application/json");
		headers.put("charset", "UTF-8");
		headers.put("Host", "xueqiu.com");
		headers.put("Cookie",
				"xq_a_token=9863acf1874182f3de6ea80874b21b2f0d568cd2; xq_r_token=35e0f3cd17d111a2d21f0aa9f857788ba4ee2cc5; __utmt=1; __utma=1.424814075.1429533521.1429533521.1429586295.2; __utmb=1.1.10.1429586295; __utmc=1; __utmz=1.1429533521.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); Hm_lvt_1db88642e346389874251b5a1eded6e3=1429533521,1429586296; Hm_lpvt_1db88642e346389874251b5a1eded6e3=1429586296");// .cookie("SESSIONID",
																																																																																																							// sessionId);

		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
				public void process(final HttpRequest request, final HttpContext context)
						throws HttpException, IOException {
					if (!request.containsHeader("Accept-Encoding")) {
						request.addHeader("Accept", "application/json");
					}
				}

			});

			// httpclient.addResponseInterceptor(new HttpResponseInterceptor() {
			//
			// public void process(
			// final HttpResponse response,
			// final HttpContext context) throws HttpException, IOException {
			// HttpEntity entity = response.getEntity();
			// Header ceheader = entity.getContentEncoding();
			// if (ceheader != null) {
			// HeaderElement[] codecs = ceheader.getElements();
			// for (int i = 0; i < codecs.length; i++) {
			// if (codecs[i].getName().equalsIgnoreCase("gzip")) {
			// response.setEntity(
			// new GzipDecompressingEntity(response.getEntity()));
			// return;
			// }
			// }
			// }
			// }
			//
			// });
			HttpGet httpget = new HttpGet(url);

			for (String key : headers.keySet()) {
				httpget.setHeader(key, headers.get(key));
			}

			// Execute HTTP request
			System.out.println("executing request " + httpget.getURI());
			HttpResponse response = httpclient.execute(httpget);
			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			// System.out.println(response.getLastHeader("Content-Encoding"));
			// System.out.println(response.getLastHeader("Content-Length"));
			System.out.println("----------------------------------------");
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String content = EntityUtils.toString(entity);
				System.out.println(content);
				System.out.println("----------------------------------------");
				// System.out.println("Uncompressed size: "+content.length());
			}
		}
		finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}
	// static class GzipDecompressingEntity extends HttpEntityWrapper {
	//
	// public GzipDecompressingEntity(final HttpEntity entity) {
	// super(entity);
	// }
	//
	// @Override
	// public InputStream getContent()
	// throws IOException, IllegalStateException {
	//
	// // the wrapped entity's getContent() decides about repeatability
	// InputStream wrappedin = wrappedEntity.getContent();
	//
	// return new GZIPInputStream(wrappedin);
	// }
	//
	// @Override
	// public long getContentLength() {
	// // length of ungzipped content is not known
	// return -1;
	// }
	//
	// }

}
