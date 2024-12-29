package com.arkxos.framework.commons.util.lang;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.annotation.Column;
import com.arkxos.framework.commons.collection.CaseIgnoreMapx;
import com.arkxos.framework.commons.collection.Mapx;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @class org.ark.framework.utility.lang.ClassUtil
 * @author Darkness
 * @date 2012-4-8 上午11:19:30
 * @version V1.0
 * @since JDP 2.1.0
 */
@Slf4j
public class ClassUtil {
	
	/**
	 * 获取方法的参数名称，按给定的参数类型匹配方法
	 * 
	 * @param clazz
	 * @param method
	 * @param paramTypes
	 * @return
	 * @throws NotFoundException
	 *             如果类或者方法不存在
	 * @throws MissingLVException
	 *             如果最终编译的class文件不包含局部变量表信息
	 * @since JDP 2.1.0
	 */
	public static String[] getMethodParamNames(Class<?> clazz, String method, Class<?>... paramTypes) throws NotFoundException, MissingLVException {

		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get(clazz.getName());
		String[] paramTypeNames = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
			paramTypeNames[i] = paramTypes[i].getName();
		CtMethod cm = cc.getDeclaredMethod(method, pool.get(paramTypeNames));
		return getMethodParamNames(cm);
	}

	/**
	 * 
	 * 
	 * @author Darkness
	 * @date 2012-11-5 下午01:34:56 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static String[] getMethodParamNames(Class<?> clazz, String method) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equals(method)) {
				Paranamer paranamer = new BytecodeReadingParanamer();
				String[] parameterNames = paranamer.lookupParameterNames(m, false);
				// will return null if not found
//				for (String s : parameterNames) {
//					System.out.println(s);
//				}
				return parameterNames;
			}
		}
		return new String[]{};
	}

	/**
	 * 获取方法的参数名称，匹配同名的第一个方法
	 * 
	 * @param clazz
	 * @param method
	 * @return
	 * @throws NotFoundException
	 *             如果类或者方法不存在
	 * @throws MissingLVException
	 *             如果最终编译的class文件不包含局部变量表信息
	 * @since JDP 2.1.0
	 */
	@Deprecated
	public static String[] OldgetMethodParamNames(Class<?> clazz, String method) throws NotFoundException, MissingLVException {

		ClassPool pool = ClassPool.getDefault();

		URL licenseResource = clazz.getClassLoader().getResource("rapidark.license");
		if (licenseResource != null) {
			String classesPath = licenseResource.getPath().replace("/rapidark.license", "");
			pool.appendClassPath(classesPath);

			String libPath = classesPath.replace("classes", "lib");
			String[] libFiles = new File(libPath).list();
			if (libFiles != null) {
				for (String libFile : libFiles) {
					pool.appendClassPath(libPath + "/" + libFile);
				}
			}
		}

		CtClass cc = pool.get(clazz.getName());
		CtMethod cm = cc.getDeclaredMethod(method);
		String[] paramNames = getMethodParamNames(cm);

		return paramNames;
	}

	/**
	 * 获取方法参数名称
	 * 
	 * @param cm
	 * @return
	 * @throws NotFoundException
	 * @throws MissingLVException
	 *             如果最终编译的class文件不包含局部变量表信息
	 * @since JDP 2.1.0
	 */
	@Deprecated
	protected static String[] getMethodParamNames(CtMethod cm) throws NotFoundException, MissingLVException {

		CtClass cc = cm.getDeclaringClass();
		MethodInfo methodInfo = cm.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
		if (attr == null)
			throw new MissingLVException(cc.getName());

		String[] paramNames = new String[cm.getParameterTypes().length];
		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;

		for (int i = 0; i < paramNames.length; i++) {
			paramNames[i] = attr.variableName(i + pos);
			if ("i".equals(paramNames[i])) {
				pos++;
				paramNames[i] = attr.variableName(i + pos);
			}
			System.out.println("==================>" + paramNames[i]);

			if ("this".equals(paramNames[i])) {
				pos++;
				paramNames[i] = attr.variableName(i + pos);
			}
		}

		for (int i = 0; i < paramNames.length; i++) {
			System.out.println(paramNames[i]);
		}

		return paramNames;
	}

