package com.rapidark.npm.cdn;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import com.rapidark.npm.JNPMService;
import com.rapidark.npm.JNPMSettings;
import com.rapidark.npm.JNPMUtils;
import com.rapidark.npm.dm.VersionInfo;

/**
 * {@link HttpServlet} to serve resources from NPM package. Format of the request: /cdn/&lt;package&gt;/&lt;file path&gt;
 * For example:
 * <ul>
 * 	<li>/cdn/bootstrap/dist/css/bootstrap.min.css</li>
 *  <li>/cdn/vue@~2.6.11/dist/vue.js</li>
 * </ul>
 */
@WebServlet(name = "cdnServlet", urlPatterns = "/cdn/*")
public class CDNServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Map<String, VersionInfo> versionsCache = new ConcurrentHashMap<>();

	@Override
	public void init() {
		if(!JNPMService.isConfigured()) {
			JNPMSettings.JNPMSettingsBuilder builder = JNPMSettings.builder();
			ServletConfig cfg = getServletConfig();

			String registryUrl = cfg.getInitParameter("registryUrl");
			if(!StringUtils.isEmpty(registryUrl)) builder.registryUrl(registryUrl);

			String homeDirectory = cfg.getInitParameter("homeDirectory");
			if(!StringUtils.isEmpty(homeDirectory)) builder.homeDirectory(Paths.get(homeDirectory));

			String downloadDirectory = cfg.getInitParameter("downloadDirectory");
			if(!StringUtils.isEmpty(downloadDirectory)) builder.downloadDirectory(Paths.get(downloadDirectory));

			String installDirectory = cfg.getInitParameter("installDirectory");
			if(!StringUtils.isEmpty(installDirectory)) builder.installDirectory(Paths.get(installDirectory));

			builder.username(cfg.getInitParameter("username")).password(cfg.getInitParameter("password"));
            builder.useCache(Boolean.valueOf(cfg.getInitParameter("useCache")));

			JNPMService.configure(builder.build());
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			CDNRequest request = CDNRequest.valueOf(req.getPathInfo());
			resp.setContentType(JNPMUtils.fileNameToMimeType(request.getFileName()));
			resp.addHeader("Cache-Control", "public, max-age=604800, immutable");
			String forceParam = req.getParameter("force");
			if(forceParam!=null) request.forceDownload(Boolean.parseBoolean(forceParam));
			JNPMUtils.readTarball(request.resolveVersion(versionsCache),
					"/package/"+request.getPath(),
					resp.getOutputStream());
		} catch (IllegalArgumentException | FileNotFoundException e) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource was not found for provided path '"+req.getPathInfo()+"'");
		}
	}

}
