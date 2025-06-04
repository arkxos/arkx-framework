package com.arkxos.framework.media;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import com.alibaba.simpleimage.ImageFormat;
import com.arkxos.framework.Config;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.StringUtil;

public class ImageJDKUtil
{
  public static BufferedImage scaleRate(BufferedImage srcImage, double rate)
  {
    return scaleRate(srcImage, rate, rate, null);
  }
  
  public static BufferedImage scaleRate(BufferedImage srcImage, int width, int height)
  {
    double w = srcImage.getWidth();
    double h = srcImage.getHeight();
    if ((w < width) && (h < height)) {
      return srcImage;
    }
    if (height == 0)
    {
      if (w <= width) {
        return srcImage;
      }
      return scaleRate(srcImage, width / w, width / w, null);
    }
    if (width == 0)
    {
      if (h <= height) {
        return srcImage;
      }
      return scaleRate(srcImage, height / h, height / h, null);
    }
    if (w / h > width / height) {
      return scaleRate(srcImage, width / w, width / w, null);
    }
    return scaleRate(srcImage, height / h, height / h, null);
  }
  
  public static BufferedImage gray(BufferedImage srcImage)
  {
    BufferedImage dstImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());
    Graphics2D g2 = dstImage.createGraphics();
    RenderingHints hints = g2.getRenderingHints();
    g2.dispose();
    ColorSpace grayCS = ColorSpace.getInstance(1003);
    ColorConvertOp colorConvertOp = new ColorConvertOp(grayCS, hints);
    colorConvertOp.filter(srcImage, dstImage);
    return dstImage;
  }
  
  public static BufferedImage scaleRate(BufferedImage srcImage, double xscale, double yscale, RenderingHints hints)
  {
    AffineTransform transform = AffineTransform.getScaleInstance(xscale, yscale);
    AffineTransformOp op = new AffineTransformOp(transform, 1);
    return op.filter(srcImage, null);
  }
  
  public static BufferedImage scaleFixed(BufferedImage srcImage, int width, int height, boolean keepRate)
  {
    int srcWidth = srcImage.getWidth();
    int srcHeight = srcImage.getHeight();
    double wScale = width * 1.0D / srcWidth;
    double hScale = height * 1.0D / srcHeight;
    if (keepRate) {
      if ((wScale > hScale) && (hScale != 0.0D)) {
        wScale = hScale;
      } else {
        hScale = wScale;
      }
    }
    ImageScale is = new ImageScale();
    return is.doScale(srcImage, (int)(srcWidth * wScale), (int)(srcHeight * hScale));
  }
  
  public static void scaleFixedImageFile(String srcFile, String destFile, int width, int height)
    throws IOException
  {
    scaleFixedImageFile(srcFile, destFile, width, height, true);
  }
  
  public static void scaleFixedImageFile(String srcFile, String destFile, int width, int height, boolean keepRate)
    throws IOException
  {
    try
    {
      FileInputStream fs = new FileInputStream(srcFile);
      byte[] bf = new byte[20];
      fs.read(bf, 0, 20);
      fs.close();
      String prefix = new String(bf);
      if (prefix.startsWith("GIF8"))
      {
        try
        {
          Dimension dim = ImageUtil.getDimension(srcFile);
          double sWidth = dim.getWidth();
          double sHeight = dim.getHeight();
          GifUtil.resizeByRate(srcFile, destFile, sWidth >= width ? width : 0, sHeight >= height ? height : 0, keepRate);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      else
      {
        BufferedImage image = readImage(srcFile);
        BufferedImage newImage = scaleFixed(image, width, height, keepRate);
        writeImageFile(destFile, newImage);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static BufferedImage readImage(String srcFile)
    throws IOException
  {
    BufferedImage src = null;
    FileInputStream fs = new FileInputStream(srcFile);
    byte[] bf = new byte[20];
    fs.read(bf, 0, 20);
    fs.close();
    String prefix = new String(bf);
    if (prefix.startsWith("BM")) {
      src = BmpUtil.read(srcFile);
    } else {
      src = ImageIO.read(new File(srcFile));
    }
    BufferedImage image = new BufferedImage(src.getWidth(), src.getHeight(), 1);
    Graphics g = image.createGraphics();
    g.drawImage(src, 0, 0, src.getWidth(), src.getHeight(), null);
    return image;
  }
  
  public static void scaleRateImageFile(String srcFile, String destFile, int width, int height)
    throws IOException
  {
    scaleFixedImageFile(srcFile, destFile, width, height, true);
  }
  
  public static void scaleRateImageFile(String srcFile, String destFile, double rate)
    throws IOException
  {
    try
    {
      if (srcFile.toLowerCase().endsWith(".gif"))
      {
        try
        {
          GifUtil.resizeByRate(srcFile, destFile, rate, rate);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      else
      {
        BufferedImage image = readImage(srcFile);
        BufferedImage newImage = scaleRate(image, rate);
        writeImageFile(destFile, newImage);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void grayImageFile(String srcFile, String destFile)
    throws IOException
  {
    writeImageFile(destFile, gray(ImageIO.read(new File(srcFile))));
  }
  
  /* Error */
  public static void writeImageFile(String fileName, BufferedImage image)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 258	java/io/FileOutputStream
    //   5: dup
    //   6: aload_0
    //   7: invokespecial 260	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   10: astore_2
    //   11: aload_0
    //   12: invokevirtual 240	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   15: ldc -12
    //   17: invokevirtual 246	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   20: ifeq +14 -> 34
    //   23: new 261	java/lang/RuntimeException
    //   26: dup
    //   27: ldc_w 263
    //   30: invokespecial 265	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   33: athrow
    //   34: aload_0
    //   35: invokevirtual 240	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   38: ldc_w 266
    //   41: invokevirtual 246	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   44: ifeq +12 -> 56
    //   47: aload_1
    //   48: ldc_w 268
    //   51: aload_2
    //   52: invokestatic 270	javax/imageio/ImageIO:write	(Ljava/awt/image/RenderedImage;Ljava/lang/String;Ljava/io/OutputStream;)Z
    //   55: pop
    //   56: aload_0
    //   57: invokevirtual 240	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   60: ldc_w 274
    //   63: invokevirtual 246	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   66: ifne +16 -> 82
    //   69: aload_0
    //   70: invokevirtual 240	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   73: ldc_w 276
    //   76: invokevirtual 246	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   79: ifeq +33 -> 112
    //   82: aload_2
    //   83: invokestatic 278	com/sun/image/codec/jpeg/JPEGCodec:createJPEGEncoder	(Ljava/io/OutputStream;)Lcom/sun/image/codec/jpeg/JPEGImageEncoder;
    //   86: astore_3
    //   87: aload_3
    //   88: aload_1
    //   89: invokeinterface 284 2 0
    //   94: astore 4
    //   96: aload 4
    //   98: fconst_1
    //   99: iconst_0
    //   100: invokeinterface 290 3 0
    //   105: aload_3
    //   106: aload_1
    //   107: invokeinterface 296 2 0
    //   112: aload_2
    //   113: invokevirtual 300	java/io/FileOutputStream:flush	()V
    //   116: goto +26 -> 142
    //   119: astore 5
    //   121: aload_2
    //   122: ifnull +17 -> 139
    //   125: aload_2
    //   126: invokevirtual 303	java/io/FileOutputStream:close	()V
    //   129: goto +10 -> 139
    //   132: astore 6
    //   134: aload 6
    //   136: invokevirtual 199	java/io/IOException:printStackTrace	()V
    //   139: aload 5
    //   141: athrow
    //   142: aload_2
    //   143: ifnull +17 -> 160
    //   146: aload_2
    //   147: invokevirtual 303	java/io/FileOutputStream:close	()V
    //   150: goto +10 -> 160
    //   153: astore 6
    //   155: aload 6
    //   157: invokevirtual 199	java/io/IOException:printStackTrace	()V
    //   160: return
    // Line number table:
    //   Java source line #265	-> byte code offset #0
    //   Java source line #267	-> byte code offset #2
    //   Java source line #268	-> byte code offset #11
    //   Java source line #269	-> byte code offset #23
    //   Java source line #271	-> byte code offset #34
    //   Java source line #272	-> byte code offset #47
    //   Java source line #274	-> byte code offset #56
    //   Java source line #275	-> byte code offset #82
    //   Java source line #276	-> byte code offset #87
    //   Java source line #277	-> byte code offset #96
    //   Java source line #278	-> byte code offset #105
    //   Java source line #280	-> byte code offset #112
    //   Java source line #281	-> byte code offset #116
    //   Java source line #283	-> byte code offset #121
    //   Java source line #284	-> byte code offset #125
    //   Java source line #286	-> byte code offset #129
    //   Java source line #287	-> byte code offset #134
    //   Java source line #289	-> byte code offset #139
    //   Java source line #283	-> byte code offset #142
    //   Java source line #284	-> byte code offset #146
    //   Java source line #286	-> byte code offset #150
    //   Java source line #287	-> byte code offset #155
    //   Java source line #290	-> byte code offset #160
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	161	0	fileName	String
    //   0	161	1	image	BufferedImage
    //   1	146	2	fos	FileOutputStream
    //   86	20	3	encoder	JPEGImageEncoder
    //   94	3	4	param	JPEGEncodeParam
    //   119	21	5	localObject	Object
    //   132	3	6	e	IOException
    //   153	3	6	e	IOException
    // Exception table:
    //   from	to	target	type
    //   2	119	119	finally
    //   121	129	132	java/io/IOException
    //   142	150	153	java/io/IOException
  }
  
  public static final void pressImage(String targetImg, String pressImg, int position)
  {
    try
    {
      File file = new File(targetImg);
      Image src = ImageIO.read(file);
      int wideth = src.getWidth(null);
      int height = src.getHeight(null);
      if ((wideth <= 300) && (height <= 300)) {
        return;
      }
      BufferedImage image = new BufferedImage(wideth, height, 1);
      Graphics g = image.createGraphics();
      g.drawImage(src, 0, 0, wideth, height, null);
      
      File file_press = new File(pressImg);
      if (!file_press.exists())
      {
        LogUtil.warn("Water image not found:" + pressImg);
        return;
      }
      Image src_press = ImageIO.read(file_press);
      int wideth_press = src_press.getWidth(null);
      int height_press = src_press.getHeight(null);
      
      int x = 0;int y = 0;
      int bianju = 20;
      int[][][] positions = {
        { { bianju, bianju }, { (wideth - wideth_press) / 2, bianju }, { wideth - wideth_press - bianju, bianju } }, 
        { { bianju, (height - height_press) / 2 }, { (wideth - wideth_press) / 2, (height - height_press) / 2 }, 
        { wideth - wideth_press - bianju, (height - height_press) / 2 } }, 
        { { bianju, height - height_press - bianju }, { (wideth - wideth_press) / 2, height - height_press - bianju }, 
        { wideth - wideth_press - bianju, height - height_press - bianju } } };
      if (position == 0) {
        position = NumberUtil.getRandomInt(9) + 1;
      }
      x = positions[((position - 1) / 3)][((position - 1) % 3)][0];
      y = positions[((position - 1) / 3)][((position - 1) % 3)][1];
      
      g.drawImage(src_press, x, y, wideth_press, height_press, null);
      
      g.dispose();
      FileOutputStream out = new FileOutputStream(targetImg);
//      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//      encoder.encode(image);
      out.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static final void pressImage(String targetImg, String pressImg, float opicty, int x, int y)
  {
    try
    {
      File file = new File(targetImg);
      Image src = ImageIO.read(file);
      int wideth = src.getWidth(null);
      int height = src.getHeight(null);
      if ((wideth <= 300) && (height <= 300)) {
        return;
      }
      BufferedImage image = new BufferedImage(wideth, height, 1);
      Graphics2D g = image.createGraphics();
      g.drawImage(src, 0, 0, wideth, height, null);
      
      File file_press = new File(pressImg);
      if (!file_press.exists())
      {
        LogUtil.warn("Water image not found:" + pressImg);
        return;
      }
      Image src_press = ImageIO.read(file_press);
      int wideth_press = src_press.getWidth(null);
      int height_press = src_press.getHeight(null);
      g.setComposite(AlphaComposite.getInstance(10, opicty));
      g.drawImage(src_press, x, y, wideth_press, height_press, null);
      
      g.dispose();
      FileOutputStream out = new FileOutputStream(targetImg);
//      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//      encoder.encode(image);
      out.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static final void pressImage(String targetImg, String pressImg)
  {
    pressImage(targetImg, pressImg, 9);
  }
  
  public static void pressText(String targetImg, String pressText, Font font, Color color, float opicty, int x, int y)
    throws IOException
  {
    Mapx<TextAttribute, Object> attr = new Mapx();
    attr.put(TextAttribute.FONT, font);
    pressText(targetImg, pressText, Font.getFont(attr), color, 0, null, opicty, x, y);
  }
  
  public static void pressText(String targetImg, String pressText, Font font, Color color, int outlineSize, Color outlineColor, float opicty, int x, int y)
  {
    try
    {
      File _file = new File(targetImg);
      Image src = ImageIO.read(_file);
      int width = src.getWidth(null);
      int height = src.getHeight(null);
      if ((width <= 300) && (height <= 300)) {
        return;
      }
      BufferedImage image = new BufferedImage(width, height, 1);
      Graphics2D g = image.createGraphics();
      g.drawImage(src, 0, 0, width, height, null);
      g.setComposite(AlphaComposite.getInstance(10, opicty));
      FontRenderContext frc = g.getFontRenderContext();
      TextLayout tl = new TextLayout(pressText, font, frc);
      Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(x, y));
      if ((outlineColor != null) && (outlineSize > 0))
      {
        g.setColor(outlineColor);
        g.setStroke(new BasicStroke(outlineSize));
        g.draw(sha);
      }
      g.setColor(color);
      g.setFont(font);
      g.drawString(pressText, x, y);
      
      g.dispose();
      FileOutputStream out = new FileOutputStream(targetImg);
//      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//      JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
//      param.setQuality(0.95F, false);
//      encoder.setJPEGEncodeParam(param);
//      encoder.encode(image);
      out.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void transform(File src, File dest)
  {
    transform(src, dest, 1600);
  }
  
  public static void transform(File src, File dest, int nw)
  {
    try
    {
      AffineTransform transform = new AffineTransform();
      BufferedImage bis = ImageIO.read(src);
      int w = bis.getWidth();
      int h = bis.getHeight();
      int nh = nw * h / w;
      double sx = nw / w;
      double sy = nh / h;
      transform.setToScale(sx, sy);
      AffineTransformOp ato = new AffineTransformOp(transform, null);
      BufferedImage bid = new BufferedImage(nw, nh, 5);
      ato.filter(bis, bid);
      ImageIO.write(bid, "jpg", dest);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void cutting(String src, String dest, int x, int y, int w, int h)
  {
    try
    {
      File srcFile = new File(src);
      String ext = FileUtil.getExtension(src);
      
      BufferedImage bi = ImageIO.read(srcFile);
      int srcWidth = bi.getWidth();
      int srcHeight = bi.getHeight();
      if ((srcWidth >= w) && (srcHeight >= h))
      {
        Image image = bi.getScaledInstance(srcWidth, srcHeight, 1);
        
        ImageFilter cropFilter = new CropImageFilter(x, y, w, h);
        Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
        int type = 1;
        if (("gif".equalsIgnoreCase(ext)) || ("png".equalsIgnoreCase(ext))) {
          type = 2;
        }
        BufferedImage tag = new BufferedImage(w, h, type);
        Graphics2D g = (Graphics2D)tag.getGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, null);
        g.dispose();
        
        ImageIO.write(tag, ext, new File(dest));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void rotate(String src, String dest, int degree)
  {
    try
    {
      BufferedImage image = readImage(src);
      
      int iw = image.getWidth();
      int ih = image.getHeight();
      int w = 0;
      int h = 0;
      int x = 0;
      int y = 0;
      
      degree %= 360;
      if (degree < 0) {
        degree += 360;
      }
      double ang = Math.toRadians(degree);
      if ((degree == 180) || (degree == 0) || (degree == 360))
      {
        w = iw;
        h = ih;
      }
      else if ((degree == 90) || (degree == 270))
      {
        w = ih;
        h = iw;
      }
      else
      {
        int d = iw + ih;
        w = (int)(d * Math.abs(Math.cos(ang)));
        h = (int)(d * Math.abs(Math.sin(ang)));
      }
      x = w / 2 - iw / 2;
      y = h / 2 - ih / 2;
      
      String ext = FileUtil.getExtension(src);
      int type = 1;
      if (("gif".equalsIgnoreCase(ext)) || ("png".equalsIgnoreCase(ext))) {
        type = 2;
      }
      BufferedImage rotatedImage = new BufferedImage(w, h, type);
      Graphics2D gs = (Graphics2D)rotatedImage.getGraphics();
      
      rotatedImage = gs.getDeviceConfiguration().createCompatibleImage(w, h, 1);
      AffineTransform at = new AffineTransform();
      at.rotate(ang, w / 2, h / 2);
      at.translate(x, y);
      
      AffineTransformOp op = new AffineTransformOp(at, 3);
      op.filter(image, rotatedImage);
      
      ImageIO.write(rotatedImage, ext, new File(dest));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void flip(String src, String dest, boolean flipX)
  {
    try
    {
      BufferedImage image = readImage(src);
      int w = image.getWidth();
      int h = image.getHeight();
      
      String ext = FileUtil.getExtension(src);
      int type = 1;
      if (("gif".equalsIgnoreCase(ext)) || ("png".equalsIgnoreCase(ext))) {
        type = 2;
      }
      BufferedImage rotatedImage = new BufferedImage(w, h, type);
      Graphics2D gs = (Graphics2D)rotatedImage.getGraphics();
      
      rotatedImage = gs.getDeviceConfiguration().createCompatibleImage(w, h, 1);
      AffineTransform transform;
      if (flipX) {
        transform = new AffineTransform(-1.0F, 0.0F, 0.0F, 1.0F, w, 0.0F);
      } else {
        transform = new AffineTransform(1.0F, 0.0F, 0.0F, -1.0F, 0.0F, h);
      }
      AffineTransformOp op = new AffineTransformOp(transform, 2);
      op.filter(image, rotatedImage);
      
      ImageIO.write(rotatedImage, ext, new File(dest));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  private static Mapx<String, Font> userFonts = new Mapx();
  private static long fontDirLastModify = 0L;
  
  public static String[] getSystemFontNames()
  {
    return GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
  }
  
  public static Mapx<String, Font> getUserFonts()
  {
    File dir = new File(Config.getContextRealPath() + "WEB-INF/fonts/");
    if (!dir.exists()) {
      return userFonts;
    }
    if (dir.lastModified() > fontDirLastModify)
    {
      File[] fontFiles = dir.listFiles(new FilenameFilter()
      {
        public boolean accept(File dir, String name)
        {
          return name.matches("(?i).+\\.(ttf|ttc)");
        }
      });
      if ((fontFiles == null) || (fontFiles.length <= 0)) {
        return userFonts;
      }
      userFonts = new Mapx();
      for (int i = 0; i < fontFiles.length; i++) {
        try
        {
          Font f = Font.createFont(0, fontFiles[i]);
          userFonts.put(f.getFamily().toLowerCase(), f);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
      fontDirLastModify = dir.lastModified();
    }
    return userFonts;
  }
  
  public static String[] getAllFontNames()
  {
    String[] userFontNames = (String[])getUserFonts().keySet().toArray(new String[0]);
    String[] systemFontNames = getSystemFontNames();
    String[] fontNames = new String[userFontNames.length + systemFontNames.length];
    System.arraycopy(userFontNames, 0, fontNames, 0, userFontNames.length);
    System.arraycopy(systemFontNames, 0, fontNames, userFontNames.length, systemFontNames.length);
    return fontNames;
  }
  
  public static Font getFont(String fontName)
  {
    Font font = null;
    if ((fontName == null) || (fontName.trim().length() == 0)) {
      return null;
    }
    fontName = fontName.trim();
    if (!fontName.startsWith("_S_"))
    {
      if (fontName.startsWith("_U_")) {
        fontName = fontName.substring(3);
      }
      font = (Font)getUserFonts().get(fontName.toLowerCase());
    }
    if ((font == null) && (!fontName.startsWith("_U_")))
    {
      if (fontName.startsWith("_S_")) {
        fontName = fontName.substring(3);
      }
      font = new Font(fontName, 0, 1);
      if ((font != null) && (font.getFamily() == new Font(null, 0, 1).getFamily()) && 
        (!fontName.equalsIgnoreCase(font.getFamily()))) {
        return null;
      }
    }
    return font;
  }
  
  public static Font[] getFonts(String fontName)
  {
    Set<Font> fontSet = new LinkedHashSet();
    Font font = getFont(fontName);
    if (font != null) {
      fontSet.add(font);
    }
    fontSet.add(new Font(null, 0, 1));
    fontSet.addAll(getUserFonts().values());
    return (Font[])fontSet.toArray(new Font[0]);
  }
  
  public static Set<String> getAvailableFormat()
  {
    Set<String> s = new HashSet();
    String[] writeFormates = ImageIO.getWriterFormatNames();
    for (int i = 0; i < writeFormates.length; i++)
    {
      String formatName = writeFormates[i].toUpperCase();
      if (formatName.contains(ImageFormat.getDesc(ImageFormat.BMP))) {
        formatName = ImageFormat.getDesc(ImageFormat.BMP);
      }
      s.add(formatName);
    }
    return s;
  }
  
  public static void txtToImage(String dest, String txt, Font font, Color fontColor, Color bgColor)
    throws Exception
  {
    txtToImage(new File(dest), txt, font, fontColor, bgColor);
  }
  
  public static void txtToImage(File dest, String txt, Font font, Color fontColor, Color bgColor)
    throws Exception
  {
    Set<String> availbleformats = getAvailableFormat();
    String ext = FileUtil.getExtension(dest.getName());
    ImageFormat iFormat = ImageFormat.getImageFormat(ext);
    if (iFormat == ImageFormat.UNKNOWN) {
      throw new RuntimeException("Unknow image suffix: " + ext);
    }
    String formatName = ImageFormat.getDesc(iFormat);
    if (!availbleformats.contains(formatName)) {
      formatName = ImageFormat.getDesc(ImageFormat.JPEG);
    }
    BufferedImage bi = txtToImage(txt, font, fontColor, bgColor, "center center", 0, 0, 0, 0, 0, ImageFormat.JPEG);
    ImageIO.write(bi, formatName, dest);
  }
  
  public static void txtToImage(OutputStream out, String txt, Font font, Color fontColor, Color bgColor, ImageFormat imageFormat)
    throws Exception
  {
    Set<String> availbleformats = getAvailableFormat();
    if (imageFormat == ImageFormat.UNKNOWN) {
      throw new RuntimeException("Unsupport image format type 'UNKNOW'!");
    }
    String formatName = ImageFormat.getDesc(imageFormat);
    if (!availbleformats.contains(formatName)) {
      formatName = ImageFormat.getDesc(ImageFormat.JPEG);
    }
    BufferedImage bi = txtToImage(txt, font, fontColor, bgColor, "center center", 0, 0, 0, 0, 0, ImageFormat.JPEG);
    ImageIO.write(bi, formatName, out);
  }
  
  public static BufferedImage txtToImage(String text, Font font, Color fontColor, Color bgColor, String align, int fontWidth, int fontHeight, int letterSpacing, int imageWidth, int imageHeight, ImageFormat imageFormat)
  {
    if (StringUtil.isEmpty(text)) {
      return null;
    }
    bgColor = bgColor == null ? Color.WHITE : bgColor;
    fontColor = fontColor == null ? Color.BLACK : fontColor;
    float fontSize = font.getSize() <= 0 ? 36.0F : font.getSize();
    align = align == null ? "center center" : align;
    imageFormat = imageFormat == null ? ImageFormat.JPEG : imageFormat;
    Font[] fonts = getFonts(font.getFontName());
    
    double xScaleModule = 1.0D;
    double yScaleModule = 1.0D;
    FontRenderContext frc = new FontRenderContext(null, true, false);
    double maxCharWidth = 0.0D;
    double maxCharHeight = 0.0D;
    int textLength = text.length();
    String[] chars = new String[textLength];
    Font[] charFonts = new Font[textLength];
    double[] charWidths = new double[textLength];
    char[] textChars = text.toCharArray();
    for (int i = 0; i < textLength; i++) {
      chars[i] = String.valueOf(textChars[i]);
    }
    Arrays.fill(charFonts, fonts[0]);
    if (fonts[0].canDisplayUpTo(text) != -1) {
      for (int i = 0; i < textLength; i++) {
        for (int j = 0; j < fonts.length; j++) {
          if (fonts[j].canDisplay(textChars[i]))
          {
            charFonts[i] = fonts[j];
            break;
          }
        }
      }
    }
    for (int i = 0; i < charFonts.length; i++)
    {
      charFonts[i] = charFonts[i].deriveFont(font.getStyle());
      if (fontSize != 0.0F) {
        charFonts[i] = charFonts[i].deriveFont(fontSize);
      }
    }
    for (int i = 0; i < textLength; i++)
    {
      Rectangle2D charBound = charFonts[i].createGlyphVector(frc, chars[i]).getGlyphOutline(0).getBounds2D();
      double width = charBound.getWidth();
      double height = charBound.getHeight();
      if (width > maxCharWidth) {
        maxCharWidth = width;
      }
      if (height > maxCharHeight) {
        maxCharHeight = height;
      }
    }
    if ((fontWidth != 0) && (fontHeight != 0))
    {
      xScaleModule = fontWidth / maxCharWidth;
      yScaleModule = fontHeight / maxCharHeight;
    }
    else if (fontWidth != 0)
    {
      xScaleModule = fontWidth / maxCharWidth;
      yScaleModule = fontSize == 0.0F ? xScaleModule : 1.0D;
    }
    else if (fontHeight != 0)
    {
      yScaleModule = fontHeight / maxCharHeight;
      xScaleModule = fontSize == 0.0F ? yScaleModule : 1.0D;
    }
    Mapx<TextAttribute, TransformAttribute> attr = new Mapx();
    attr.put(TextAttribute.TRANSFORM, new TransformAttribute(AffineTransform.getScaleInstance(xScaleModule, yScaleModule)));
    for (int i = 0; i < textLength; i++) {
      charFonts[i] = charFonts[i].deriveFont(attr);
    }
    double boundWidth = textLength * letterSpacing;
    double boundHeight = 0.0D;
    double boundY = 0.0D;
    for (int i = 0; i < textLength; i++)
    {
      Rectangle2D charBound2 = charFonts[i].getStringBounds(chars[i], frc);
      double width = charBound2.getWidth();
      double height = charBound2.getHeight();
      double y = charBound2.getY();
      charWidths[i] = width;
      boundWidth += width;
      if (height > boundHeight) {
        boundHeight = height;
      }
      if (Math.abs(y) > Math.abs(boundY)) {
        boundY = y;
      }
    }
    int width = imageWidth != 0 ? imageWidth : (int)boundWidth;
    int height = imageHeight != 0 ? imageHeight : (int)boundHeight;
    
    int xPoint = 0;
    int yPoint = 0;
    String[] aligns;
    if (align.indexOf(",") != -1) {
      aligns = align.trim().split(",");
    } else {
      aligns = align.trim().split(" +");
    }
    if (aligns[0].equalsIgnoreCase("left")) {
      xPoint = 0;
    } else if (aligns[0].equalsIgnoreCase("center")) {
      xPoint = (int)(width - boundWidth) / 2;
    } else if (aligns[0].equalsIgnoreCase("right")) {
      xPoint = (int)(width - boundWidth);
    } else {
      try
      {
        xPoint = Integer.parseInt(aligns[0]);
      }
      catch (Exception e)
      {
        xPoint = 0;
      }
    }
    if (aligns.length > 1)
    {
      if (aligns[1].equalsIgnoreCase("top")) {
        yPoint = (int)-boundY;
      } else if (aligns[1].equalsIgnoreCase("center")) {
        yPoint = (int)((height - boundHeight) / 2.0D - boundY);
      } else if (aligns[1].equalsIgnoreCase("bottom")) {
        yPoint = (int)(height - boundHeight - boundY);
      } else {
        try
        {
          yPoint = (int)(Integer.parseInt(aligns[1]) - boundY);
        }
        catch (Exception e)
        {
          yPoint = (int)-boundY;
        }
      }
    }
    else {
      yPoint = (int)((height - boundHeight) / 2.0D - boundY);
    }
    BufferedImage image = new BufferedImage(width, height, 1);
    Graphics2D g2i = image.createGraphics();
    g2i.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2i.setPaint(bgColor);
    g2i.fillRect(0, 0, width, 2 * height);
    g2i.setPaint(fontColor);
    double x = xPoint;
    for (int i = 0; i < textLength; i++)
    {
      g2i.setFont(charFonts[i]);
      g2i.drawString(chars[i], (int)x, yPoint);
      x += charWidths[i] + letterSpacing;
    }
    g2i.dispose();
    return image;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    txtToImage("e:/testimage/asdfasdf.jpg", "asdf", new Font("宋体", 1, 28), Color.black, Color.white);
  }
}
