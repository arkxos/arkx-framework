//package com.rapidark.cloud.platform.gateway.manage.rest;
//
//import com.alibaba.csp.sentinel.annotation.SentinelResource;
//import com.alibaba.csp.sentinel.slots.block.BlockException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @Description
// * @Author JL
// * @Date 2022/12/09
// * @Version V1.0
// */
//@Slf4j
//@RestController
//@RequestMapping("/sentinel")
//public class SentinelRest {
//
//    /**
//     * 查询nacos配置信息
//     * SentinelResource.Sentinel:提供了 @SentinelResource 注解用于定义资源，并提供了 AspectJ 的扩展用于自动定义资源、处理 BlockException 等.
//     * SentinelResource.value:规则名称，fallback：发生异常降级回调方法，blockHandler：限流回调方法，exceptionsToIgnore：需排查的异常类型
//     * @return
//     */
//    @SentinelResource(value = "EXAMPLES-getUser", fallback = "sentinelFallback", blockHandler = "sentinelBlockHandler")
//    @RequestMapping("/info")
//    public String info(){
//        log.info("--------info--------");
//        return HttpStatus.OK.toString();
//    }
//
//    /**
//     * 当@SentinelResource注解的业务方法内抛出异常，则进入到此回滚方法内
//     * @param throwable
//     * @return
//     */
//    public String sentinelFallback(Throwable throwable){
//        log.info("--------/sentinelFallback-------, error: {}", throwable.getLocalizedMessage());
//        return HttpStatus.INTERNAL_SERVER_ERROR.toString();
//    }
//
//    /**
//     * 注意：自定义限流方法 sentinelBlockHandler()方法要保持参数、返回类型与原调用方法一致，否则会抛UndeclaredThrowableException
//     * @param be
//     * @return
//     */
//    public String sentinelBlockHandler(BlockException be){
//        log.info("--------/sentinelBlock-------, rule:{}", be.getRule().getResource());
//        return HttpStatus.FORBIDDEN.toString();
//    }
//}
