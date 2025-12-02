package io.arkx.framework.preloader;

import java.io.File;
import java.util.jar.JarFile;

public class JarClassLoaderTest {

	public static void main(String[] args) throws Exception {
		String folder = "C:\\Users\\Administrator\\git\\rapid-ark-v2\\rapid-ark-web\\src\\main\\webapp\\WEB-INF\\lib";
		File[] children = new File(folder).listFiles();
		for (File child : children) {
			System.out.println(child);
			// ZipUtil.unzip(child.getAbsolutePath(), folder);
			// JarUtil.readJar(child.getAbsolutePath());

			JarFile jf = new JarFile(child.getAbsolutePath());
			jf.getManifest();
		}
	}

}
