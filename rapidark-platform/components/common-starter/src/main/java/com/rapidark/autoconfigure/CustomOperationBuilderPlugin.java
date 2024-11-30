//package com.rapidark.autoconfigure;
//
//import com.google.common.base.Optional;
//import com.google.common.collect.Lists;
//import io.swagger.v3.oas.annotations.media.Schema;
//import javassist.ClassPool;
//import javassist.CtClass;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import springfox.documentation.RequestHandler;
//import springfox.documentation.service.StringVendorExtension;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.OperationBuilderPlugin;
//import springfox.documentation.spi.service.contexts.OperationContext;
//import springfox.documentation.spi.service.contexts.RequestMappingContext;
//import springfox.documentation.swagger.common.SwaggerPluginSupport;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.stream.Collectors;
//
///**
// * 通过编写插件实现接口按定义顺序排序
// * @author darkness
// * @version 1.0
// * @date 2021/6/7 12:52
// */
////@Component
//public class CustomOperationBuilderPlugin implements OperationBuilderPlugin {
//
//    private static final Logger log = LoggerFactory.getLogger(CustomOperationBuilderPlugin.class);
//
//    @Override
//    public boolean supports(DocumentationType documentationType) {
//        return SwaggerPluginSupport.pluginDoesApply(documentationType);
//    }
//
//
//    @Override
//    public void apply(OperationContext context) {
//        //1.没有ApiOperation注解的直接返回
//        Optional<ApiOperation> apiOperation = context.findAnnotation(ApiOperation.class);
//        if (!apiOperation.isPresent()) {
//            return;
//        }
//
//        //2.获取当前方法的位置，然后设置进position中
//        try {
//            Class<? extends OperationContext> operationContextClass = context.getClass();
//            Field requestContextFiled = operationContextClass.getDeclaredField("requestContext");
//            requestContextFiled.setAccessible(true);
//            RequestMappingContext requestContext = (RequestMappingContext) requestContextFiled.get(context);
//
//            Class<? extends RequestMappingContext> requestContextClass = requestContext.getClass();
//            Field handler = requestContextClass.getDeclaredField("handler");
//            handler.setAccessible(true);
//            RequestHandler requestHandler = (RequestHandler) handler.get(requestContext);
//
//            //得到当前handler对应的Controller
//            Class<?> aClass = requestHandler.declaringClass();
//            //获取所有方法
////            Method[] declaredMethods = aClass.getDeclaredMethods();
//            //获取当前api对应哪个方法
//            Method nowMethod = requestHandler.getHandlerMethod().getMethod();
//
//            //等到当前方法在所有方法中的位置(TO:位置变成了编译后的位置，需要找到编译前的位置)
////            int indexOf = ArrayUtils.indexOf(declaredMethods, nowMethod);
//            //使用javasisit获取到对应方法在原始类的多少行
//            int indexOf = getMethodOriginalLine(aClass, nowMethod);
//            if (indexOf != -1) {
//                //swagger-ui高版本不支持position排序了，解决方法是引入knife4j-spring-ui
//                //ps:找不到swagger-ui前端页面源代码加载位置，所以搞不定了~~~。有能力的小伙伴可以直接去修改swagger-ui的前端源代码
//                context.operationBuilder().position(indexOf);
//                //添加扩展参数：x-order（支持knife4j-spring-ui）
//                context.operationBuilder().extensions(Lists.newArrayList(new StringVendorExtension("x-order", indexOf + "")));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.warn("加载swagger中方法api={}，设置顺序出错。", context.getName(), e);
//        }
//    }
//
//    /**
//     * 获取方法在类中的原始开始行数
//     *
//     * @param clazz     原始类
//     * @param nowMethod 需要查找的哪个方法
//     * @return
//     */
//    private int getMethodOriginalLine(Class clazz, Method nowMethod) throws Exception {
//        ClassPool pool = ClassPool.getDefault();
//        String className = clazz.getName();
//        CtClass cc = pool.get(className);
//        Class<?>[] parameterTypes = nowMethod.getParameterTypes();
//        String[] objects = Arrays.stream(parameterTypes).map(Class::getName).collect(Collectors.toList()).toArray(new String[]{});
//        return cc.getDeclaredMethod(nowMethod.getName(), pool.get(objects)).getMethodInfo().getLineNumber(0);
//    }
//}