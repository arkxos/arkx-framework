package org.ark.framework.media;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.NumberUtil;

public class ImageJDKUtil {

    public static BufferedImage scaleRate(BufferedImage srcImage, double rate) {
        return scaleRate(srcImage, rate, rate, null);
    }

    public static BufferedImage scaleRate(BufferedImage srcImage, int width, int height) {
        double w = srcImage.getWidth();
        double h = srcImage.getHeight();
        if ((w < width) && (h < height)) {
            return srcImage;
        }
        if (height == 0) {
            if (w <= width) {
                return srcImage;
            }
            return scaleRate(srcImage, width / w, width / w, null);
        }
        if (width == 0) {
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

    public static BufferedImage gray(BufferedImage srcImage) {
        BufferedImage dstImage = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());
        Graphics2D g2 = dstImage.createGraphics();
        RenderingHints hints = g2.getRenderingHints();
        g2.dispose();
        ColorSpace grayCS = ColorSpace.getInstance(1003);
        ColorConvertOp colorConvertOp = new ColorConvertOp(grayCS, hints);
        colorConvertOp.filter(srcImage, dstImage);
        return dstImage;
    }

    public static BufferedImage scaleRate(BufferedImage srcImage, double xscale, double yscale, RenderingHints hints) {
        AffineTransform transform = AffineTransform.getScaleInstance(xscale, yscale);
        AffineTransformOp op = new AffineTransformOp(transform, 1);
        return op.filter(srcImage, null);
    }

    public static BufferedImage scaleFixed(BufferedImage srcImage, int width, int height, boolean keepRate) {
        int srcWidth = srcImage.getWidth();
        int srcHeight = srcImage.getHeight();
        double wScale = width * 1.0D / srcWidth;
        double hScale = height * 1.0D / srcHeight;
        if (keepRate) {
            if ((wScale > hScale) && (hScale != 0.0D))
                wScale = hScale;
            else {
                hScale = wScale;
            }
        }
        ImageScale is = new ImageScale();
        return is.doScale(srcImage, (int) (srcWidth * wScale), (int) (srcHeight * hScale));
    }

    public static void scaleFixedImageFile(String srcFile, String destFile, int width, int height) throws IOException {
        scaleFixedImageFile(srcFile, destFile, width, height, true);
    }

    public static void scaleFixedImageFile(String srcFile, String destFile, int width, int height, boolean keepRate)
            throws IOException {
        try {
            FileInputStream fs = new FileInputStream(srcFile);
            byte[] bf = new byte[20];
            fs.read(bf, 0, 20);
            fs.close();
            String prefix = new String(bf);
            if (prefix.startsWith("GIF8")) {
                try {
                    Dimension dim = ImageUtil.getDimension(srcFile);
                    double sWidth = dim.getWidth();
                    double sHeight = dim.getHeight();
                    GifUtil.resizeByRate(srcFile, destFile, sWidth >= width ? width : 0, sHeight >= height ? height : 0,
                            keepRate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                BufferedImage image = readImage(srcFile);
                BufferedImage newImage = scaleFixed(image, width, height, keepRate);
                writeImageFile(destFile, newImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage readImage(String srcFile) throws IOException {
        BufferedImage src = null;
        FileInputStream fs = new FileInputStream(srcFile);
        byte[] bf = new byte[20];
        fs.read(bf, 0, 20);
        fs.close();
        String prefix = new String(bf);
        if (prefix.startsWith("BM"))
            src = BmpUtil.read(srcFile);
        else {
            src = ImageIO.read(new File(srcFile));
        }
        BufferedImage image = new BufferedImage(src.getWidth(), src.getHeight(), 1);
        Graphics g = image.createGraphics();
        g.drawImage(src, 0, 0, src.getWidth(), src.getHeight(), null);
        return image;
    }

    public static void scaleRateImageFile(String srcFile, String destFile, int width, int height) throws IOException {
        scaleFixedImageFile(srcFile, destFile, width, height, true);
    }

    public static void scaleRateImageFile(String srcFile, String destFile, double rate) throws IOException {
        try {
            if (srcFile.toLowerCase().endsWith(".gif")) {
                try {
                    GifUtil.resizeByRate(srcFile, destFile, rate, rate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                BufferedImage image = readImage(srcFile);
                BufferedImage newImage = scaleRate(image, rate);
                writeImageFile(destFile, newImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void grayImageFile(String srcFile, String destFile) throws IOException {
        writeImageFile(destFile, gray(ImageIO.read(new File(srcFile))));
    }

    public static void writeImageFile(String fileName, BufferedImage image) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        if (fileName.toLowerCase().endsWith(".gif")) {
            throw new RuntimeException("File extension must be .gif");
        }
        if (fileName.toLowerCase().endsWith(".png")) {
            ImageIO.write(image, "png", fos);
        }
        if ((fileName.toLowerCase().endsWith(".jpg")) || (fileName.toLowerCase().endsWith(".jpeg"))) {
            // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            // JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            // param.setQuality(1.0F, false);
            // encoder.encode(image);
            ImageIO.write(image, "png", fos);
        }
        fos.flush();
        fos.close();
    }

    public static final void pressImage(String targetImg, String pressImg, int position) {
        try {
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
            if (!file_press.exists()) {
                LogUtil.warn("Water image not found:" + pressImg);
                return;
            }
            Image src_press = ImageIO.read(file_press);
            int wideth_press = src_press.getWidth(null);
            int height_press = src_press.getHeight(null);

            int x = 0;
            int y = 0;
            int bianju = 20;
            int[][][] positions = {
                    {{bianju, bianju}, {(wideth - wideth_press) / 2, bianju}, {wideth - wideth_press - bianju, bianju}},
                    {{bianju, (height - height_press) / 2}, {(wideth - wideth_press) / 2, (height - height_press) / 2},
                            {wideth - wideth_press - bianju, (height - height_press) / 2}},
                    {{bianju, height - height_press - bianju},
                            {(wideth - wideth_press) / 2, height - height_press - bianju},
                            {wideth - wideth_press - bianju, height - height_press - bianju}}};
            if (position == 0) {
                position = NumberUtil.getRandomInt(9) + 1;
            }
            x = positions[((position - 1) / 3)][((position - 1) % 3)][0];
            y = positions[((position - 1) / 3)][((position - 1) % 3)][1];

            g.drawImage(src_press, x, y, wideth_press, height_press, null);

            g.dispose();
            FileOutputStream out = new FileOutputStream(targetImg);
            // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            // encoder.encode(image);
            ImageIO.write(image, "png", out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void pressImage(String targetImg, String pressImg, float opicty, int x, int y) {
        try {
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
            if (!file_press.exists()) {
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
            // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            // encoder.encode(image);
            ImageIO.write(image, "png", out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void pressImage(String targetImg, String pressImg) {
        pressImage(targetImg, pressImg, 9);
    }

    public static void pressText(String targetImg, String pressText, Font font, Color color, float opicty, int x, int y)
            throws IOException {
        Mapx attr = new Mapx();
        attr.put(TextAttribute.FONT, font);
        pressText(targetImg, pressText, Font.getFont(attr), color, 0, null, opicty, x, y);
    }

    public static void pressText(String targetImg, String pressText, Font font, Color color, int outlineSize,
            Color outlineColor, float opicty, int x, int y) {
        try {
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
            if ((outlineColor != null) && (outlineSize > 0)) {
                g.setColor(outlineColor);
                g.setStroke(new BasicStroke(outlineSize));
                g.draw(sha);
            }
            g.setColor(color);
            g.setFont(font);
            g.drawString(pressText, x, y);

            g.dispose();
            FileOutputStream out = new FileOutputStream(targetImg);
            // JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            // JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);
            // param.setQuality(0.95F, false);
            // encoder.setJPEGEncodeParam(param);
            // encoder.encode(image);
            ImageIO.write(image, "png", out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transform(File src, File dest) {
        transform(src, dest, 1600);
    }

    public static void transform(File src, File dest, int nw) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cutting(String src, String dest, int x, int y, int w, int h) {
        try {
            File srcFile = new File(src);
            String ext = FileUtil.getExtension(src);

            BufferedImage bi = ImageIO.read(srcFile);
            int srcWidth = bi.getWidth();
            int srcHeight = bi.getHeight();
            if ((srcWidth >= w) && (srcHeight >= h)) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight, 1);

                ImageFilter cropFilter = new CropImageFilter(x, y, w, h);
                Image img = Toolkit.getDefaultToolkit()
                        .createImage(new FilteredImageSource(image.getSource(), cropFilter));
                int type = 1;
                if (("gif".equalsIgnoreCase(ext)) || ("png".equalsIgnoreCase(ext))) {
                    type = 2;
                }
                BufferedImage tag = new BufferedImage(w, h, type);
                Graphics2D g = (Graphics2D) tag.getGraphics();

                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(img, 0, 0, null);
                g.dispose();

                ImageIO.write(tag, ext, new File(dest));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rotate(String src, String dest, int degree) {
        try {
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

            if ((degree == 180) || (degree == 0) || (degree == 360)) {
                w = iw;
                h = ih;
            } else if ((degree == 90) || (degree == 270)) {
                w = ih;
                h = iw;
            } else {
                int d = iw + ih;
                w = (int) (d * Math.abs(Math.cos(ang)));
                h = (int) (d * Math.abs(Math.sin(ang)));
            }

            x = w / 2 - iw / 2;
            y = h / 2 - ih / 2;

            String ext = FileUtil.getExtension(src);
            int type = 1;
            if (("gif".equalsIgnoreCase(ext)) || ("png".equalsIgnoreCase(ext))) {
                type = 2;
            }
            BufferedImage rotatedImage = new BufferedImage(w, h, type);
            Graphics2D gs = (Graphics2D) rotatedImage.getGraphics();

            rotatedImage = gs.getDeviceConfiguration().createCompatibleImage(w, h, 1);
            AffineTransform at = new AffineTransform();
            at.rotate(ang, w / 2, h / 2);
            at.translate(x, y);

            AffineTransformOp op = new AffineTransformOp(at, 3);
            op.filter(image, rotatedImage);

            ImageIO.write(rotatedImage, ext, new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flip(String src, String dest, boolean flipX) {
        try {
            BufferedImage image = readImage(src);
            int w = image.getWidth();
            int h = image.getHeight();

            String ext = FileUtil.getExtension(src);
            int type = 1;
            if (("gif".equalsIgnoreCase(ext)) || ("png".equalsIgnoreCase(ext))) {
                type = 2;
            }
            BufferedImage rotatedImage = new BufferedImage(w, h, type);
            Graphics2D gs = (Graphics2D) rotatedImage.getGraphics();

            rotatedImage = gs.getDeviceConfiguration().createCompatibleImage(w, h, 1);
            AffineTransform transform;
            if (flipX)
                transform = new AffineTransform(-1.0F, 0.0F, 0.0F, 1.0F, w - 1, 0.0F);
            else {
                transform = new AffineTransform(1.0F, 0.0F, 0.0F, -1.0F, 0.0F, h - 1);
            }

            AffineTransformOp op = new AffineTransformOp(transform, 2);
            op.filter(image, rotatedImage);

            ImageIO.write(rotatedImage, ext, new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        flip("e:\\test.png", "e:\\test.png", true);
    }

}
