package io.arkx.framework.core.method;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import io.arkx.framework.WebCurrent;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.Primitives;
import io.arkx.framework.commons.util.lang.ClassUtil;
import io.arkx.framework.core.bean.BeanDescription;
import io.arkx.framework.core.bean.BeanManager;
import io.arkx.framework.core.bean.BeanProperty;
import io.arkx.framework.core.bean.BeanUtil;
import io.arkx.framework.core.castor.CastorService;
import io.arkx.framework.thirdparty.el.Constants;

/**
 * UI方法参数绑定器
 */
public class UIMethodBinder {
    /**
     * 将参数绑定到UIMethod实例（将当前请求中的变量设置到UIMethod的属性上，并将args中的值按类型匹配到方法参数上）
     *
     * @param m
     *            UIMethod实例
     * @param args
     *            参数列表
     */
    // public static void bind(UIMethod m, Object[] args) {
    // BeanUtil.fill(m, WebCurrent.getRequest());
    // BeanDescription bd = BeanManager.getBeanDescription(m.getClass());
    // for (BeanProperty bp : bd.getPropertyMap().values()) {
    // if (bp.canWrite()) {
    // for (Object arg : args) {
    // if (!Primitives.isPrimitives(arg) && !(arg instanceof String) &&
    // bp.getPropertyType().isInstance(arg)) {
    // bp.write(m, arg);
    // }
    // }
    // }
    // }
    // }
    public static void bind(UIMethod m, Object[] args) {
        BeanUtil.fill(m, WebCurrent.getRequest());
        BeanDescription bd = BeanManager.getBeanDescription(m.getClass());
        for (BeanProperty bp : bd.getPropertyMap().values()) {
            if (bp.canWrite()) {
                boolean flag = false;
                Object[] arrayOfObject;
                int j = (arrayOfObject = args).length;
                for (int i = 0; i < j; i++) {
                    Object arg = arrayOfObject[i];
                    if ((!Primitives.isPrimitives(arg)) && (!(arg instanceof String))
                            && (bp.getPropertyType().isInstance(arg))) {
                        bp.write(m, arg);
                        flag = true;
                        break;
                    }
                }
                try {
                    if ((!flag) && (WebCurrent.getRequest() != null)) {
                        for (Map.Entry<String, Object> e : WebCurrent.getRequest().entrySet()) {
                            if (((String) e.getKey()).equalsIgnoreCase(bp.getName())) {
                                bp.write(m, CastorService.toType(e.getValue(), bp.getPropertyType()));
                                flag = true;
                                break;
                            }
                        }
                    }
                    if ((!flag) && (WebCurrent.getResponse() != null)) {
                        for (Map.Entry<String, Object> e : WebCurrent.getResponse().entrySet()) {
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
     * @param m
     *            方法
     * @param args
     *            参数列表
     * @return 将args转换成匹配方法参数类型的数组
     */
    // public static Object[] convertArg(Method m, Object[] args, String[] params) {
    // Class<?>[] cs = m.getParameterTypes();
    // if (cs == null || cs.length == 0) {
    // return Constants.NoArgs;
    // }
    // Object[] arr = new Object[cs.length];
    // int i = 0;
    // for (Class<?> c : cs) {
    // for (Object obj : args) {
    // if (c.isInstance(obj)) {
    // arr[i] = obj;
    // break;
    // }
    // }
    // i++;
    // }
    // return arr;
    // }
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
                    if ((!flag) && (WebCurrent.getRequest() != null)) {
                        for (Entry<String, Object> e : WebCurrent.getRequest().entrySet()) {
                            if (e.getKey().equalsIgnoreCase(name)) {
                                arr[i] = CastorService.toType(e.getValue(), cs[i]);
                                flag = true;
                                break;
                            }
                        }
                    }
                    if ((!flag) && (WebCurrent.getResponse() != null)) {
                        for (Entry<String, Object> e : WebCurrent.getResponse().entrySet()) {
                            if (e.getKey().equalsIgnoreCase(name)) {
                                arr[i] = CastorService.toType(e.getValue(), cs[i]);
                                break;
                            }
                        }
                    }
                } catch (Exception localException) {
                }
            }
            if (arr[i] == null) {
                if (cs[i] == Mapx.class) {
                    arr[i] = WebCurrent.getRequest();
                } else {
                    arr[i] = ClassUtil.mapToObject(cs[i], WebCurrent.getRequest());
                }
            }
        }
        return arr;
    }
}
