package org.ark.framework.jaf.zhtml;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Locale;

import com.arkxos.framework.Config;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


/**
 * @class org.ark.framework.jaf.zhtml.ZhtmlTesterResponse
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:56:38 
 * @version V1.0
 */
public class ZhtmlTesterResponse implements HttpServletResponse {
	private ZhtmlTester tester;
	private String charset;
	private PrintWriter writer = new PrintWriter(new StringWriter());
	private ZhtmlPage page;

	public ZhtmlTesterResponse(ZhtmlPage page) {
		this.page = page;
	}

	public void flushBuffer() throws IOException {
	}

	public int getBufferSize() {
		return 0;
	}

	public String getCharacterEncoding() {
		if (this.charset == null) {
			this.charset = Config.getGlobalCharset();
		}
		return this.charset;
	}

	public String getContentType() {
		return "text/html";
	}

	public Locale getLocale() {
		return Locale.CHINA;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	public PrintWriter getWriter() throws IOException {
		return this.writer;
	}

	public boolean isCommitted() {
		return false;
	}

	public void reset() {
	}

	public void resetBuffer() {
	}

	public void setBufferSize(int arg0) {
	}

	public void setCharacterEncoding(String charset) {
		this.charset = charset;
	}

	public void setContentLength(int arg0) {
	}

	public void setContentType(String arg0) {
	}

	public void setLocale(Locale arg0) {
	}

	public void addCookie(Cookie arg0) {
	}

	public void addDateHeader(String arg0, long arg1) {
	}

	public void addHeader(String arg0, String arg1) {
	}

	public void addIntHeader(String arg0, int arg1) {
	}

	public boolean containsHeader(String arg0) {
		return false;
	}

	public String encodeRedirectURL(String arg0) {
		return null;
	}

	public String encodeRedirectUrl(String arg0) {
		return null;
	}

	public String encodeURL(String arg0) {
		return null;
	}

	public String encodeUrl(String arg0) {
		return null;
	}

	public void sendError(int arg0) throws IOException {
	}

	public void sendError(int arg0, String arg1) throws IOException {
	}

	public void sendRedirect(String arg0) throws IOException {
	}

	public void setDateHeader(String arg0, long arg1) {
	}

	public void setHeader(String arg0, String arg1) {
	}

	public void setIntHeader(String arg0, int arg1) {
	}

	public void setStatus(int arg0) {
	}

	public void setStatus(int arg0, String arg1) {
	}

	@Override
	public void setContentLengthLong(long len) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getHeader(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		// TODO Auto-generated method stub
		return null;
	}
}