package com.arkxos.framework.commons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomUtil {

	@SafeVarargs
	public static <T> T random(T... datas) {
		if (datas == null || datas.length == 0) {
			return null;
		}
		Random rand = new Random();
		int randNum = rand.nextInt(datas.length);
		return datas[randNum];
	}

	public static <T> T random(List<T> datas) {
		if (datas == null || datas.isEmpty()) {
			return null;
		}
		Random rand = new Random();
		int randNum = rand.nextInt(datas.size());
		return datas.get(randNum);
	}
	
	public static <T> List<T> random(List<T> datas, int size) {
		List<T> result = new ArrayList<>();
		if (datas == null || datas.isEmpty()) {
			return result;
		}
		while (result.size() < size) {
			T randomValue = random(datas);
			if(!result.contains(randomValue)) {
				result.add(randomValue);
			}
		}
		return result;
	}

	public static String randomNumberString(int length) {
		long base = 1;
		for (int i = 1; i < length; i++) {
			base *= 10;
		}
		long value = (long) ((Math.random() * 9 + 1) * base);
		return value + "";
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			System.out.println(randomNumberString(5));
		}
		
		for (int i = 0; i < 10; i++) {
			System.out.println(random(Arrays.asList("1",  "2", "3", "4", "5", "6"), 3));;
		}
	}
}
