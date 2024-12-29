package com.arkxos.framework.media;

import com.rapidark.framework.Config;

public class MediaConfig
{
  public static String getGraphicsMagickDirectory()
  {
    return Config.getValue("App.GraphicsMagickDirectory");
  }
  
  public static String getImageLibType()
  {
    return Config.getValue("App.ImageLibType");
  }
}
