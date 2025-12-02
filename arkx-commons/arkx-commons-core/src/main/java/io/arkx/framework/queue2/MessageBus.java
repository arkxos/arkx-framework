package io.arkx.framework.queue2;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.arkx.framework.commons.util.lang.ReflectionUtil;

/**
 * 事件处理相关操作工具类
 *
 * @author Darkness
 * @date 2013-11-27 下午7:23:02
 * @version V1.0
 * @since ark 1.0
 */
public class MessageBus {

    private static MessageBus instance = new MessageBus();

    public static MessageBus globalInstance() {
        return instance;
    }

    /** KEY：事件类的类名，值：所有监听此事件的处理类实例 */
    private Map<String, List<LisenerInfo>> listeners = new LinkedHashMap<>();

    public MessageBus() {
        // no instance
    }

    /**
     * 扫瞄所有bean,进行事件监听（业务类按事件类型归类），此方法要在系统启动完后立即调用 此方法大概过程是
     * 1、从SPRING中找出所实现了IBaseEventListener的具体业务类实例 2、把这些实例归类装进MAP变量listeners中，
     * 此MAP变量的结构是： "UserDeleteEvent.class",{ortherServiceImpl,xxxServiceImpl,...}
     * "UserUpdateEvent.class",{yyyServiceImpl,zzzServiceImpl,...} key,valueList
     */
    public void register(Collection<?> eventListeners) {

        if (eventListeners == null || eventListeners.size() == 0)
            return;

        // 下面循环进行归类
        for (Object listener : eventListeners) {
            register(listener);
        }
    }

    @SuppressWarnings("rawtypes")
    public void register(Object listener) {
        // 注意这里不能使用listener.getClass()方法，因此方法返回的只是SPRING的代理类，此代理类的方法没有注解信息
        // Method[] methods = listener.getRealClass().getDeclaredMethods();
        Method[] methods = ReflectionUtil.getDeclaredMethods(listener);

        for (Method method : methods) {
            // 判断方法中是否有指定注解类型的注解
            boolean hasAnnotation = method.isAnnotationPresent(Subscribe.class);
            if (hasAnnotation) {
                // 根据注解类型返回方法的指定类型注解
                Subscribe annotation = method.getAnnotation(Subscribe.class);

                {// 处理EventClass
                    Class<? extends Message>[] events = annotation.events();
                    if (events != null && events.length > 0) {// 这里过滤掉没有真正实现事件监听的业务类
                        for (Class<? extends Message> event : events) {
                            String methodName = method.getName();

                            Method listenEventMethod = null;
                            try {
                                listenEventMethod = listener.getClass().getMethod(methodName,
                                        method.getParameterTypes());
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException("初始化事件监听器时出错：", e);
                            }

                            List<LisenerInfo> listenerInfos = listeners.get(event.getName());
                            if (listenerInfos == null) {
                                listenerInfos = new ArrayList<LisenerInfo>();
                                put(event.getName(), listenerInfos);
                            }
                            // 注意这里要用代理类的方法，即listener.getClass().getMethod(method.getName())，不能直接使用method变量，下同
                            listenerInfos.add(new LisenerInfo(listener, listenEventMethod));
                        }
                    }
                }

                {// 处理EventName
                    String[] eventNames = annotation.eventNames();
                    if (eventNames == null || eventNames.length == 0) {// 这里过滤掉没有真正实现事件监听的业务类
                        continue;
                    }
                    for (String eventName : eventNames) {
                        String methodName = method.getName();

                        Method listenEventMethod = null;
                        try {
                            listenEventMethod = listener.getClass().getMethod(methodName, method.getParameterTypes());
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException("初始化事件监听器时出错：", e);
                        }

                        List<LisenerInfo> listenerInfos = listeners.get(eventName);
                        if (listenerInfos == null) {
                            listenerInfos = new ArrayList<LisenerInfo>();
                            put(eventName, listenerInfos);
                        }
                        // 注意这里要用代理类的方法，即listener.getClass().getMethod(method.getName())，不能直接使用method变量，下同
                        listenerInfos.add(new LisenerInfo(listener, listenEventMethod));
                    }
                }
            }
        }
    }

