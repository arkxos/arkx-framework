package org.ark.framework.jaf.zhtml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.util.FileUtil;

/**
import com.rapidark.preloader.PreClassLoader;
 * @class org.ark.framework.jaf.zhtml.ZhtmlManager
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:55:19 
 * @version V1.0
 */
public class ZhtmlManager {
	private static HashMap<String, ZhtmlExecutor> map = new HashMap<String, ZhtmlExecutor>();
	private static Object mutex = new Object();

	public static void forward(String url, HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ZhtmlRuntimeException, ZhtmlCompileException, ServletException,
			IOException {
		String base = request.getServletPath();
		String url2 = url;
		if (!url.startsWith("/")) {
			while (url2.startsWith(".")) {
				if (url2.startsWith("./")) {
					url2 = url2.substring(2);
				} else {
					if (!url2.startsWith("../"))
						break;
					base = base.substring(0, base.lastIndexOf("/") + 1);
					url2 = url2.substring(3);
				}

			}

			url2 = base + url2;
		}
		if (!execute(url2, request, response, context)) {
			RequestDispatcher rd = request.getRequestDispatcher(url);
			rd.forward(request, response);
		}
	}

	public static boolean execute(String url, HttpServletRequest request, HttpServletResponse response, ServletContext context) throws ZhtmlRuntimeException, ZhtmlCompileException {
		response.setHeader("Pragma", "No-Cache");
		response.setHeader("Cache-Control", "No-Cache");
		response.setDateHeader("Expires", 0L);
		response.setContentType("text/html");

		String fileName = url;
		if (fileName.indexOf("?") > 0) {
			fileName = fileName.substring(0, fileName.indexOf("?"));
		}
		if (fileName.indexOf("#") > 0) {
			fileName = fileName.substring(0, fileName.indexOf("#"));
		}
		if (fileName.startsWith("/")) {
			fileName = fileName.substring(1);
		}

		ZhtmlExecutor je = map.get(fileName);
		String fullPath = Config.getContextRealPath() + "/" + fileName;
		fullPath = FileUtil.normalizePath(fullPath);
		try {
			if ((je == null) || (needReload(je, fileName))) {
				ZhtmlCompiler jc = new ZhtmlCompiler();
				if (new File(fullPath).exists()) {
					jc.compile(fullPath);
					je = jc.getExecutor();
				} else {
					String source = getSourceInJar(fileName);
					if (source != null) {
						jc.setFileName(fileName);
						jc.compileSource(source);
						je = jc.getExecutor();
						je.fromJar = true;
					} else {
						return false;
					}
				}
				synchronized (mutex) {
					map.put(fileName, je);
				}
			}
		} catch (FileNotFoundException e) {
			return false;
		} catch (NotPrecompileException e) {
			return false;
		}

		ZhtmlCommonServlet servlet = new ZhtmlCommonServlet(context);
		PageContext pageContext = JspFactory.getDefaultFactory().getPageContext(servlet, request, response, null, je.sessionFlag, 8192, true);

		ZhtmlPage page = new ZhtmlPage(fullPath);
		page.setPageContext(pageContext);
		page.setRequest(request);
		page.setResponse(response);

		je.execute(page);
		JspFactory.getDefaultFactory().releasePageContext(pageContext);
		return true;
	}

	public static String getSourceInJar(String fileName) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		if (is != null) {
			try {
				return new String(FileUtil.readByte(is), Config.getGlobalCharset());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static boolean needReload(ZhtmlExecutor je, String fileName) throws FileNotFoundException {
		File f;
		if (je.fromJar)
			return false;
		if (System.currentTimeMillis() - je.getLastModified() <= 3000L)
			return false;
		f = new File(FileUtil.normalizePath((new StringBuilder(String.valueOf(Config.getContextRealPath()))).append("/").append(fileName).toString()));
		if (!f.exists()) {
			throw new FileNotFoundException((new StringBuilder("File not found:")).append(fileName).toString());
		}
		map.remove(fileName);

		if (f.lastModified() != je.getLastModified())
			return true;
		return false;

	}
}