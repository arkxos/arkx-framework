package io.arkx.framework.common.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import io.arkx.framework.common.utils.StringUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * xss 过滤
 * body 缓存
 * headers 缓存
 *
 * @author liuyadu
 */
public class XssServletRequestWrapper extends HttpServletRequestWrapper {
    private HttpServletRequest request;
    private final byte[] body;
    private Map<String, String> headers = new HashMap<>();

    public XssServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(request.getInputStream(), baos);
        this.body = baos.toByteArray();
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    @Override
    public String getParameter(String name) {
        name = StringUtils.stripXss(name);
        String value = request.getParameter(name);
        if (!StringUtils.isEmpty(value)) {
            value = StringUtils.stripXss(value).trim();
        }
        return value;
    }

    @Override
    public String getHeader(String name) {
        name = StringUtils.trim(name);
        String value;
        if (headers.containsKey(name)) {
            value = headers.get(name);
        } else {
            value = super.getHeader(name);
            this.headers.put(name, value);
        }
        if (StringUtils.isNotBlank(value)) {
            value = StringUtils.trim(value);
        }
        return value;
    }

    public void putHeader(String name, String value) {
        this.headers.put(name, value);
    }


    @Override
    public Enumeration<String> getHeaderNames() {
        Set<String> set = new HashSet<>(headers.keySet());
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            set.add(name);
        }
        return Collections.enumeration(set);
    }

    @Override
    public String[] getParameterValues(String name) {
        name = StringUtils.stripXss(name);
        String[] parameterValues = super.getParameterValues(name);
        if (parameterValues == null) {
            return null;
        }
        for (int i = 0; i < parameterValues.length; i++) {
            String value = parameterValues[i];
            parameterValues[i] = StringUtils.stripXss(value).trim();
        }
        return parameterValues;
    }
}