    public void unregister(Object listener) {
        for (String key : listeners.keySet()) {
            List<LisenerInfo> lisenerInfos = listeners.get(key);

            List<LisenerInfo> needRemoveLisenersInfos = new ArrayList<>();
            for (LisenerInfo lisenerInfo : lisenerInfos) {
                if (lisenerInfo.getService() == listener) {
                    needRemoveLisenersInfos.add(lisenerInfo);
                }
            }

            for (LisenerInfo lisenerInfo : needRemoveLisenersInfos) {
                lisenerInfos.remove(lisenerInfo);
            }
        }
    }

    private List<String> patternEventNames = new ArrayList<>();

    private void put(String eventName, List<LisenerInfo> listenerInfos) {
        listeners.put(eventName, listenerInfos);

        Matcher m = GLOB_PATTERN.matcher(eventName);
        if (m.find()) {
            patternEventNames.add(eventName);
        }
    }

    /**
     * 发布事件的静态方法
     */
    public void publish(Message<?> event) {
        // System.out.println("发布事件：" + event.getClass().getName() +
        // "["+event.name()+"]");

        // 根据实际事件名称，从listeners中找出监听了此事件的业务类，调用之
        List<LisenerInfo> list = listeners.get(event.getClass().getName());
        if (list != null && list.size() > 0) {
            for (LisenerInfo listener : list) {
                try {
                    Method method = listener.getMethod();
                    method.setAccessible(true);
                    method.invoke(listener.getService(), event);
                } catch (Exception e) {
                    // 此处不能捕捉异常，因为任何一个处理类实例出错都应该全部回滚
                    e.printStackTrace();
                }
            }
        }

        // 根据实际事件名称，从listeners中找出监听了此事件的业务类，调用之
        List<LisenerInfo> listByEventName = listByEventName(event.name());
        if (listByEventName != null && listByEventName.size() > 0) {
            for (LisenerInfo listener : listByEventName) {
                try {
                    listener.getMethod().invoke(listener.getService(), event);
                } catch (Exception e) {
                    // 此处不能捕捉异常，因为任何一个处理类实例出错都应该全部回滚
                    e.printStackTrace();
                }
            }
        }
    }

    private List<LisenerInfo> listByEventName(String eventName) {
        List<LisenerInfo> result = new ArrayList<>();

        for (String patternEventName : patternEventNames) {
            Pattern pattern = createPattern(patternEventName);
            Matcher m = pattern.matcher(eventName);
            if (m.matches()) {
                List<LisenerInfo> lisenerInfos = listeners.get(patternEventName);
                if (lisenerInfos != null) {
                    result.addAll(lisenerInfos);
                }
            }
        }

        List<LisenerInfo> lisenerInfos = listeners.get(eventName);
        if (lisenerInfos != null) {
            result.addAll(lisenerInfos);
        }
        return result;
    }

    private final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

    private Pattern createPattern(String pattern) {
        StringBuilder patternBuilder = new StringBuilder();
        Matcher m = GLOB_PATTERN.matcher(pattern);
        int end = 0;
        while (m.find()) {
            patternBuilder.append(quote(pattern, end, m.start()));
            String match = m.group();
            if ("?".equals(match)) {
                patternBuilder.append('.');
            } else if ("*".equals(match)) {
                patternBuilder.append(".*");
            }
            // else if (match.startsWith("{") && match.endsWith("}")) {
            // int colonIdx = match.indexOf(':');
            // if (colonIdx == -1) {
            // patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
            // variableNames.add(m.group(1));
            // }
            // else {
            // String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
            // patternBuilder.append('(');
            // patternBuilder.append(variablePattern);
            // patternBuilder.append(')');
            // String variableName = match.substring(1, colonIdx);
            // variableNames.add(variableName);
            // }
            // }
            end = m.end();
        }
        patternBuilder.append(quote(pattern, end, pattern.length()));
        return Pattern.compile(patternBuilder.toString());
    }

    private String quote(String pattern, int beginIndex, int endIndex) {
        return pattern.substring(beginIndex, endIndex);
    }

    public static void main(String[] args) {
        String patternEventName = "open-*";
        Pattern pattern = new MessageBus().createPattern(patternEventName);
        Matcher m = pattern.matcher("open-你好");
        System.out.println(m.matches());
    }
}

// 此类记录目标方法和目标类
class LisenerInfo {

    private Method method;// 目标方法
    private Object service;// 业务类实例

    public LisenerInfo(Object service, Method method) {
        this.method = method;
        this.service = service;
    }

    public Method getMethod() {
        return method;
    }

    public Object getService() {
        return service;
    }
}
