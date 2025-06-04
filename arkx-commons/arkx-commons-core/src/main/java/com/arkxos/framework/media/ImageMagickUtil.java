package com.arkxos.framework.media;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.Primitives;
import io.arkx.framework.commons.util.StringUtil;

public class ImageMagickUtil
{
  public static void scaleRateImageFile(String fromFileName, String toFileName, int toWidth, int toHeight)
    throws Exception
  {
    Dimension dim = ImageUtil.getDimension(fromFileName);
    double w = dim.getWidth();
    double h = dim.getHeight();
    if ((w < toWidth) && (h < toHeight))
    {
      FileUtil.copy(fromFileName, toFileName);
      return;
    }
    if (toWidth == 0)
    {
      if (h <= toHeight) {
        FileUtil.copy(fromFileName, toFileName);
      } else {
        scaleRateImageFile(fromFileName, toFileName, toHeight / h);
      }
      return;
    }
    if (toHeight == 0)
    {
      if (w <= toWidth) {
        FileUtil.copy(fromFileName, toFileName);
      } else {
        scaleRateImageFile(fromFileName, toFileName, toWidth / w);
      }
      return;
    }
    if (toWidth / w > toHeight / h) {
      scaleRateImageFile(fromFileName, toFileName, toHeight / h);
    } else {
      scaleRateImageFile(fromFileName, toFileName, toWidth / w);
    }
  }
  
  public static void scaleRateImageFile(String fromFileName, String toFileName, double rate)
    throws Exception
  {
    Dimension dim = ImageUtil.getDimension(fromFileName);
    double w = dim.getWidth();
    double h = dim.getHeight();
    scaleFixedImageFile(fromFileName, toFileName, (int)(w * rate), (int)(h * rate));
  }
  
