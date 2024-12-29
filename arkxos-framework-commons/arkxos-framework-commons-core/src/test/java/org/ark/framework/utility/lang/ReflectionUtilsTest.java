package org.ark.framework.utility.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import com.arkxos.framework.commons.util.lang.ReflectionUtil;

/**
 * 
 * @author Darkness
 * @date 2012-6-26 下午3:47:25
 * @version V1.0
 */
public class ReflectionUtilsTest {

	/**
	 * 测试获取父类的各个方法对象
	 */
	 @Test
	public void testGetDeclaredMethod() {

		Object obj = new Son();

		// 获取公共方法名
		Method publicMethod = ReflectionUtil.getDeclaredMethod(obj, "publicMethod");
		System.out.println(publicMethod.getName());

		// 获取默认方法名
		Method defaultMethod = ReflectionUtil.getDeclaredMethod(obj, "defaultMethod");
		System.out.println(defaultMethod.getName());

		// 获取被保护方法名
		Method protectedMethod = ReflectionUtil.getDeclaredMethod(obj, "protectedMethod");
		System.out.println(protectedMethod.getName());

		// 获取私有方法名
		Method privateMethod = ReflectionUtil.getDeclaredMethod(obj, "privateMethod");
		System.out.println(privateMethod.getName());
	}

	/**
	 * 测试调用父类的方法
	 * 
	 * @throws Exception
	 */
	 @Test
	public void testInvokeMethod() throws Exception {
		Object obj = new Son();

		// 调用父类的公共方法
		ReflectionUtil.invokeMethod(obj, "publicMethod");

		// 调用父类的默认方法
		ReflectionUtil.invokeMethod(obj, "defaultMethod");

		// 调用父类的被保护方法
		ReflectionUtil.invokeMethod(obj, "protectedMethod");

		// 调用父类的私有方法
		ReflectionUtil.invokeMethod(obj, "privateMethod");
	}

	/**
	 * 测试获取父类的各个属性名
	 */
	 @Test
	public void testGetDeclaredField() {

		Object obj = new Son();

		// 获取公共属性名
		Field publicField = ReflectionUtil.getDeclaredField(obj, "publicField");
		System.out.println(publicField.getName());

		// 获取公共属性名
		Field defaultField = ReflectionUtil.getDeclaredField(obj, "defaultField");
		System.out.println(defaultField.getName());

		// 获取公共属性名
		Field protectedField = ReflectionUtil.getDeclaredField(obj, "protectedField");
		System.out.println(protectedField.getName());

		// 获取公共属性名
		Field privateField = ReflectionUtil.getDeclaredField(obj, "privateField");
		System.out.println(privateField.getName());

	}

	@Test
	public void testSetFieldValue() {

		Object obj = new Son();

		System.out.println("原来的各个属性的值: ");
		System.out.println("publicField = " + ReflectionUtil.getFieldValue(obj, "publicField"));
		System.out.println("defaultField = " + ReflectionUtil.getFieldValue(obj, "defaultField"));
		System.out.println("protectedField = " + ReflectionUtil.getFieldValue(obj, "protectedField"));
		System.out.println("privateField = " + ReflectionUtil.getFieldValue(obj, "privateField"));

		ReflectionUtil.setFieldValue(obj, "publicField", "a");
		ReflectionUtil.setFieldValue(obj, "defaultField", "b");
		ReflectionUtil.setFieldValue(obj, "protectedField", "c");
		ReflectionUtil.setFieldValue(obj, "privateField", "d");

		System.out.println("***********************************************************");

		System.out.println("将属性值改变后的各个属性值: ");
		System.out.println("publicField = " + ReflectionUtil.getFieldValue(obj, "publicField"));
		System.out.println("defaultField = " + ReflectionUtil.getFieldValue(obj, "defaultField"));
		System.out.println("protectedField = " + ReflectionUtil.getFieldValue(obj, "protectedField"));
		System.out.println("privateField = " + ReflectionUtil.getFieldValue(obj, "privateField"));

	}

	 @Test
	public void testGetFieldValue() {

		Object obj = new Son();

		System.out.println("publicField = " + ReflectionUtil.getFieldValue(obj, "publicField"));
		System.out.println("defaultField = " + ReflectionUtil.getFieldValue(obj, "defaultField"));
		System.out.println("protectedField = " + ReflectionUtil.getFieldValue(obj, "protectedField"));
		System.out.println("privateField = " + ReflectionUtil.getFieldValue(obj, "privateField"));
	}

}

/**
 * 父类
 */
class Parent {

	public String publicField = "1";

	String defaultField = "2";

	protected String protectedField = "3";

	private String privateField = "4";

	public void publicMethod() {
		System.out.println("publicMethod...");
	}

	void defaultMethod() {
		System.out.println("defaultMethod...");
	}

	protected void protectedMethod() {
		System.out.println("protectedMethod...");
	}

	private void privateMethod() {
		System.out.println("..privateMethod.");
	}

}

/**
 * 子类
 */
class Son extends Parent {
}
