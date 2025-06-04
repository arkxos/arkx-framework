package io.arkx.framework.media;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.FileInputStream;
import java.io.IOException;

public class BmpUtil
{
  public static int constructInt(byte[] in, int offset)
  {
    int ret = in[(offset + 3)] & 0xFF;
    ret = ret << 8 | in[(offset + 2)] & 0xFF;
    ret = ret << 8 | in[(offset + 1)] & 0xFF;
    ret = ret << 8 | in[(offset + 0)] & 0xFF;
    return ret;
  }
  
  public static int constructInt3(byte[] in, int offset)
  {
    int ret = 255;
    ret = ret << 8 | in[(offset + 2)] & 0xFF;
    ret = ret << 8 | in[(offset + 1)] & 0xFF;
    ret = ret << 8 | in[(offset + 0)] & 0xFF;
    return ret;
  }
  
  public static long constructLong(byte[] in, int offset)
  {
    long ret = in[(offset + 7)] & 0xFF;
    ret |= ret << 8 | in[(offset + 6)] & 0xFF;
    ret |= ret << 8 | in[(offset + 5)] & 0xFF;
    ret |= ret << 8 | in[(offset + 4)] & 0xFF;
    ret |= ret << 8 | in[(offset + 3)] & 0xFF;
    ret |= ret << 8 | in[(offset + 2)] & 0xFF;
    ret |= ret << 8 | in[(offset + 1)] & 0xFF;
    ret |= ret << 8 | in[(offset + 0)] & 0xFF;
    return ret;
  }
  
  public static double constructDouble(byte[] in, int offset)
  {
    long ret = constructLong(in, offset);
    return Double.longBitsToDouble(ret);
  }
  
  public static short constructShort(byte[] in, int offset)
  {
    short ret = (short)((short)in[(offset + 1)] & 0xFF);
    ret = (short)(ret << 8 | (short)((short)in[(offset + 0)] & 0xFF));
    return ret;
  }
  
  static class BitmapHeader
  {
    public int nsize;
    public int nbisize;
    public int nwidth;
    public int nheight;
    public int nplanes;
    public int nbitcount;
    public int ncompression;
    public int nsizeimage;
    public int nxpm;
    public int nypm;
    public int nclrused;
    public int nclrimp;
    
    public void read(FileInputStream fs)
      throws IOException
    {
      int bflen = 14;
      byte[] bf = new byte[14];
      fs.read(bf, 0, 14);
      int bilen = 40;
      byte[] bi = new byte[40];
      fs.read(bi, 0, 40);
      
      this.nsize = BmpUtil.constructInt(bf, 2);
      this.nbisize = BmpUtil.constructInt(bi, 2);
      this.nwidth = BmpUtil.constructInt(bi, 4);
      this.nheight = BmpUtil.constructInt(bi, 8);
      this.nplanes = BmpUtil.constructShort(bi, 12);
      
      this.nbitcount = BmpUtil.constructShort(bi, 14);
      
      this.ncompression = BmpUtil.constructInt(bi, 16);
      this.nsizeimage = BmpUtil.constructInt(bi, 20);
      if (this.nsizeimage == 0) {
        this.nsizeimage = this.nsize;
      }
      this.nxpm = BmpUtil.constructInt(bi, 24);
      this.nypm = BmpUtil.constructInt(bi, 28);
      this.nclrused = BmpUtil.constructInt(bi, 32);
      this.nclrimp = BmpUtil.constructInt(bi, 36);
    }
  }
  
