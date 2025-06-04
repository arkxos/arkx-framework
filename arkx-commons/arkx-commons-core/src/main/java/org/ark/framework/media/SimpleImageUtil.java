package org.ark.framework.media;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.ark.framework.media.render.ZDrawTextItem;

import com.alibaba.simpleimage.ImageFormat;
import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.render.DrawTextItem;
import com.alibaba.simpleimage.render.DrawTextParameter;
import com.alibaba.simpleimage.render.DrawTextRender;
import com.alibaba.simpleimage.render.ReadRender;
import com.alibaba.simpleimage.render.ScaleParameter;
import com.alibaba.simpleimage.render.ScaleRender;
import com.alibaba.simpleimage.render.WriteRender;
import io.arkx.framework.commons.util.FileUtil;

public class SimpleImageUtil {
	public static void pressText(String filePath, String txt, Font font, Color fontColor, float opicty, int x, int y) throws Exception {
		pressText(filePath, txt, new DrawTextItem[] { new ZDrawTextItem(txt, fontColor, font, opicty, x, y) });
	}

	public static void pressText(String filePath, String txt, Font font, Color fontColor, Color fontShadowColor, float opicty, int x, int y) throws Exception {
		pressText(filePath, txt, new DrawTextItem[] { new ZDrawTextItem(txt, fontColor, fontShadowColor, font, opicty, x, y) });
	}

	public static void pressText(String filePath, String txt, Font font, Color fontColor, Color outlineColor, int outlineSize, float opicty, int x, int y) throws Exception {
		pressText(filePath, txt, new DrawTextItem[] { new ZDrawTextItem(txt, fontColor, outlineColor, outlineSize, font, opicty, x, y) });
	}

	public static void pressText(String filePath, String txt, DrawTextItem[] items) throws Exception {
		InputStream in = null;
		ImageRender fr = null;
		try {
			filePath = FileUtil.normalizePath(filePath);
			in = new FileInputStream(filePath);
			ImageRender rr = new ReadRender(in);

			DrawTextParameter dtp = new DrawTextParameter();
			if (items != null) {
				for (DrawTextItem itm : items) {
					dtp.addTextInfo(itm);
				}
			}
			DrawTextRender dtr = new DrawTextRender(rr, dtp);
			ImageFormat iFormat = ImageFormat.getImageFormat(FileUtil.getExtension(filePath));
			if (iFormat == ImageFormat.UNKNOWN) {
				throw new Exception("Unknow image type: " + filePath);
			}
			fr = new WriteRender(dtr, filePath, iFormat);
			fr.render();
		} finally {
			IOUtils.closeQuietly(in);
			if (fr != null)
				fr.dispose();
		}
	}

	public static void scale(String filePath, String destPath, double rate) throws Exception {
		Dimension d = ImageUtil.getDimension(filePath);
		double w = d.getWidth();
		double h = d.getHeight();
		w *= rate;
		h *= rate;
		if ((w <= 0.0D) || (h <= 0.0D)) {
			FileUtil.copy(filePath, destPath);
			return;
		}
		scale(filePath, destPath, (int) w, (int) h);
	}

	public static void scale(String filePath, String destPath, int maxWidth, int maxHeight) throws Exception {
		ScaleParameter scaleParam = new ScaleParameter(maxWidth, maxHeight, ScaleParameter.Algorithm.LANCZOS);
		scale(filePath, destPath, scaleParam);
	}

	public static void scale(String filePath, String destPath, ScaleParameter scaleParam) throws Exception {
		ImageFormat imageFormat = ImageFormat.getImageFormat(FileUtil.getExtension(filePath));
		scale(new File(filePath), new File(destPath), scaleParam, imageFormat, true);
	}

	public static void scale(File in, File out, ScaleParameter scaleParam, ImageFormat format, boolean toRGB) throws Exception {
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		ImageRender wr = null;
		try {
			inStream = new FileInputStream(in);
			ImageRender rr = new ReadRender(inStream, toRGB);
			ImageRender sr = new ScaleRender(rr, scaleParam);
			wr = new WriteRender(sr, out, format);
			wr.render();
		} finally {
			IOUtils.closeQuietly(inStream);
			IOUtils.closeQuietly(outStream);
			if (wr != null)
				wr.dispose();
		}
	}
}