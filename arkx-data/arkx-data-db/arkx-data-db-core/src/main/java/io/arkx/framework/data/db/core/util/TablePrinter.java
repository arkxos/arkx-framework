package io.arkx.framework.data.db.core.util;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;

/**
 * @author Nobody
 * @date 2025-07-09 14:58
 * @since 1.0
 */
public class TablePrinter {

	public static void printTable(String tableName, List<String> fields, Map<String, String> types,
			List<Object[]> rows) {
		List<String[]> data = new ArrayList<>();
		data.add(fields.toArray(new String[0]));
		data.add(fields.stream().map(types::get).toArray(String[]::new));
		data.addAll(rows.stream().map(row -> {
			List<String> rowData = new ArrayList<>();
			for (Object value : row) {
				rowData.add(value == null ? "" : value.toString());
			}
			return rowData.toArray(new String[0]);
		}).collect(Collectors.toCollection(ArrayList::new)));

		printTable(tableName, data);
	}

	public static void printTable(String tableName, List<String[]> data) {
		if (data == null || data.isEmpty()) {
			System.out.println("无数据");
			return;
		}

		// List<String> lines = data.stream().map(line -> String.join(",",
		// line)).collect(Collectors.toList());
		List<String> lines = data.stream().map(row -> Arrays.stream(row).map(cell -> {
			if (cell == null)
				return "";
			// 判断是否需要加引号
			// if (cell.contains(",") || cell.contains("\"") || cell.contains("\n") ||
			// cell.contains("'")) {
			return "\"" + cell.replace("\"", "\"\"") + "\""; // 双引号包裹并转义内部双引号
			// }
			// return cell;
		}).collect(Collectors.joining(","))).collect(Collectors.toList());
		File tempDir = new File(System.getProperty("user.dir") + File.separator + "temp");
		if (!tempDir.exists()) {
			tempDir.mkdir();
		}
		String filename = tableName + "_"
				+ LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")) + "_"
				+ (int) (ThreadLocalRandom.current().nextDouble() * 1000) + ".csv";
		FileUtil.writeUtf8Lines(lines,
				System.getProperty("user.dir") + File.separator + "temp" + File.separator + filename);

		// 提取表头（假设第一行为表头）
		String[] header = data.getFirst();
		List<String[]> rows = data.subList(1, data.size());

		// 计算每列最大显示宽度（中文字符占 2 位，英文字符占 1 位）
		int[] colWidths = new int[header.length];
		for (int i = 0; i < header.length; i++) {
			colWidths[i] = getMaxDisplayWidth(header[i], rows, i);
		}

		// 生成分隔线（兼容 JDK 1.8）
		String separator = "+" + String.join("+", Arrays.stream(colWidths)
			.boxed() // 转换为 Stream<Integer>
			.map(width -> repeatString('-', width * 2))
			.collect(Collectors.toList())) + "+";

		// 打印表头
		System.out.println(separator);
		System.out.println("|" + formatRow(header, colWidths) + "|");

		// 打印数据行
		int i = 0;
		for (String[] row : rows) {
			if (i++ > 10) {// 只打印几行，避免打印过多数据
				break;
			}
			System.out.println(separator);
			System.out.println("|" + formatRow(row, colWidths) + "|");
		}
		System.out.println(separator);
	}

	// 计算某一列的最大显示宽度（考虑中文字符）
	private static int getMaxDisplayWidth(String header, List<String[]> rows, int colIndex) {
		int max = getDisplayWidth(header);
		for (String[] row : rows) {
			max = Math.max(max, getDisplayWidth(row[colIndex]));
		}
		return max;
	}

	// 计算单个字符串的显示宽度（中文字符 2，英文字符 1）
	private static int getDisplayWidth(String str) {
		if (str == null)
			return 0;
		int width = 0;
		for (char c : str.toCharArray()) {
			width += (c >= 0x4E00 && c <= 0x9FFF) ? 2 : 1; // 中文字符 Unicode 范围
		}
		return width;
	}

	// 格式化一行数据（左对齐，填充空格）
	private static String formatRow(String[] row, int[] colWidths) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < row.length; i++) {
			String cell = row[i] != null ? row[i] : "";
			int width = colWidths[i];
			// 左对齐，不足部分用空格填充（总宽度为 2 倍字符宽度）
			sb.append(String.format("%-" + (width * 2) + "s|", cell));
		}
		return sb.toString();
	}

	// JDK 1.8 兼容的字符串重复方法（替代 String.repeat）
	private static String repeatString(char c, int times) {
		if (times <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(times);
		for (int i = 0; i < times; i++) {
			sb.append(c);
		}
		return sb.toString();
	}

	public static void main1(String[] args) {
		// 示例数据（表头 + 3 行数据）
		List<String[]> data = new ArrayList<>();
		data.add(new String[] { "姓名", "年龄", "城市" }); // 表头
		data.add(new String[] { "张三", "25", "北京" });
		data.add(new String[] { "Alice", "30", "New York" });
		data.add(new String[] { "王五", "28", "上海" });

		printTable("t", data);
	}

}
