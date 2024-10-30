package com.rapidark.npm.cdn;

import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import com.rapidark.npm.JNPMService;
import com.rapidark.npm.JNPMSettings;

public class CDNTest {

	static {
		if(!JNPMService.isConfigured())
			JNPMService.configure(JNPMSettings.builder()
						.homeDirectory(Paths.get("target", ".jnpm"))
 						.build());
	}

	@Test
	public void testParsing() {
		CDNRequest reqInfo = CDNRequest.valueOf("/vue@2.6.11/path/to/the/file.js");
		assertEquals("vue", reqInfo.getPackageName());
		assertEquals("2.6.11", reqInfo.getVersionExpression());
		assertEquals("vue@2.6.11", reqInfo.getPackageVersionExpression());
		assertEquals("path/to/the/file.js", reqInfo.getPath());
		assertEquals("file.js", reqInfo.getFileName());
		assertTrue(reqInfo.isExactVersion());

		reqInfo = CDNRequest.valueOf("/vue@~2.6.11/path/to/the/file2.js");
		assertEquals("vue", reqInfo.getPackageName());
		assertEquals("~2.6.11", reqInfo.getVersionExpression());
		assertEquals("vue@~2.6.11", reqInfo.getPackageVersionExpression());
		assertEquals("path/to/the/file2.js", reqInfo.getPath());
		assertEquals("file2.js", reqInfo.getFileName());
		assertTrue(!reqInfo.isExactVersion());

		reqInfo = CDNRequest.valueOf("/vue/path/to/the/file3.js");
		assertEquals("vue", reqInfo.getPackageName());
		assertEquals("latest", reqInfo.getVersionExpression());
		assertEquals("vue@latest", reqInfo.getPackageVersionExpression());
		assertEquals("path/to/the/file3.js", reqInfo.getPath());
		assertEquals("file3.js", reqInfo.getFileName());
		assertTrue(!reqInfo.isExactVersion());
	}

    @Test
    public void testParsingEx() {
        String PATH_PATTERN = "/(.*)@([^/]*)/(.*)";
        Pattern PATH_REGEXP = Pattern.compile(PATH_PATTERN);
        String fullPath = "/@rapidark/arkos-app-system@1.0.29/hel_dist/hel-meta.json";
//        fullPath = "/vue@2.6.11/path/to/the/file.js";
        fullPath = "/vue/path/to/the/file3.js";
        Matcher matcher = PATH_REGEXP.matcher(fullPath);
        if(matcher.matches()) {
            String packageName = matcher.group(1);
            String version = matcher.group(2);
            String path = matcher.group(3);
            System.out.println("packageName: " + packageName);
            System.out.println("version: " + version);
            System.out.println("path: " + path);
        }
    }

    @Test
    public void testParsingWithGroup() {
        CDNRequest reqInfo = CDNRequest.valueOf("/@rapidark/arkos-app-system@1.0.29/hel_dist/hel-meta.json");
        assertEquals("@rapidark/arkos-app-system", reqInfo.getPackageName());
        assertEquals("1.0.29", reqInfo.getVersionExpression());
        assertEquals("@rapidark/arkos-app-system@1.0.29", reqInfo.getPackageVersionExpression());
        assertEquals("hel_dist/hel-meta.json", reqInfo.getPath());
        assertEquals("hel-meta.json", reqInfo.getFileName());
        assertTrue(reqInfo.isExactVersion());
    }

	@Test
	public void testServlet() throws Exception{
		HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/vue@2.6.11/dist/vue.js");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {

			@Override
			public void write(int b) {
				baos.write(b);
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
			}

			@Override
			public boolean isReady() {
				return true;
			}

		};
        when(response.getOutputStream()).thenReturn(sos);

        new CDNServlet().doGet(request, response);

        baos.flush(); // it may not have been flushed yet...
        String content = new String(baos.toByteArray());
//        System.out.println("Content: "+content);
        assertTrue(content.contains("Vue.js v2.6.11"));
	}
}
