package org.ark.framework.media;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.rapidark.framework.Config;

public class ImageMagickUtil
{
  public static void scaleRateImageFile(String fromFileName, String toFileName, int toWidth, int toHeight)
    throws Exception
  {
    Dimension dim = ImageUtil.getDimension(fromFileName);
    double w = dim.getWidth();
    double h = dim.getHeight();
    if ((w < toWidth) && (h < toHeight)) {
      FileUtil.copy(fromFileName, toFileName);
      return;
    }
    if (toWidth == 0) {
      if (h <= toHeight)
        FileUtil.copy(fromFileName, toFileName);
      else {
        scaleRateImageFile(fromFileName, toFileName, toHeight / h);
      }
      return;
    }if (toHeight == 0) {
      if (w <= toWidth)
        FileUtil.copy(fromFileName, toFileName);
      else {
        scaleRateImageFile(fromFileName, toFileName, toWidth / w);
      }
      return;
    }
    if (toWidth / w > toHeight / h)
      scaleRateImageFile(fromFileName, toFileName, toHeight / h);
    else
      scaleRateImageFile(fromFileName, toFileName, toWidth / w);
  }

  public static void scaleRateImageFile(String fromFileName, String toFileName, double rate) throws Exception
  {
    Dimension dim = ImageUtil.getDimension(fromFileName);
    double w = dim.getWidth();
    double h = dim.getHeight();

    IMOperation op = new IMOperation();
    op.addImage();
    op.colorspace("RGB");
    op.resize(Integer.valueOf((int)(w * rate)), Integer.valueOf((int)(h * rate)));
    op.addImage();

    ConvertCmd convert = new ConvertCmd(true);
    try {
      convert.run(op, new Object[] { fromFileName, toFileName });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void scaleFixedImageFile(String fromFileName, String toFileName, int toWidth, int toHeight) throws Exception {
    IMOperation op = new IMOperation();
    op.addImage();
    op.colorspace("RGB");
    op.resize(Integer.valueOf(toWidth), Integer.valueOf(toHeight));
    op.addImage();

    ConvertCmd convert = new ConvertCmd(true);
    try {
      convert.run(op, new Object[] { fromFileName, toFileName });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void pressText(String filePath, String pressText, Font font, Color color, float opicty, int x, int y) throws Exception
  {
    SimpleImageUtil.pressText(filePath, pressText, font, color, opicty, x, y);
  }

  public static void pressText(String filePath, String pressText, Font font, Color color, Color outlineColor, int outlineSize, float opicty, int x, int y) throws Exception
  {
    SimpleImageUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, opicty, x, y);
  }

  public static void pressText(String filePath, String pressText, Font font, Color color, Color shadowColor, float opicty, int x, int y) throws Exception
  {
    SimpleImageUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, x, y);
  }

  public static final void pressImage(String targetImg, String pressImg, int position) throws Exception {
    CompositeCmd cmd = new CompositeCmd(true);
    IMOperation op = new IMOperation();
    op.gravity(getPositionString(position));
    op.quality(Double.valueOf(90.0D));
    op.addImage();
    op.addImage();
    op.addImage();
    cmd.run(op, new Object[] { pressImg, targetImg, targetImg });
  }

  private static String getPositionString(int pos) {
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
    ConvertCmd convert = new ConvertCmd();
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = Config.getValue("ImageMagickPath");
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm ImageMagickPath config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    convert.run(op, new Object[0]);
  }

  public static void rotate(String src, String dest, int degree) throws IOException, InterruptedException, IM4JavaException
  {
    IMOperation op = new IMOperation();
    op.addImage(new String[] { src });
    op.rotate(Double.valueOf(Math.toRadians(degree)));
    op.addImage(new String[] { dest });
    ConvertCmd convert = new ConvertCmd();
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = Config.getValue("ImageMagickPath");
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm ImageMagickPath config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    convert.run(op, new Object[0]);
  }

  public static void flip(String src, String dest, boolean flipX) throws IOException, InterruptedException, IM4JavaException
  {
    IMOperation op = new IMOperation();
    op.addImage(new String[] { src });
    if (flipX)
      op.flop();
    else {
      op.flip();
    }
    op.addImage(new String[] { dest });
    ConvertCmd convert = new ConvertCmd();
    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
    {
      String imageMagickPath = Config.getValue("ImageMagickPath");
      if (StringUtil.isEmpty(imageMagickPath)) {
        throw new RuntimeException("Confirm ImageMagickPath config has set!");
      }
      convert.setSearchPath(imageMagickPath);
    }
    convert.run(op, new Object[0]);
  }
}