  public static BufferedImage read(FileInputStream fs)
  {
    try
    {
      BitmapHeader bh = new BitmapHeader();
      bh.read(fs);
      Image image = null;
      if (bh.nbitcount == 24) {
        image = readMap24(fs, bh);
      }
      if (bh.nbitcount == 32) {
        image = readMap32(fs, bh);
      }
      if (bh.nbitcount == 8) {
        image = readMap8(fs, bh);
      }
      if (bh.nbitcount == 1) {
        image = readMap1(fs, bh);
      }
      fs.close();
      image = toBufferedImage(image);
      return (BufferedImage)image;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  private static Image readMap1(FileInputStream fs, BitmapHeader bh)
    throws IOException
  {
    int nNumColors = 0;
    if (bh.nclrused > 0) {
      nNumColors = bh.nclrused;
    } else {
      nNumColors = 1 << bh.nbitcount;
    }
    int[] npalette = new int[nNumColors];
    byte[] bpalette = new byte[nNumColors * 4];
    fs.read(bpalette, 0, nNumColors * 4);
    int nindex1 = 0;
    for (int n = 0; n < nNumColors; n++)
    {
      npalette[n] = 
      
        (0xFF000000 | (bpalette[(nindex1 + 2)] & 0xFF) << 16 | (bpalette[(nindex1 + 1)] & 0xFF) << 8 | bpalette[nindex1] & 0xFF);
      nindex1 += 4;
    }
    int scanLineSize = (bh.nwidth * bh.nbitcount + 31 & 0xFFFFFFE0) >> 3;
    
    int[] ndata1 = new int[bh.nwidth * bh.nheight];
    
    byte[] blinedata = new byte[scanLineSize];
    
    nindex1 = 0;
    for (int j1 = 0; j1 < bh.nheight; j1++)
    {
      fs.read(blinedata, 0, scanLineSize);
      nindex1 = 0;
      for (int i1 = 0; i1 < bh.nwidth; i1++)
      {
        if (bh.nwidth * (bh.nheight - j1 - 1) + i1 > bh.nwidth * bh.nheight - 1) {
          break;
        }
        if (nindex1 > scanLineSize * bh.nheight - 1) {
          break;
        }
        for (int pixPerByte = 0; pixPerByte < 8; pixPerByte++)
        {
          int shift = 8 - pixPerByte - 1;
          ndata1[(bh.nwidth * (bh.nheight - j1 - 1) + i1)] = npalette[(blinedata[nindex1] >> shift & 0x1)];
          if (pixPerByte != 7)
          {
            i1++;
            if (i1 >= bh.nwidth) {
              break;
            }
          }
        }
        nindex1++;
      }
    }
    Image image = Toolkit.getDefaultToolkit().createImage(
      new MemoryImageSource(bh.nwidth, bh.nheight, ndata1, 0, 
      bh.nwidth));
    fs.close();
    return image;
  }
  
  protected static Image readMap32(FileInputStream fs, BitmapHeader bh)
    throws IOException
  {
    int[] ndata = new int[bh.nheight * bh.nwidth];
    byte[] brgb = new byte[bh.nwidth * 4 * bh.nheight];
    fs.read(brgb, 0, bh.nwidth * 4 * bh.nheight);
    int nindex = 0;
    for (int j = 0; j < bh.nheight; j++) {
      for (int i = 0; i < bh.nwidth; i++)
      {
        ndata[(bh.nwidth * (bh.nheight - j - 1) + i)] = constructInt3(brgb, nindex);
        nindex += 4;
      }
    }
    Image image = Toolkit.getDefaultToolkit().createImage(
      new MemoryImageSource(bh.nwidth, bh.nheight, ndata, 0, bh.nwidth));
    fs.close();
    return image;
  }
  
  protected static Image readMap24(FileInputStream fs, BitmapHeader bh)
    throws IOException
  {
    if (bh.nsizeimage == 0)
    {
      bh.nsizeimage = ((bh.nwidth * bh.nbitcount + 31 & 0xFFFFFFE0) >> 3);
      bh.nsizeimage *= bh.nheight;
    }
    int npad = bh.nsizeimage / bh.nheight - bh.nwidth * 3;
    int[] ndata = new int[bh.nheight * bh.nwidth];
    byte[] brgb = new byte[(bh.nwidth + npad) * 3 * bh.nheight];
    fs.read(brgb, 0, (bh.nwidth + npad) * 3 * bh.nheight);
    int nindex = 0;
    for (int j = 0; j < bh.nheight; j++)
    {
      for (int i = 0; i < bh.nwidth; i++)
      {
        ndata[(bh.nwidth * (bh.nheight - j - 1) + i)] = constructInt3(brgb, nindex);
        nindex += 3;
      }
      nindex += npad;
    }
    Image image = Toolkit.getDefaultToolkit().createImage(
      new MemoryImageSource(bh.nwidth, bh.nheight, ndata, 0, bh.nwidth));
    fs.close();
    return image;
  }
  
  protected static Image readMap8(FileInputStream fs, BitmapHeader bh)
    throws IOException
  {
    int nNumColors = 0;
    if (bh.nclrused > 0) {
      nNumColors = bh.nclrused;
    } else {
      nNumColors = 1 << bh.nbitcount;
    }
    if (bh.nsizeimage == 0)
    {
      bh.nsizeimage = ((bh.nwidth * bh.nbitcount + 31 & 0xFFFFFFE0) >> 3);
      bh.nsizeimage *= bh.nheight;
    }
    int[] npalette = new int[nNumColors];
    byte[] bpalette = new byte[nNumColors * 4];
    fs.read(bpalette, 0, nNumColors * 4);
    int nindex8 = 0;
    for (int n = 0; n < nNumColors; n++)
    {
      npalette[n] = constructInt3(bpalette, nindex8);
      nindex8 += 4;
    }
    int npad8 = bh.nsizeimage / bh.nheight - bh.nwidth;
    int[] ndata8 = new int[bh.nwidth * bh.nheight];
    byte[] bdata = new byte[(bh.nwidth + npad8) * bh.nheight];
    fs.read(bdata, 0, (bh.nwidth + npad8) * bh.nheight);
    nindex8 = 0;
    for (int j8 = 0; j8 < bh.nheight; j8++)
    {
      for (int i8 = 0; i8 < bh.nwidth; i8++)
      {
        ndata8[(bh.nwidth * (bh.nheight - j8 - 1) + i8)] = npalette[(bdata[nindex8] & 0xFF)];
        nindex8++;
      }
      nindex8 += npad8;
    }
    Image image = Toolkit.getDefaultToolkit().createImage(
      new MemoryImageSource(bh.nwidth, bh.nheight, ndata8, 0, bh.nwidth));
    return image;
  }
  
  public static BufferedImage read(String sdir, String sfile)
  {
    return read(sdir + sfile);
  }
  
  public static BufferedImage read(String sdir)
  {
    try
    {
      FileInputStream fs = new FileInputStream(sdir);
      return read(fs);
    }
    catch (IOException ex) {}
    return null;
  }
  
  public static BufferedImage toBufferedImage(Image image)
  {
    if ((image instanceof BufferedImage)) {
      return (BufferedImage)image;
    }
    BufferedImage bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), 
      2);
    Graphics2D g2d = bimage.createGraphics();
    g2d.drawImage(image, 0, 0, null);
    return bimage;
  }
  
  public static boolean hasAlpha(Image image)
  {
    if ((image instanceof BufferedImage))
    {
      BufferedImage bimage = (BufferedImage)image;
      return bimage.getColorModel().hasAlpha();
    }
    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    try
    {
      pg.grabPixels();
    }
    catch (InterruptedException localInterruptedException) {}
    ColorModel cm = pg.getColorModel();
    return cm.hasAlpha();
  }
  
  public static void main(String[] args)
    throws IOException
  {
    BufferedImage img = read("F:/Document/My Pictures/1169106248148.bmp");
    img = ImageJDKUtil.scaleFixed(img, 100, 100, true);
    ImageJDKUtil.writeImageFile("F:/Document/My pictures/1.jpg", img);
  }
}
