package org.ark.framework.media;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NodeList;

import com.arkxos.framework.Config;
import com.arkxos.framework.commons.util.NumberUtil;

public class ImageUtil {
	public static final int NorthWest = 1;
	public static final int North = 2;
	public static final int NorthEast = 3;
	public static final int West = 4;
	public static final int Center = 5;
	public static final int East = 6;
	public static final int SouthWest = 7;
	public static final int South = 8;
	public static final int SouthEast = 9;

	public static boolean isCMYK(String fileName) throws Exception {
		if ((!fileName.toLowerCase().endsWith(".jpg")) && (!fileName.toLowerCase().endsWith(".jpeg"))) {
			return false;
		}
		File file = new File(fileName);
		ImageInputStream input = ImageIO.createImageInputStream(file);
		Iterator readers = ImageIO.getImageReaders(input);
		if ((readers == null) || (!readers.hasNext())) {
			throw new RuntimeException("No ImageReaders found");
		}

		ImageReader reader = (ImageReader) readers.next();
		reader.setInput(input);
		String format = reader.getFormatName();

		if (("JPEG".equalsIgnoreCase(format)) || ("JPG".equalsIgnoreCase(format))) {
			IIOMetadata metadata = reader.getImageMetadata(0);
			String metadataFormat = metadata.getNativeMetadataFormatName();
			IIOMetadataNode iioNode = (IIOMetadataNode) metadata.getAsTree(metadataFormat);
			NodeList children = iioNode.getElementsByTagName("app14Adobe");
			if (children.getLength() > 0) {
				return true;
			}
		}
		return false;
	}

	public static int[] getPosition(String targetImg, String pressText, int fontSize, int position) throws IOException {
		File _file = new File(targetImg);
		Image src = ImageIO.read(_file);
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		int x;
		int y;
		if (position == 0) {
			 x = NumberUtil.getRandomInt(width);
			x = x < fontSize * 2 ? fontSize * 2 : x;
			 y = NumberUtil.getRandomInt(height);
			y = y < fontSize * 2 / 2 ? fontSize * 2 : y;
		} else {
			x = width * ((position - 1) % 3) / 3 + fontSize * 2;
			y = width * ((position - 1) / 3) / 3 + fontSize * 2;
		}
		if (x > width - fontSize * pressText.length() * 4 / 3) {
			x = width - fontSize * pressText.length() * 4 / 3;
		}
		if (y > height - fontSize) {
			y = height - fontSize;
		}
		return new int[] { x, y };
	}

	public static boolean existsGraphicsMagick() {
		return "1".equals(Config.getValue("ImageLibType"));
	}

	public static Dimension getDimension(String fileName) throws Exception {
		File file = new File(fileName);
		ImageInputStream input = ImageIO.createImageInputStream(file);
		Iterator readers = ImageIO.getImageReaders(input);
		if ((readers == null) || (!readers.hasNext())) {
			throw new RuntimeException("No ImageReaders found");
		}
		ImageReader reader = (ImageReader) readers.next();
		reader.setInput(input);
		return new Dimension(reader.getWidth(0), reader.getHeight(0));
	}

	public static void scaleRateImageFile(String fromFileName, String toFileName, int toWidth, int toHeight) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.scaleRateImageFile(fromFileName, toFileName, toWidth, toHeight);
		else
			SimpleImageUtil.scale(fromFileName, toFileName, toWidth, toHeight);
	}

	public static void scaleRateImageFile(String fromFileName, String toFileName, double rate) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.scaleRateImageFile(fromFileName, toFileName, rate);
		else
			SimpleImageUtil.scale(fromFileName, toFileName, rate);
	}

	public static void scaleFixedImageFile(String fromFileName, String toFileName, int toWidth, int toHeight) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.scaleFixedImageFile(fromFileName, toFileName, toWidth, toHeight);
		else
			SimpleImageUtil.scale(fromFileName, toFileName, toWidth, toHeight);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, float opicty, int position) throws Exception {
		int[] xy = getPosition(filePath, pressText, font.getSize(), position);
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, opicty, xy[0], xy[1]);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, opicty, xy[0], xy[1]);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, Color outlineColor, int outlineSize, float opicty, int position) throws Exception {
		int[] xy = getPosition(filePath, pressText, font.getSize(), position);
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, opicty, xy[0], xy[1]);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, opicty, xy[0], xy[1]);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, Color shadowColor, float opicty, int position) throws Exception {
		int[] xy = getPosition(filePath, pressText, font.getSize(), position);
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, xy[0], xy[1]);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, xy[0], xy[1]);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, float opicty, int x, int y) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, opicty, x, y);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, opicty, x, y);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, Color outlineColor, int outlineSize, float opicty, int x, int y) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, opicty, x, y);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, opicty, x, y);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, Color shadowColor, float opicty, int x, int y) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, x, y);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, x, y);
	}

	public static final void pressImage(String src, String pressImg, float opicty, int x, int y) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.pressImage(src, pressImg, x);
		else
			ImageJDKUtil.pressImage(src, pressImg, opicty, x, y);
	}

	public static final void pressImage(String src, String pressImg, int position) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.pressImage(src, pressImg, position);
		else
			ImageJDKUtil.pressImage(src, pressImg, position);
	}

	public static final void cuttingImage(String src, String dest, int x, int y, int w, int h) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.cutting(src, dest, x, y, w, h);
		else
			ImageJDKUtil.cutting(src, dest, x, y, w, h);
	}

	public static final void rotateImageFile(String src, String dest, int degree) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.rotate(src, dest, degree);
		else
			ImageJDKUtil.rotate(src, dest, degree);
	}

	public static final void flipImageFile(String src, String dest, boolean flipX) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.flip(src, dest, flipX);
		else
			ImageJDKUtil.flip(src, dest, flipX);
	}
}