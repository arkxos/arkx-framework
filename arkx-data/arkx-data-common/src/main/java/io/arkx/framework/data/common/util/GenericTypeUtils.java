package io.arkx.framework.data.common.util;

/**
 * @author Nobody
 * @date 2025-07-26 22:46
 * @since 1.0
 */

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 泛型类型工具类（支持获取类的泛型父类/接口的实际类型参数）
 */
public class GenericTypeUtils {

	/**
	 * 获取类的泛型父类的第一个实际类型参数（简化版，适用于单参数场景）
	 * @param clazz 目标类的 Class 对象（需显式继承泛型父类，如 `class Sub extends Base<T>`）
	 * @param <T> 泛型父类的类型参数（如 `Base<T>` 中的 T）
	 * @return 第一个泛型参数的 Class 对象（若未指定泛型参数，抛出异常）
	 * @throws IllegalStateException 当泛型参数未显式指定或类型擦除时
	 */
	public static <T> Class<T> getGenericParentFirstType(Class<?> clazz) {
		Class<?>[] types = getGenericParentTypes(clazz, null);
		if (types == null || types.length == 0) {
			throw new IllegalStateException("类 " + clazz.getName() + " 未显式继承泛型父类，或泛型参数被擦除");
		}
		return (Class<T>) types[0];
	}

	/**
	 * 获取类的泛型父类的所有实际类型参数（支持多参数）
	 * @param clazz 目标类的 Class 对象
	 * @param parentClass 目标泛型父类的 Class 对象（如 `Base.class`，若为 null 则取直接父类）
	 * @param <P> 泛型父类的类型（如 `Base
	 * <P>
	 * ` 中的 P）
	 * @return 实际类型参数的 Class 数组（若未指定泛型参数，返回空数组）
	 */
	public static <P> Class<?>[] getGenericParentTypes(Class<?> clazz, Class<P> parentClass) {
		// 获取目标类的泛型父类（可能为 ParameterizedType 或原始类型）
		Type genericSuperclass = clazz.getGenericSuperclass();

		// 若未指定父类，尝试获取父类（兼容无泛型的情况）
		if (genericSuperclass instanceof Class<?>) {
			return new Class[0]; // 未显式继承泛型父类，返回空数组
		}

		// 确认是参数化类型（ParameterizedType）
		if (!(genericSuperclass instanceof ParameterizedType)) {
			return new Class[0];
		}

		ParameterizedType paramType = (ParameterizedType) genericSuperclass;

		// 过滤目标父类（若指定了 parentClass）
		Class<?> rawType = (Class<?>) paramType.getRawType();
		if (parentClass != null && !parentClass.equals(rawType)) {
			return new Class[0]; // 不匹配目标父类，返回空数组
		}

		// 提取所有实际类型参数
		Type[] actualTypeArgs = paramType.getActualTypeArguments();
		List<Class<?>> result = new ArrayList<>();
		for (Type type : actualTypeArgs) {
			result.add(extractClassType(type)); // 递归解析嵌套泛型
		}
		return result.toArray(new Class[0]);
	}

	/**
	 * 获取类实现的泛型接口的第一个实际类型参数（简化版，适用于单参数场景）
	 * @param clazz 目标类的 Class 对象（需显式实现泛型接口，如 `class Impl implements MyInterface<T>`）
	 * @param interfaceClass 目标泛型接口的 Class 对象（如 `MyInterface.class`）
	 * @param <T> 泛型接口的类型参数（如 `MyInterface<T>` 中的 T）
	 * @return 第一个泛型参数的 Class 对象（若未指定泛型参数，抛出异常）
	 * @throws IllegalStateException 当泛型参数未显式指定或类型擦除时
	 */
	public static <T> Class<T> getGenericInterfaceFirstType(Class<?> clazz, Class<?> interfaceClass) {
		Class<?>[] types = getGenericInterfaceTypes(clazz, interfaceClass);
		if (types == null || types.length == 0) {
			throw new IllegalStateException(
					"类 " + clazz.getName() + " 未显式实现泛型接口 " + interfaceClass.getName() + "，或泛型参数被擦除");
		}
		return (Class<T>) types[0];
	}