	/**
	 * 在class中未找到局部变量表信息<br>
	 * 使用编译器选项 javac -g:{vars}来编译源文件
	 * 
	 * @author Darkness
	 * @since JDP 2.1.0
	 */
	public static class MissingLVException extends Exception {

		private static final long serialVersionUID = 1L;

		static String msg = "class:%s 不包含局部变量表信息，请使用编译器选项 javac -g:{vars}来编译源文件。";

		public MissingLVException(String clazzName) {
			super(String.format(msg, clazzName));
		}
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
	 * GenricManager<Book>
	 * 
	 * @param clazz
	 *            The class to introspect
	 * @return the first generic declaration, or <code>Object.class</code> if
	 *         cannot be determined
	 * @since JDP 2.1.0
	 */
	public static Class<?> getSuperClassGenricType(Class<?> clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的范型参数的类型. 如public BookManager extends
	 * GenricManager<Book>
	 * 
	 * @param clazz
	 *            clazz The class to introspect
	 * @param index
	 *            the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or <code>Object.class</code> if
	 *         cannot be determined
	 * @since JDP 2.1.0
	 */
	public static Class<?> getSuperClassGenricType(Class<?> clazz, int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: " + params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}
		return (Class<?>) params[index];
	}

	/**
	 * 获取类中的所有PropertyDescriptor
	 * 
	 * @author Darkness
	 * @date 2011-12-9 下午01:40:42
	 * @version V1.0
	 * @param clazz
	 * @return
	 * @since JDP 2.1.0
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor pd[] = beanInfo.getPropertyDescriptors();
			return pd;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @author Darkness
	 * @date 2012-11-5 下午01:35:55 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static void getDeclaredMethods(Class<?> clazz) throws Exception {
		// -------------------获取方法的详细信息
		Method m[] = clazz.getDeclaredMethods();
		for (int i = 0; i < m.length; i++) {
			// --------------------获得方法的名字
			System.out.println("方法的名字是:" + m[i].getName());
			// --------------------获得方法参数的类型和有几个参数
			Class<?> b[] = m[i].getParameterTypes();// 获得所有的参数并且存放到数组B中
			for (int j = 0; j < b.length; j++) {
				System.out.println("参数的类型是" + b[j]);
			}
			// --------------------获得方法返回值的类型
			System.out.println(m[i].getReturnType());// 获得方法的返回值类型
			// --------------------获得方法的修饰符
			int mod = m[i].getModifiers();
			System.out.println("方法的修饰符有" + Modifier.toString(mod));
			// --------------------获得方法的异常类型
			Class<?> e[] = m[i].getExceptionTypes();// 获得所有的异常类型存放到数组e中
			for (int k = 0; k < e.length; k++) {
				System.out.println("方法的异常类型是：" + e[k]);
			}
			System.out.println("-------------------------------------------------------------------");
		}
		// ----------------------------获得属性的详细信息

	}

	/**
	 * 
	 * 
	 * @author Darkness
	 * @date 2012-11-5 下午01:36:07 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static Class<?> getGenerateParameter(Class<?> clazz) {
		return (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	
	@Deprecated
	public static Mapx<String, Object> getPropertyValues(Object object) {
		return objectToMapx(object);
	}
	
	/**
	 * 将对象的field、value设置到map返回
	 * 
	 * @author Darkness
	 * @date 2012-11-4 下午09:13:50 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static Mapx<String, Object> objectToMapx(Object object) {

		Mapx<String, Object> result = new Mapx<>();

		PropertyDescriptor[] props = getPropertyDescriptors(object.getClass());
		for (PropertyDescriptor propertyDescriptor : props) {
			
			if("class".equals(propertyDescriptor.getName())) {
				continue;
			}
			
			Object value = null;
			try {
				value = propertyDescriptor.getReadMethod().invoke(object);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			result.put(propertyDescriptor.getName(), value);
		}

		return result;
	}
	
	/**
	 * 将对象的field、value设置到map返回，忽略大小写
	 * 
	 * @author Darkness
	 * @date 2012-11-4 下午09:13:50 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static Mapx<String, Object> objectToCaseIgnoreMapx(Object object) {
		return new CaseIgnoreMapx<>(objectToMapx(object));
	}
	
	public static <T> List<T> mapToObjectList(Class<T> clazz, Map<String, Object> params, String alias) {

		List<T> postList = new ArrayList<>();
		Map<String, T> map = new HashMap<>();

		try {
			for (String paramName : params.keySet()) {
				if (paramName.indexOf(alias + "(") != -1) {
					String indexStr = paramName.substring(paramName.indexOf("(") + 1, paramName.indexOf(")"));
					T obj = null;
					if (map.containsKey(indexStr)) {
						obj = map.get(indexStr);
					} else {
						obj = clazz.newInstance();
						map.put(indexStr, obj);
					}
					String fieldName = paramName.substring(paramName.indexOf(")") + 2);
					Field field = null;
					try {
						field = ReflectionUtil.getDeclaredField(obj, fieldName);
					} catch (Exception e) {
						continue;
					}
					if(field == null) {
						continue;
					}
					
					if(Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					
					Object value = params.get(paramName);
					if(value == null) {
						continue;
					}
					ReflectionUtil.setFieldValue(obj, field.getName(), value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("参数 " + paramName + " 错误: " + e.getMessage(), e);
			throw new IllegalArgumentException("参数  错误: " + e.getMessage());
		}
		Iterator iter = map.keySet().iterator();
		int maxKey = 0;
		while (iter.hasNext()) {
			int key = Integer.parseInt(iter.next().toString());
			if (key > maxKey) {
				maxKey = key;
			}
		}
		// 0 1 6 3 5
		for (int i = 0; i < maxKey + 1; i++) {
			T o = map.get(i + "");
			if (o == null)
				continue;
			postList.add(postList.size(), o);
		}

		return postList;
	}
	
	
	public static <T> T caseIngoreMapToObject(Class<T> clazz, Map<String, Object> map) {
		return mapToObject(clazz, new CaseIgnoreMapx<>(map));
	}
	
	/**
	 * map转换成object
	 * 
	 * @author Darkness
	 * @date 2012-11-4 下午09:36:39 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static <T> T mapToObject(Class<T> clazz, Map<String, Object> map) {

		T result = null;
		try {
			result = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		Field[] fields = ReflectionUtil.getDeclaredFields(clazz);
		for (Field field : fields) {
			
			if(Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
			String fieldName = field.getName();
			
			Object value = map.get(fieldName);
			if(value == null) {
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					fieldName = column.name();
					value = map.get(fieldName);
				}
				continue;
			}
			ReflectionUtil.setFieldValue(result, field.getName(), value);
		}
		
		return result;
	}

	/**
	 * 将new对象中的不为null的属性附加到old对象中
	 * 
	 * @author Darkness
	 * @date 2012-11-4 下午10:11:51 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static void applyToObject(Object entityOld, Object entityNew) {
		Field[] fields = ReflectionUtil.getDeclaredFields(entityOld.getClass());
		
		for (Field field : fields) {
			
			/**
			 * final 类型的字段不做修改
			 */
			if(Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			
			Object newValue = ReflectionUtil.getFieldValue(entityNew, field.getName());
			if(newValue != null) {
				ReflectionUtil.setFieldValue(entityOld, field.getName(), newValue);
			}
		}
		
	}
	
	/**
	 * 将new对象中的不为null的属性附加到old对象中
	 * 
	 * @author Darkness
	 * @date 2012-11-4 下午10:11:51 
	 * @version V1.0
	 * @since JDP 2.1.0
	 */
	public static <T> T objectTo(Object entity, Class<T> clazz) {
		T t = null;
		try {
			t = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		
		Field[] fields = ReflectionUtil.getDeclaredFields(entity.getClass());
		
		for (Field field : fields) {
			
			/**
			 * final 类型的字段不做修改
			 */
			if(Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			
			Object value = ReflectionUtil.getFieldValue(entity, field.getName());
			if(value != null) {
				ReflectionUtil.setFieldValue(t, field.getName(), value);
			}
		}
		
		return t;
	}
}
