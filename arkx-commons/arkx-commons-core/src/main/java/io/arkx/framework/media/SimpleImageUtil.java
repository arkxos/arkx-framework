// Decompiled by DJ v3.12.12.101 Copyright 2016 Atanas Neshkov  Date: 2016/5/16 13:34:22
// Home Page:  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   SimpleImageUtil.java

package io.arkx.framework.media;

import java.awt.*;
import java.io.*;

import javax.imageio.ImageIO;

import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.Primitives;
import io.arkx.framework.media.render.ZDrawTextItem;

import com.alibaba.simpleimage.ImageFormat;
import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.ImageWrapper;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.render.*;

public class SimpleImageUtil {

	public SimpleImageUtil() {
	}

	public static void main(String args[]) {
		try {
			String filePath = "D:/71.jpg";
			String destPath = "D:/72.jpg";
			scale(filePath, destPath, 350, 40, "fill_9");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void pressText(String filePath, String txt, Font font, Color fontColor, float opicty, int x, int y)
			throws Exception {
		pressText(filePath, txt, new DrawTextItem[] { new ZDrawTextItem(txt, fontColor, font, opicty, x, y) });
	}

	public static void pressText(String filePath, String txt, Font font, Color fontColor, Color fontShadowColor,
			float opicty, int x, int y) throws Exception {
		pressText(filePath, txt,
				new DrawTextItem[] { new ZDrawTextItem(txt, fontColor, fontShadowColor, font, opicty, x, y) });
	}

	public static void pressText(String filePath, String txt, Font font, Color fontColor, Color outlineColor,
			int outlineSize, boolean underline, float opicty, int x, int y) throws Exception {
		pressText(filePath, txt, new DrawTextItem[] {
				new ZDrawTextItem(txt, fontColor, outlineColor, outlineSize, font, opicty, x, y, underline) });
	}

	public static void pressText(String filePath, String txt, DrawTextItem items[]) throws Exception {
		InputStream in;
		ImageRender fr;
		in = null;
		fr = null;
		filePath = FileUtil.normalizePath(filePath);
		in = new FileInputStream(filePath);
		ImageRender rr = new ReadRender(in);
		DrawTextParameter dtp = new DrawTextParameter();
		if (items != null) {
			DrawTextItem adrawtextitem[];
			int j = (adrawtextitem = items).length;
			for (int i = 0; i < j; i++) {
				DrawTextItem itm = adrawtextitem[i];
				dtp.addTextInfo(itm);
			}

		}
		DrawTextRender dtr = new DrawTextRender(rr, dtp);
		ImageFormat iFormat = ImageFormat.getImageFormat(FileUtil.getExtension(filePath));
		if (iFormat == ImageFormat.UNKNOWN)
			throw new Exception((new StringBuilder("Unknow image type: ")).append(filePath).toString());
		fr = new WriteRender(dtr, filePath, iFormat);
		fr.render();
		closeQuietly(in);
		if (fr != null)
			fr.dispose();
		return;
	}

	public static void scale(String filePath, String destPath, double rate) throws Exception {
		Dimension d = ImageUtil.getDimension(filePath);
		double w = d.getWidth();
		double h = d.getHeight();
		w *= rate;
		h *= rate;
		if (w <= 0.0D || h <= 0.0D) {
			FileUtil.copy(filePath, destPath);
			return;
		}
		else {
			scale(filePath, destPath, (int) w, (int) h);
			return;
		}
	}

	public static void scale(String filePath, String destPath, int width, int height, String mode) throws Exception {
		if ("fit".equals(mode)) {
			scale(filePath, destPath, width, height);
		}
		else {
			int subIndex = mode.indexOf('_');
			if (subIndex == 4) {
				String sMode = mode.substring(0, subIndex);
				if ("fill".equals(sMode)) {
					if (subIndex < mode.length() - 1) {
						int cutLoc = Primitives.getInteger(mode.substring(subIndex + 1));
						if (cutLoc <= 0 || cutLoc > 9)
							cutLoc = 5;
						scaleOnFill(filePath, destPath, width, height, cutLoc);
					}
					else {
						scaleOnFill(filePath, destPath, width, height, 5);
					}
				}
				else {
					scale(filePath, destPath, width, height);
				}
			}
			else if (subIndex == -1) {
				if ("fill".equals(mode))
					scaleOnFill(filePath, destPath, width, height, 5);
				else
					scale(filePath, destPath, width, height);
			}
			else {
				scale(filePath, destPath, width, height);
			}
		}
	}

	public static void scale(String filePath, String destPath, int maxWidth, int maxHeight) throws Exception {
		ScaleParameter scaleParam = new ScaleParameter(maxWidth, maxHeight,
				com.alibaba.simpleimage.render.ScaleParameter.Algorithm.LANCZOS);
		scale(filePath, destPath, scaleParam);
	}

	public static void scale(String filePath, String destPath, ScaleParameter scaleParam) throws Exception {
		ImageFormat imageFormat = ImageFormat.getImageFormat(FileUtil.getExtension(filePath));
		scale(new File(filePath), new File(destPath), scaleParam, imageFormat, true);
	}

	public static void scale(File in, File out, ScaleParameter scaleParam, ImageFormat format, boolean toRGB)
			throws Exception {
		FileInputStream inStream;
		FileOutputStream outStream;
		ImageRender wr;
		inStream = null;
		outStream = null;
		wr = null;
		inStream = new FileInputStream(in);
		ImageRender rr = new ReadRender(inStream, toRGB);
		ImageRender sr = new ScaleRender(rr, scaleParam);
		wr = new WriteRender(sr, out, format);
		wr.render();
		closeQuietly(inStream);
		closeQuietly(outStream);
		if (wr != null)
			wr.dispose();
		return;
	}

	private static void closeQuietly(OutputStream input) {
		try {
			if (input != null)
				input.close();
		}
		catch (IOException ioexception) {
		}
	}

	private static void closeQuietly(InputStream input) {
		try {
			if (input != null)
				input.close();
		}
		catch (IOException ioexception) {
		}
	}

	public static void scaleOnFill(String filePath, String destPath, int width, int height, int location)
			throws Exception {
		FileInputStream inStream;
		FileOutputStream outStream;
		ImageRender wr;
		inStream = null;
		outStream = null;
		wr = null;
		ImageFormat imageFormat = ImageFormat.getImageFormat(FileUtil.getExtension(filePath));
		inStream = new FileInputStream(filePath);
		ReadRender rr = new ReadRender(inStream, true);
		rr = new ReadRender(inStream, true);
		ScaleParameter scaleParam = new ScaleParameter(width, height);// ,
																		// com.alibaba.simpleimage.render.ScaleParameter.Algorithm.LANCZOS,
																		// com.alibaba.simpleimage.render.ScaleParameter.Mode.FILL);
		ImageRender sr = new ScaleRender(rr, scaleParam);
		CropParameter cropParameter = new CropParameter(0, 0, width, height);
		ImageRender cr = new CropRender(sr, cropParameter);
		wr = new WriteRender(cr, destPath, imageFormat);
		wr.render();
		closeQuietly(inStream);
		closeQuietly(outStream);
		if (wr != null)
			wr.dispose();
		return;
	}

	public static void wartermark(File sf, File wf, File tf, int x, int y, float alpha) {
		InputStream in;
		ImageRender sr;
		in = null;
		sr = null;
		try {
			in = new FileInputStream(sf);
			ImageRender sir = new ReadRender(in, true);
			WatermarkParameter param = createWatermarkParam(wf, alpha, x, y);
			sr = new WatermarkRender(sir, param);
			write(sr, tf);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (sr != null)
			try {
				sr.dispose();
			}
			catch (SimpleImageException e) {
				e.printStackTrace();
			}
		closeQuietly(in);
		if (sr != null)
			try {
				sr.dispose();
			}
			catch (SimpleImageException e) {
				e.printStackTrace();
			}
		closeQuietly(in);
	}

	public static void wartermark(File sf, File wf, File tf, int position, float alpha) {
		try {
			int width;
			int height;
			Image src = ImageIO.read(sf);
			width = src.getWidth(null);
			height = src.getHeight(null);
			if (width <= 300 && height <= 300)
				return;
			if (!wf.exists()) {
				LogUtil.warn((new StringBuilder("Water image not found:")).append(wf).toString());
				return;
			}
			int width_press;
			int height_press;
			Image src_press = ImageIO.read(wf);
			width_press = src_press.getWidth(null);
			height_press = src_press.getHeight(null);
			if (width < width_press || height < height_press) {
				LogUtil.error("Source image's width and height muust greater than water press image!");
				return;
			}
			try {
				int x = 0;
				int y = 0;
				int bianju = 20;
				int positions[][][] = {
						{ { bianju, bianju }, { (width - width_press) / 2, bianju },
								{ width - width_press - bianju, bianju } },
						{ { bianju, (height - height_press) / 2 },
								{ (width - width_press) / 2, (height - height_press) / 2 },
								{ width - width_press - bianju, (height - height_press) / 2 } },
						{ { bianju, height - height_press - bianju },
								{ (width - width_press) / 2, height - height_press - bianju },
								{ width - width_press - bianju, height - height_press - bianju } } };
				if (position == 0)
					position = NumberUtil.getRandomInt(9) + 1;
				x = positions[(position - 1) / 3][(position - 1) % 3][0];
				x = x > 0 ? x : 1;
				y = positions[(position - 1) / 3][(position - 1) % 3][1];
				y = y > 0 ? y : 1;
				wartermark(sf, wf, tf, x, y, alpha);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static WatermarkParameter createWatermarkParam(File wf, float alpha, int x, int y) throws Exception {
		InputStream in;
		ImageRender rr;
		ImageWrapper imageWrapper;
		in = null;
		rr = null;
		imageWrapper = null;
		in = new FileInputStream(wf);
		rr = new ReadRender(in, false);
		imageWrapper = rr.render();
		if (rr != null)
			rr.dispose();
		closeQuietly(in);
		WatermarkParameter param = new WatermarkParameter(imageWrapper, alpha, x, y);
		return param;
	}

	static void write(ImageRender sr, File output) throws Exception {
		ImageRender wr = null;
		wr = new WriteRender(sr, output, ImageFormat.JPEG);
		wr.render();
		if (wr != null)
			wr.dispose();
		return;
	}

}