  public static void scaleRateImageFile(String fromFileName, String toFileName, int toWidth, int toHeight, String mode)
    throws Exception
  {
    IMOperation op = new IMOperation();
    op.addImage();
    op.colorspace("RGB");
    if ("fit".equals(mode))
    {
      op.resize(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
    }
    else if ("stretch".equals(mode))
    {
      op.resize(Integer.valueOf(toWidth), Integer.valueOf(toHeight), Character.valueOf('!'));
    }
    else
    {
      int subIndex = mode.indexOf('_');
      if (subIndex == 4)
      {
        String sMode = mode.substring(0, subIndex);
        if ("fill".equals(sMode))
        {
          if (subIndex < mode.length() - 1)
          {
            int cutLoc = Primitives.getInteger(mode.substring(subIndex + 1));
            if ((cutLoc <= 0) || (cutLoc > 9)) {
              cutLoc = 5;
            }
            op.thumbnail(Integer.valueOf(toWidth), Integer.valueOf(toHeight), Character.valueOf('^')).gravity(getPositionString(cutLoc)).extent(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
          }
          else
          {
            op.thumbnail(Integer.valueOf(toWidth), Integer.valueOf(toHeight), Character.valueOf('^')).gravity(getPositionString(5)).extent(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
          }
        }
        else {
          op.resize(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
        }
      }
      else if (subIndex == -1)
      {
        if ("fill".equals(mode)) {
          op.thumbnail(Integer.valueOf(toWidth), Integer.valueOf(toHeight), Character.valueOf('^')).gravity(getPositionString(5)).extent(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
        } else {
          op.resize(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
        }
      }
      else
      {
        op.resize(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
      }
    }
    op.addImage();
    ConvertCmd convert = new ConvertCmd(true);
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = MediaConfig.getGraphicsMagickDirectory();
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm GraphicsMagick directory config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    try
    {
      convert.run(op, new Object[] { fromFileName, toFileName });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void scaleFixedImageFile(String fromFileName, String toFileName, int toWidth, int toHeight)
    throws Exception
  {
    IMOperation op = new IMOperation();
    op.addImage();
    op.colorspace("RGB");
    op.resize(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
    op.addImage();
    
    ConvertCmd convert = new ConvertCmd(true);
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = MediaConfig.getGraphicsMagickDirectory();
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm GraphicsMagick directory config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    try
    {
      convert.run(op, new Object[] { fromFileName, toFileName });
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void pressText(String filePath, String pressText, Font font, Color color, float opicty, int x, int y)
    throws Exception
  {
    SimpleImageUtil.pressText(filePath, pressText, font, color, opicty, x, y);
  }
  
  public static void pressText(String filePath, String pressText, Font font, Color color, Color outlineColor, int outlineSize, boolean underline, float opicty, int x, int y)
    throws Exception
  {
    SimpleImageUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, underline, opicty, x, y);
  }
  
  public static void pressText(String filePath, String pressText, Font font, Color color, Color shadowColor, float opicty, int x, int y)
    throws Exception
  {
    SimpleImageUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, x, y);
  }
  
  public static final void pressImage(String targetImg, String pressImg, int position, int opicty)
    throws Exception
  {
    Dimension d = ImageUtil.getDimension(targetImg);
    double w = d.getWidth();
    double h = d.getHeight();
    if ((w <= 300.0D) && (h <= 300.0D)) {
      return;
    }
    IMOperation op = new IMOperation();
    op.dissolve(Integer.valueOf(opicty));
    op.gravity(getPositionString(position));
    op.quality(Double.valueOf(90.0D));
    op.addImage();
    op.addImage();
    op.addImage();
    
    CompositeCmd cmd = new CompositeCmd(true);
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = MediaConfig.getGraphicsMagickDirectory();
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm GraphicsMagick directory config has set!");
      }
      cmd.setSearchPath(imageMagickPath);
    }
    cmd.run(op, new Object[] { pressImg, targetImg, targetImg });
  }
  
  private static String getPositionString(int pos)
  {
    if (pos == 1) {
      return "NorthWest";
    }
    if (pos == 2) {
      return "North";
    }
    if (pos == 3) {
      return "NorthEast";
    }
    if (pos == 4) {
      return "West";
    }
    if (pos == 5) {
      return "Center";
    }
    if (pos == 6) {
      return "East";
    }
    if (pos == 7) {
      return "SouthWest";
    }
    if (pos == 8) {
      return "South";
    }
    if (pos == 9) {
      return "SouthEast";
    }
    return "SouthEast";
  }
  
  public static void cutting(String src, String dest, int x, int y, int w, int h)
    throws IOException, InterruptedException, IM4JavaException
  {
    IMOperation op = new IMOperation();
    op.addImage(new String[] { src });
    op.crop(Integer.valueOf(w), Integer.valueOf(h), Integer.valueOf(x), Integer.valueOf(y));
    op.addImage(new String[] { dest });
    ConvertCmd convert = new ConvertCmd(true);
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = MediaConfig.getGraphicsMagickDirectory();
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm GraphicsMagick directory config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    convert.run(op, new Object[0]);
  }
  
  public static void rotate(String src, String dest, int degree)
    throws IOException, InterruptedException, IM4JavaException
  {
    IMOperation op = new IMOperation();
    op.addImage(new String[] { src });
    op.rotate(Double.valueOf(Math.toRadians(degree)));
    op.addImage(new String[] { dest });
    ConvertCmd convert = new ConvertCmd(true);
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = MediaConfig.getGraphicsMagickDirectory();
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm GraphicsMagick directory config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    convert.run(op, new Object[0]);
  }
  
  public static void flip(String src, String dest, boolean flipX)
    throws IOException, InterruptedException, IM4JavaException
  {
    IMOperation op = new IMOperation();
    op.addImage(new String[] { src });
    if (flipX) {
      op.flop();
    } else {
      op.flip();
    }
    op.addImage(new String[] { dest });
    ConvertCmd convert = new ConvertCmd(true);
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = MediaConfig.getGraphicsMagickDirectory();
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm GraphicsMagick directory config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    convert.run(op, new Object[0]);
  }
}
