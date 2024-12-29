package com.arkxos.framework.core.method;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import com.arkxos.framework.Current;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.Primitives;
import com.arkxos.framework.commons.util.lang.ClassUtil;
import com.arkxos.framework.core.bean.BeanDescription;
import com.arkxos.framework.core.bean.BeanManager;
import com.arkxos.framework.core.bean.BeanProperty;
import com.arkxos.framework.core.bean.BeanUtil;
import com.arkxos.framework.core.castor.CastorService;
import com.arkxos.framework.thirdparty.el.Constants;

/**
 * UI方法参数绑定器
 */
public class UIMethodBinder {
	/**
	 * 将参数绑定到UIMethod实例（将当前请求中的变量设置到UIMethod的属性上，并将args中的值按类型匹配到方法参数上）
	 * 
	 * @param m UIMethod实例
	 * @param args 参数列表
	 */
//	public static void bind(UIMethod m, Object[] args) {
//	BeanUtil.fill(m, Current.getRequest());
//	BeanDescription bd = BeanManager.getBeanDescription(m.getClass());
//	for (BeanProperty bp : bd.getPropertyMap().values()) {
//		if (bp.canWrite()) {
//			for (Object arg : args) {
//				if (!Primitives.isPrimitives(arg) && !(arg instanceof String) && bp.getPropertyType().isInstance(arg)) {
//					bp.write(m, arg);
//				}
//			}
//		}
//	}
//}
	public static void bind(UIMethod m, Object[] args) {
		BeanUtil.fill(m, Current.getRequest());
		BeanDescription bd = BeanManager.getBeanDescription(m.getClass());
		for (BeanProperty bp : bd.getPropertyMap().values()) {
			if (bp.canWrite()) {
				boolean flag = false;
				Object[] arrayOfObject;
				int j = (arrayOfObject = args).length;
				for (int i = 0; i < j; i++) {
					Object arg = arrayOfObject[i];
					if ((!Primitives.isPrimitives(arg)) && (!(arg instanceof String)) && (bp.getPropertyType().isInstance(arg))) {
						bp.write(m, arg);
						flag = true;
						break;
					}
				}
				try {
					if ((!flag) && (Current.getRequest() != null)) {
						for (Map.Entry<String, Object> e : Current.getRequest().entrySet()) {
							if (((String) e.getKey()).equalsIgnoreCase(bp.getName())) {
								bp.write(m, CastorService.toType(e.getValue(), bp.getPropertyType()));
								flag = true;
								break;
							}
						}
					}
					if ((!flag) && (Current.getResponse() != null)) {
						for (Map.Entry<String, Object> e : Current.getResponse().entrySet()) {
							if (((String) e.getKey()).equalsIgnoreCase(bp.getName())) {
								bp.write(m, CastorService.toType(e.getValue(), bp.getPropertyType()));
								break;
							}
						}
					}
				} catch (Exception localException) {
				}
			}
		}
	}

	/**
	 * @param m 方法
	 * @param args 参数列表
	 * @return 将args转换成匹配方法参数类型的数组
	 */
//	public static Object[] convertArg(Method m, Object[] args, String[] params) {
//		Class<?>[] cs = m.getParameterTypes();
//		if (cs == null || cs.length == 0) {
//			return Constants.NoArgs;
//		}
//		Object[] arr = new Object[cs.length];
//		int i = 0;
//		for (Class<?> c : cs) {
//			for (Object obj : args) {
//				if (c.isInstance(obj)) {
//					arr[i] = obj;
//					break;
//				}
//			}
//			i++;
//		}
//		return arr;
//	}
	public static Object[] convertArg(Method m, Object[] args, String[] params) {
		Class<?>[] cs = m.getParameterTypes();
		if ((cs == null) || (cs.length == 0)) {
			return Constants.NoArgs;
		}
		Object[] arr = new Object[cs.length];
		for (int i = 0; i < cs.length; i++) {
			boolean flag = false;
			for (int j = 0; j < args.length; j++) {
				if ((args[j] != null) && (cs[i].isInstance(args[j]))) {
					arr[i] = args[j];
					args[j] = null;
					flag = true;
					break;
				}
			}
			if (params != null) {
				String name = params[i];
				try {
					if ((!flag) && (Current.getRequest() != null)) {
						for (Entry<String, Object> e : Current.getRequest().entrySet()) {
							if (e.getKey().equalsIgnoreCase(name)) {
								arr[i] = CastorService.toType(e.getValue(), cs[i]);
								flag = true;
								break;
							}
						}
					}
					if ((!flag) && (Current.getResponse() != null)) {
						for (Entry<String, Object> e : Current.getResponse().entrySet()) {
							if (e.getKey().equalsIgnoreCase(name)) {
								arr[i] = CastorService.toType(e.getValue(), cs[i]);
								break;
							}
						}
					}
				} catch (Exception localException) {
				}
			}
			if(arr[i] == null) {
				if (cs[i] == Mapx.class) {
					arr[i] = Current.getRequest();
				} else {
					arr[i] = ClassUtil.mapToObject(cs[i], Current.getRequest());					
				}
			}
		}
		return arr;
	}
}
