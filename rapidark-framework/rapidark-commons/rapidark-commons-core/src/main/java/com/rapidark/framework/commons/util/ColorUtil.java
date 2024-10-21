package com.rapidark.framework.commons.util;

import java.awt.Color;
import java.util.Random;

public class ColorUtil {

	public static Color randomColor() {
		int R = (int) (Math.random() * 256);
		int G = (int) (Math.random() * 256);
		int B = (int) (Math.random() * 256);
		Color color = new Color(R, G, B); // random color, but can be bright or dull
		return color;
	}

	public static Color randomHSBColor() {
		// to get rainbow, pastel colors
		Random random = new Random();
		final float hue = random.nextFloat();
		final float saturation = 0.9f;// 1.0 for brilliant, 0.0 for dull
		final float luminance = 1.0f; // 1.0 for brighter, 0.0 for black
		Color color = Color.getHSBColor(hue, saturation, luminance);
		return color;
	}

	/**
	 * to get rainbow, pastel colors
	 * 
	 * @param saturation
	 *            1.0 for brilliant, 0.0 for dull
	 * @param luminance
	 *            1.0 for brighter, 0.0 for black
	 * @return
	 */
	public static Color randomHSBColor(float saturation, float luminance) {
		Random random = new Random();
		final float hue = random.nextFloat();
		Color color = Color.getHSBColor(hue, saturation, luminance);
		return color;
	}

	public static void main(String[] args) {
		System.out.println(toHexFromColor(Color.BLUE));
		System.out.println(toColorFromString(toHexFromColor(Color.BLUE)));
	}

	/**
	 * Color对象转换成字符串
	 * 
	 * @param color
	 *            Color对象
	 * @return 16进制颜色字符串
	 */
	private static String toHexFromColor(Color color) {
		String r, g, b;
		StringBuilder su = new StringBuilder();
		r = Integer.toHexString(color.getRed());
		g = Integer.toHexString(color.getGreen());
		b = Integer.toHexString(color.getBlue());
		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;
		r = r.toUpperCase();
		g = g.toUpperCase();
		b = b.toUpperCase();
		su.append("#");//0xFF
		su.append(r);
		su.append(g);
		su.append(b);
		// 0xFF0000FF
		return su.toString();
	}

	/**
	 * 字符串转换成Color对象
	 * 
	 * @param colorStr
	 *            16进制颜色字符串
	 * @return Color对象
	 */
	public static Color toColorFromString(String colorStr) {
		colorStr = colorStr.substring(1);
		Color color = new Color(Integer.parseInt(colorStr, 16));
		// java.awt.Color[r=0,g=0,b=255]
		return color;
	}
}
