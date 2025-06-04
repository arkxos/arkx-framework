package io.arkx.framework.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.Primitives;
import io.arkx.framework.commons.util.StringUtil;

public class VideoUtil
{
  public static boolean captureImage(String src, String dest)
  {
    MediaInfoParser parser = new MediaInfoParser();
    parser.parse(src);
    
    long duration = parser.getDuration();
    if (duration < 30L) {
      return captureImage(src, dest, new Long(duration / 3L).intValue(), parser.getWidth(), parser.getHeight());
    }
    return captureImage(src, dest, 15, parser.getWidth(), parser.getHeight());
  }
  
  public static boolean captureImage(String src, String dest, int startSecond, int width, int height)
  {
    return captureImage(src, dest, startSecond, width, height, null);
  }
  
  public static boolean captureImage(String src, String dest, int startSecond, int width, int height, AfterVideoConvertEvent event)
  {
    FfmpegCommandBuilder fcb = new FfmpegCommandBuilder();
    fcb.addFfmpegCommand();
    fcb.addInputFileCommand(src);
    fcb.addOverwriteCommand();
    fcb.addCaptureCommand(startSecond, width, height);
    fcb.addCommand(dest);
    execute(fcb.getCommandList(), src, dest, false, event);
    return true;
  }
  
  private static FfmpegCommandBuilder getVideoConvertCommand(String src, String videoResolution)
  {
    FfmpegCommandBuilder fcb = new FfmpegCommandBuilder();
    fcb.addFfmpegCommand();
    fcb.addInputFileCommand(src);
    fcb.addOverwriteCommand();
    
    fcb.addSimpleAudioCommand();
    
    fcb.addVideoConvertCommand(videoResolution);
    return fcb;
  }
  
  public static boolean convertAndWatermark2Flv(String src, String dest, String videoResolution, String imagePath, FfmpegCommandBuilder.VideoWatermarkPosition position, AfterVideoConvertEvent event)
  {
    if (src.toLowerCase().endsWith(".flv"))
    {
      waterMark(src, dest, imagePath, position, event);
      return true;
    }
    FfmpegCommandBuilder fcb = getVideoConvertCommand(src, videoResolution);
    if ((StringUtil.isNotNull(imagePath)) && (FileUtil.exists(imagePath)))
    {
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
      fcb.addWatermarkCommand(imageName, position);
    }
    else
    {
      LogUtil.warn("VideoUtil.convertAndWatermark2Mp4=视频水印图片错误：" + imagePath);
    }
    fcb.addCommand("-f");
    fcb.addCommand("flv");
    
    boolean rewriteSourceFile = false;
    if ((dest == null) || (dest.equals(src)))
    {
      dest = src.substring(0, src.lastIndexOf("/") + 1) + "_watermarkt_" + src.substring(src.lastIndexOf("/") + 1);
      rewriteSourceFile = true;
    }
    fcb.addCommand(dest);
    
    String commandDirectory = imagePath.substring(0, imagePath.lastIndexOf("/"));
    execute(commandDirectory, fcb.getCommandList(), src, dest, rewriteSourceFile, event);
    return true;
  }
  
  public static boolean convert2Flv(String src, String dest, String videoResolution, AfterVideoConvertEvent event)
  {
    if (src.toLowerCase().endsWith(".flv")) {
      return true;
    }
    FfmpegCommandBuilder fcb = getVideoConvertCommand(src, videoResolution);
    fcb.addCommand("-f");
    fcb.addCommand("flv");
    
    boolean rewriteSourceFile = false;
    if ((dest == null) || (dest.equals(src)))
    {
      dest = src.substring(0, src.lastIndexOf("/") + 1) + "_watermarkt_" + src.substring(src.lastIndexOf("/") + 1);
      rewriteSourceFile = true;
    }
    fcb.addCommand(dest);
    
    execute(fcb.getCommandList(), src, dest, rewriteSourceFile, event);
    return true;
  }
  
  public static boolean convertAndWatermark2Mp4(String src, String dest, String videoResolution, String imagePath, FfmpegCommandBuilder.VideoWatermarkPosition position, AfterVideoConvertEvent event)
  {
    if (src.toLowerCase().endsWith(".mp4"))
    {
      waterMark(src, dest, imagePath, position, event);
      return true;
    }
    FfmpegCommandBuilder fcb = getVideoConvertCommand(src, videoResolution);
    if ((StringUtil.isNotNull(imagePath)) && (FileUtil.exists(imagePath)))
    {
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
      fcb.addWatermarkCommand(imageName, position);
    }
    else
    {
      LogUtil.warn("VideoUtil.convertAndWatermark2Mp4=视频水印图片错误：" + imagePath);
    }
    fcb.addCommand("-f");
    fcb.addCommand("mp4");
    
    boolean rewriteSourceFile = false;
    if ((dest == null) || (dest.equals(src)))
    {
      dest = src.substring(0, src.lastIndexOf("/") + 1) + "_watermarkt_" + src.substring(src.lastIndexOf("/") + 1);
      rewriteSourceFile = true;
    }
    fcb.addCommand(dest);
    
    String commandDirectory = imagePath.substring(0, imagePath.lastIndexOf("/"));
    execute(commandDirectory, fcb.getCommandList(), src, dest, rewriteSourceFile, event);
    return true;
  }
  
