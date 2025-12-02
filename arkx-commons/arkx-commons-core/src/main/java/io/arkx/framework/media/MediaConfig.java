package io.arkx.framework.media;

import io.arkx.framework.Config;

public class MediaConfig {

	public static String getGraphicsMagickDirectory() {
		return Config.getValue("App.GraphicsMagickDirectory");
	}

	public static String getImageLibType() {
		return Config.getValue("App.ImageLibType");
	}

}
