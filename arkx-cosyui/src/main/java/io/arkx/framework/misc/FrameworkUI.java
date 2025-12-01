package io.arkx.framework.misc;

import io.arkx.framework.Constant;
import io.arkx.framework.annotation.Priv;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ServletUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.cosyui.web.UIFacade;
import io.arkx.framework.data.db.DataCollection;

import java.util.HashMap;

/**
 * @class org.ark.framework.FrameworkUI
 *  框架内置一些UI方法
 * @author Darkness
 * @date 2013-1-31 下午01:00:44 
 * @version V1.0
 */
public class FrameworkUI extends UIFacade {

	/**
	 * 用于调试模式下收集前台页面的在浏览器端耗费的时间
	 */
	@Priv(login = false)
	public void sendPageCost(String url, String Cost, String ReadyCost) {
//		String url = $V("URL");$V("Cost")$V("ReadyCost")
		url = url.substring(url.indexOf("/", 8));
		int cost = Integer.parseInt(Cost);
		int readyCost = Integer.parseInt(ReadyCost);
		if (cost > 100) {
			LogUtil.info("ClientCost\tReady=" + ReadyCost + "ms" + (readyCost > 1000 ? "!" : "") + "\tLoad=" + Cost + "ms"
					+ (cost > 3000 ? "!" : "") + "\t" + url);
		}
	}

	/**
	 * 调用远程应用上的UI方法
	 * 
	 * @param url 远程应用根地址
	 * @param method UI方法别名
	 * @param request 请求参数
	 * @return 请求结果
	 */
	public static DataCollection callRemoteMethod(String url, String method, RequestData request) {
		if (!url.endsWith("/")) {
			url = StringUtil.concat(url, "/");
		}
		if (!url.startsWith("http://")) {
			url = StringUtil.concat("http://", url);
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Constant.Method, method);
		params.put(Constant.Data, request.toJSON());
		params.put(Constant.URL, url);
		params.put(Constant.DataFormat, "json");
		url = url + "ajax/invoke";
		String result = ServletUtil.postURLContent(url, params, "UTF-8");
		if (result != null) {
			DataCollection dc = new DataCollection();
			dc.parseJSON(result);
			return dc;
		}
		LogUtil.warn("Framework.callRemoteMethod() error,URL=" + url);
		return null;
	}
	
//	public static DataCollection callRemoteMethod(String url, String method, RequestData request) {
//		SimpleHttpConnectionManager cm = new SimpleHttpConnectionManager();
//		HttpConnectionManagerParams hcmp = new HttpConnectionManagerParams();
//		hcmp.setDefaultMaxConnectionsPerHost(1);
//		hcmp.setConnectionTimeout(3000);
//		hcmp.setSoTimeout(3000);
//		cm.setParams(hcmp);
//		HttpClient httpClient = new HttpClient(cm);
//		if (!url.endsWith("/")) {
//			url = url + "/";
//		}
//		for (int i = 0; i < 3;) {
//			try {
//				PostMethod pm = new PostMethod(url + "MainServlet.zhtml");
//				pm.addParameter("_ARK_METHOD", method);
//				pm.addParameter("_ARK_DATA", request.toXML());
//				pm.addParameter("_ARK_URL", url);
//				httpClient.executeMethod(pm);
//				if (pm.getStatusCode() != 200) {
//					LogUtil.warn("Framework.callRemoteMethod() error::" + pm.getStatusCode() + ";URL=" + url);
//				} else {
//					InputStream is = pm.getResponseBodyAsStream();
//					byte[] body = IOUtil.getBytesFromStream(is);
//					String result = new String(body, "UTF-8");
//					DataCollection dc = new DataCollection();
//					dc.parseXML(result);
//					return dc;
//				}
//			} catch (Exception e) {
//				LogUtil.warn("Framework.callRemoteMethod() error:" + e.getMessage() + ";URL=" + url);
//
//				i++;
//			}
//
//		}
//
//		return null;
//	}
}