  public static boolean convert2Mp4(String src, String dest, String videoResolution, AfterVideoConvertEvent event)
  {
    if (src.endsWith(".mp4")) {
      return true;
    }
    FfmpegCommandBuilder fcb = getVideoConvertCommand(src, videoResolution);
    fcb.addCommand("-f");
    fcb.addCommand("mp4");
    
    boolean rewriteSourceFile = false;
    if ((dest == null) || (dest.equals(src)))
    {
      dest = src.substring(0, src.lastIndexOf("/") + 1) + "_watermarkt_" + src.substring(src.lastIndexOf("/") + 1);
      rewriteSourceFile = true;
    }
    fcb.addCommand(dest);
    
    execute(fcb.getCommandList(), src, dest, rewriteSourceFile, event);
    return true;
  }
  
  public static void waterMark(String src, String dest, String imagePath, FfmpegCommandBuilder.VideoWatermarkPosition position, AfterVideoConvertEvent event)
  {
    if ((StringUtil.isEmpty(imagePath)) || (!FileUtil.exists(imagePath)) || (!FileUtil.exists(src))) {
      return;
    }
    FfmpegCommandBuilder fcb = new FfmpegCommandBuilder();
    fcb.addFfmpegCommand();
    fcb.addInputFileCommand(src);
    fcb.addOverwriteCommand();
    
    MediaInfoParser parser = new MediaInfoParser();
    parser.parse(src);
    parser.getvBitRate();
    
    fcb.addVideoBitRateCommand(parser.getvBitRate());
    
    String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
    fcb.addWatermarkCommand(imageName, position);
    
    boolean rewriteSourceFile = false;
    if ((dest == null) || (dest.equals(src)))
    {
      dest = src.substring(0, src.lastIndexOf("/") + 1) + "_watermarkt_" + src.substring(src.lastIndexOf("/") + 1);
      rewriteSourceFile = true;
    }
    fcb.addCommand(dest);
    
    String commandDirectory = imagePath.substring(0, imagePath.lastIndexOf("/"));
    execute(commandDirectory, fcb.getCommandList(), src, dest, rewriteSourceFile, event);
  }
  
  private static void execute(List<String> commandList, String src, String dest, boolean rewriteSourceFile, AfterVideoConvertEvent event)
  {
    execute(null, commandList, src, dest, rewriteSourceFile, event);
  }
  
  public static void execute(String directory, List<String> commandList, final String src, final String dest, final boolean rewriteSourceFile, final AfterVideoConvertEvent event)
  {
   final ProcessBuilder builder = new ProcessBuilder(new String[0]);
    if (StringUtil.isNotNull(directory)) {
      builder.directory(new File(directory));
    }
    builder.command(commandList);
    new Thread(new Runnable()
    {
      public void run()
      {
        try
        {
        	Process pc = builder.start();

          
          String errorMsg = VideoUtil.readInputStream(pc.getErrorStream(), "error");
          String outputMsg = VideoUtil.readInputStream(pc.getInputStream(), "out");
          int c = pc.waitFor();
          if (c != 0)
          {
            System.out.println("处理失败：" + errorMsg);
          }
          else
          {
            if (rewriteSourceFile)
            {
              FileUtil.move(dest, src);
              if (event != null) {
                event.afterConvert(src);
              }
            }
            else if (event != null)
            {
              event.afterConvert(dest);
            }
            System.out.println(outputMsg);
          }
        }
        catch (Exception e)
        {
          e.printStackTrace();
          LogUtil.info("在进行转换处理方法中发生异常");
        }
      }
    })
    
      .start();
  }
  
  public static String readInputStream(InputStream is, String f)
    throws IOException
  {
    BufferedReader br = new BufferedReader(new InputStreamReader(is));
    StringBuffer lines = new StringBuffer();
    long totalTime = 0L;
    int percent = 0;
    for (String line = br.readLine(); line != null; line = br.readLine())
    {
      if (lines.length() > 0) {
        lines.append("\n");
      }
      lines.append(line);
      int positionDuration = line.indexOf("Duration:");
      int positionTime = line.indexOf("time=");
      if (positionDuration > 0)
      {
        String dur = line.replace("Duration:", "");
        dur = dur.trim().substring(0, 8);
        int h = Primitives.getInteger(dur.substring(0, 2));
        int m = Primitives.getInteger(dur.substring(3, 5));
        int s = Primitives.getInteger(dur.substring(6, 8));
        totalTime = h * 3600 + m * 60 + s;
      }
      if (positionTime > 0)
      {
        String time = line.substring(positionTime, 
          line.indexOf("bitrate") - 1);
        time = time.substring(time.indexOf("=") + 1, time.indexOf("."));
        int h = Primitives.getInteger(time.substring(0, 2));
        int m = Primitives.getInteger(time.substring(3, 5));
        int s = Primitives.getInteger(time.substring(6, 8));
        int total = h * 3600 + m * 60 + s;
        float t = total / (float)totalTime;
        percent = (int)Math.ceil(t * 100.0F);
      }
      System.out.println("完成：" + percent + "%");
    }
    br.close();
    return lines.toString();
  }
  
  public static abstract interface AfterVideoConvertEvent
  {
    public abstract void afterConvert(String paramString);
  }
}
