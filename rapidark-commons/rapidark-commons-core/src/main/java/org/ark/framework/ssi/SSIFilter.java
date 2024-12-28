package org.ark.framework.ssi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.util.StringUtil;
import com.rapidark.framework.extend.ExtendManager;


/**
 * @class org.ark.framework.ssi.SSIFilter
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:35:03 
 * @version V1.0
 */
public class SSIFilter implements Filter {
	protected FilterConfig config = null;

	protected int debug = 0;

	protected Long expires = null;

	protected boolean isVirtualWebappRelative = true;

	public void init(FilterConfig config) throws ServletException {
		this.config = config;

		String value = null;
		try {
			value = config.getInitParameter("debug");
			this.debug = Integer.parseInt(value);
		} catch (Throwable localThrowable) {
		}
		try {
			value = config.getInitParameter("expires");
			if (StringUtil.isEmpty(value)) {
				value = "0";
			}
			this.expires = Long.valueOf(value);
		} catch (NumberFormatException e) {
			this.expires = null;
			config.getServletContext().log("Invalid format for expires initParam; expected integer (seconds)");
		} catch (Throwable localThrowable1) {
		}
		if (this.debug > 0)
			config.getServletContext().log("SSIFilter.init() SSI invoker started with 'debug'=" + this.debug);
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ExtendManager.invoke("org.ark.framework.BeforeSSIFilter", new Object[] { request, response, chain });

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if ((Config.ServletMajorVersion == 2) && (Config.ServletMinorVersion == 3))
			response.setContentType("text/html;charset=" + Config.getGlobalCharset());
		else {
			response.setCharacterEncoding(Config.getGlobalCharset());
		}
		request.setCharacterEncoding(Config.getGlobalCharset());

		req.setAttribute("org.apache.catalina.ssi.SSIServlet", "true");
		req.setAttribute("javax.servlet.include.context_path", "true");

		ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream();
		ResponseIncludeWrapper responseIncludeWrapper = new ResponseIncludeWrapper(this.config.getServletContext(), req, res, basos);

		chain.doFilter(req, responseIncludeWrapper);

		responseIncludeWrapper.flushOutputStreamOrWriter();
		byte[] bytes = basos.toByteArray();

		String encoding = res.getCharacterEncoding();

		SSIExternalResolver ssiExternalResolver = new SSIServletExternalResolver(this.config.getServletContext(), req, res, this.isVirtualWebappRelative, this.debug, encoding);
		SSIProcessor ssiProcessor = new SSIProcessor(ssiExternalResolver, this.debug);

		Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes), encoding);
		ByteArrayOutputStream ssiout = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(ssiout, encoding));

		long lastModified = ssiProcessor.process(reader, responseIncludeWrapper.getLastModified(), writer);

		writer.flush();
		bytes = ssiout.toByteArray();

		if (this.expires != null) {
			res.setDateHeader("expires", new Date().getTime() + this.expires.longValue() * 1000L);
		}
		if (lastModified > 0L) {
			res.setDateHeader("last-modified", lastModified);
		}
		res.setDateHeader("last-modified", System.currentTimeMillis());
		res.setContentLength(bytes.length);

		res.setContentType("text/html;charset=" + Config.getGlobalCharset());
		try {
			OutputStream out = res.getOutputStream();
			out.write(bytes);
		} catch (Throwable t) {
			try {
				Writer out = res.getWriter();
				out.write(new String(bytes));
			} catch (Throwable localThrowable1) {
			}
		}
	}

	public void destroy() {
	}
}