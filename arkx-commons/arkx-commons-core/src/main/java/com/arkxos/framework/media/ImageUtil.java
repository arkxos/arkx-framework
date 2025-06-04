// Decompiled by DJ v3.12.12.101 Copyright 2016 Atanas Neshkov  Date: 2016/5/16 13:33:08
// Home Page:  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ImageUtil.java

package com.arkxos.framework.media;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.NodeList;

import io.arkx.framework.commons.util.NumberUtil;

public class ImageUtil {

	public ImageUtil() {
	}

	public static boolean isCMYK(String fileName) throws Exception {
		if (!fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".jpeg"))
			return false;
		File file = new File(fileName);
		ImageInputStream input = ImageIO.createImageInputStream(file);
		Iterator readers = ImageIO.getImageReaders(input);
		if (readers == null || !readers.hasNext())
			throw new RuntimeException("No ImageReaders found");
		ImageReader reader = (ImageReader) readers.next();
		reader.setInput(input);
		String format = reader.getFormatName();
		if ("JPEG".equalsIgnoreCase(format) || "JPG".equalsIgnoreCase(format)) {
			IIOMetadata metadata = reader.getImageMetadata(0);
			String metadataFormat = metadata.getNativeMetadataFormatName();
			IIOMetadataNode iioNode = (IIOMetadataNode) metadata.getAsTree(metadataFormat);
			NodeList children = iioNode.getElementsByTagName("app14Adobe");
			if (children.getLength() > 0)
				return true;
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
			x = x >= fontSize * 2 ? x : fontSize * 2;
			y = NumberUtil.getRandomInt(height);
			y = y >= (fontSize * 2) / 2 ? y : fontSize * 2;
		} else {
			x = (width * ((position - 1) % 3)) / 3 + fontSize * 2;
			y = (width * ((position - 1) / 3)) / 3 + fontSize * 2;
		}
		if (x > width - (fontSize * pressText.length() * 4) / 3)
			x = width - (fontSize * pressText.length() * 4) / 3;
		if (y > height - fontSize)
			y = height - fontSize;
		return (new int[] { x, y });
	}

	public static Paint getColor(Object color, Color defaultColor) {
		if (color == null)
			return null;
		if (color instanceof Paint)
			return (Paint) color;
		String colorStr = (String) color;
		Color c = null;
		colorStr = colorStr.trim();
		if (colorStr.startsWith("#"))
			c = Color.decode(colorStr);
		else if (colorStr.matches("(?i)rgb\\s*\\(\\s*\\d+\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*\\)")) {
			String rgb[] = colorStr.substring(colorStr.indexOf("(") + 1, colorStr.indexOf(")")).split(",");
			c = new Color(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim()));
		} else {
			try {
				c = (Color) Color.class.getField(colorStr).get(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return c != null ? c : defaultColor;
	}

	public static Dimension getDimension(String fileName) throws Exception {
		File file;
		Dimension d;
		ImageInputStream input;
		ImageReader reader;
		file = new File(fileName);
		d = new Dimension(0, 0);
		input = null;
		reader = null;
		try {
			input = ImageIO.createImageInputStream(file);
			Iterator readers = ImageIO.getImageReaders(input);
			if (readers == null || !readers.hasNext())
				throw new RuntimeException("No ImageReaders found");
			reader = (ImageReader) readers.next();
			reader.setInput(input);
			d = new Dimension(reader.getWidth(0), reader.getHeight(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (input != null)
				input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (reader != null)
				reader.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (input != null)
				input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (reader != null)
				reader.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (input != null)
				input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (reader != null)
				reader.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	public static boolean existsGraphicsMagick() {
		return "GraphicsMagick".equals(MediaConfig.getImageLibType());
	}

	public static void scaleRateImageFile(String fromFileName, String toFileName, int toWidth, int toHeight) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.scaleRateImageFile(fromFileName, toFileName, toWidth, toHeight);
		else
			SimpleImageUtil.scale(fromFileName, toFileName, toWidth, toHeight);
	}

	public static void scaleRateImageFile(String fromFileName, String toFileName, int toWidth, int toHeight, String mode) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.scaleRateImageFile(fromFileName, toFileName, toWidth, toHeight, mode);
		else
			SimpleImageUtil.scale(fromFileName, toFileName, toWidth, toHeight, mode);
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
		int xy[] = getPosition(filePath, pressText, font.getSize(), position);
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, opicty, xy[0], xy[1]);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, opicty, xy[0], xy[1]);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, Color outlineColor, int outlineSize, float opicty, int position) throws Exception {
		int xy[] = getPosition(filePath, pressText, font.getSize(), position);
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, false, opicty, xy[0], xy[1]);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, false, opicty, xy[0], xy[1]);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, Color shadowColor, float opicty, int position) throws Exception {
		int xy[] = getPosition(filePath, pressText, font.getSize(), position);
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

	public static void pressText(String filePath, String pressText, Font font, Color color, Color outlineColor, int outlineSize, boolean underline, float opicty, int x, int y)
			throws Exception {
		if (existsGraphicsMagick())
			SimpleImageUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, underline, opicty, x, y);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, outlineColor, outlineSize, underline, opicty, x, y);
	}

	public static void pressText(String filePath, String pressText, Font font, Color color, Color shadowColor, float opicty, int x, int y) throws Exception {
		if (existsGraphicsMagick())
			ImageMagickUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, x, y);
		else
			SimpleImageUtil.pressText(filePath, pressText, font, color, shadowColor, opicty, x, y);
	}

	public static final void pressImage(String src, String pressImg, float opicty, int x, int y) throws Exception {
		if (existsGraphicsMagick()) {
			int dissolve = Math.round(opicty * 100F);
			ImageMagickUtil.pressImage(src, pressImg, x, dissolve);
		} else {
			SimpleImageUtil.wartermark(new File(src), new File(pressImg), new File(src), x, y, opicty);
		}
	}

	public static final void pressImage(String src, String pressImg, float opicty, int position) throws Exception {
		if (existsGraphicsMagick()) {
			int dissolve = Math.round(opicty * 100F);
			ImageMagickUtil.pressImage(src, pressImg, position, dissolve);
		} else {
			SimpleImageUtil.wartermark(new File(src), new File(pressImg), new File(src), position, opicty);
		}
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

	public static final void txtToImage(String dest, String txt, Font font, Color fontColor, Color bgColor) throws Exception {
		if (existsGraphicsMagick())
			ImageJDKUtil.txtToImage(dest, txt, font, fontColor, bgColor);
		else
			ImageJDKUtil.txtToImage(dest, txt, font, fontColor, bgColor);
	}

	public static final int NorthWest = 1;
	public static final int North = 2;
	public static final int NorthEast = 3;
	public static final int West = 4;
	public static final int Center = 5;
	public static final int East = 6;
	public static final int SouthWest = 7;
	public static final int South = 8;
	public static final int SouthEast = 9;
	public static final String ZOOM_MODE_Fill = "fill";
	public static final String ZOOM_MODE_Fit = "fit";
	public static final String ZOOM_MODE_Stretch = "stretch";
}