	/**
	 * 获取类实现的泛型接口的所有实际类型参数（支持多参数）
	 * @param clazz 目标类的 Class 对象
	 * @param interfaceClass 目标泛型接口的 Class 对象（如 `MyInterface.class`）
	 * @param <I> 泛型接口的类型（如 `MyInterface<I>` 中的 I）
	 * @return 实际类型参数的 Class 数组（若未指定泛型参数，返回空数组）
	 */
	public static <I> Class<?>[] getGenericInterfaceTypes(Class<?> clazz, Class<I> interfaceClass) {
		// 获取目标类实现的所有泛型接口
		Type[] genericInterfaces = clazz.getGenericInterfaces();

		List<Class<?>> result = new ArrayList<>();
		for (Type type : genericInterfaces) {
			// 仅处理参数化类型（ParameterizedType）
			if (!(type instanceof ParameterizedType)) {
				continue;
			}

			ParameterizedType paramType = (ParameterizedType) type;
			Class<?> rawType = (Class<?>) paramType.getRawType();

			// 匹配目标接口
			if (!interfaceClass.equals(rawType)) {
				continue;
			}

			// 提取所有实际类型参数
			Type[] actualTypeArgs = paramType.getActualTypeArguments();
			for (Type argType : actualTypeArgs) {
				result.add(extractClassType(argType)); // 递归解析嵌套泛型
			}
			break; // 找到第一个匹配的接口后退出（假设只实现一次）
		}
		return result.toArray(new Class[0]);
	}

	/**
	 * 递归解析类型，提取最终的 Class 类型（处理嵌套泛型，如 List<List<String>>）
	 * @param type 待解析的类型（可能是 TypeVariable、WildcardType、ParameterizedType 等）
	 * @return 最终的 Class 对象（若无法解析为具体类，抛出异常）
	 */
	private static Class<?> extractClassType(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type; // 直接是 Class 类型（如 String）
		}
		else if (type instanceof ParameterizedType) {
			// 处理嵌套泛型（如 List<List<String>> 中的内层 List<String>）
			ParameterizedType paramType = (ParameterizedType) type;
			return extractClassType(paramType.getRawType()); // 递归提取原始类型
		}
		else {
			// 不支持的类型（如 TypeVariable、WildcardType）
			throw new IllegalArgumentException("不支持的类型: " + type.getTypeName() + "，仅支持具体类或参数化类型");
		}
	}

	// ------------------------- 示例用法 -------------------------
	public static void mainx(String[] args) {
		// 测试 1：获取泛型父类的类型参数
		Class<SubClass> subClass = SubClass.class;
		Class<?>[] parentTypes = GenericTypeUtils.getGenericParentTypes(subClass, BaseClass.class);
		System.out.println("SubClass 继承的 BaseClass 泛型参数: " + Arrays.toString(parentTypes));
		// 输出：[class java.lang.String]

		// 测试 2：获取泛型接口的类型参数
		Class<IntegerImpl> integerImpl = IntegerImpl.class;
		Class<?>[] interfaceTypes = GenericTypeUtils.getGenericInterfaceTypes(integerImpl, MyInterface.class);
		System.out.println("IntegerImpl 实现的 MyInterface 泛型参数: " + Arrays.toString(interfaceTypes));
		// 输出：[class java.lang.Integer]

		// 测试 3：获取嵌套泛型参数（如 List<List<String>>）
		Class<NestedGenericClass> nestedClass = NestedGenericClass.class;
		Class<?>[] nestedTypes = GenericTypeUtils.getGenericParentTypes(nestedClass, NestedBase.class);
		System.out.println("NestedGenericClass 继承的 NestedBase 泛型参数: " + Arrays.toString(nestedTypes));
		// 输出：[interface java.util.List] （注：List 是接口，无法继续解析内层 String，需根据需求扩展）
	}

	// ------------------------- 测试用例类 -------------------------
	// 泛型基类
	static class BaseClass<T> {

	}

	// 子类显式继承泛型基类（单参数）
	static class SubClass extends BaseClass<String> {

	}

	// 泛型接口
	interface MyInterface<T> {

	}

	// 实现类显式实现泛型接口（单参数）
	static class IntegerImpl implements MyInterface<Integer> {

	}

	// 嵌套泛型基类
	static class NestedBase<T> {

	}

	// 嵌套泛型子类（内层是 List<String>）
	static class NestedGenericClass extends NestedBase<List<String>> {

	}

}
