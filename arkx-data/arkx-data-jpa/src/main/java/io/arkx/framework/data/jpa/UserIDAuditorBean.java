package io.arkx.framework.data.jpa;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.arkxos.framework.Account;
import io.arkx.framework.boot.spring.axon.CurrentAuditor;
import com.arkxos.framework.commons.util.StringUtil;

/**
 * 设置审计
 * @author Darkness
 * @date 2019-07-19 14:16:42
 * @version V1.0
 */
@Component("auditorAware")
public class UserIDAuditorBean implements AuditorAware<String> {
	
	/**
	 * 获取操作哦人员账号
	 */
    @Override
    public Optional<String> getCurrentAuditor() {
    	if(CurrentAuditor.get() != null) {
    		return Optional.of(CurrentAuditor.get().getOperatorId());
    	}
    	if(!StringUtil.isEmpty(Account.getUserName())) {
    		return Optional.of(Account.getUserName());
    	}
//    	if(!StringUtil.isEmpty(SecurityUtils.getCurrentUsername())) {
//    		return Optional.of(SecurityUtils.getCurrentUsername());
//    	}
    	// 用户定时任务，或者无Token调用的情况
    	return Optional.of("System");
//        SecurityContext ctx = SecurityContextHolder.getContext();
//        if (ctx == null) {
//            return null;
//        }
//        if (ctx.getAuthentication() == null) {
//            return null;
//        }
//        if (ctx.getAuthentication().getPrincipal() == null) {
//            return null;
//        }
//        Object principal = ctx.getAuthentication().getPrincipal();
//        if (principal.getClass().isAssignableFrom(Long.class)) {
//            return (Long) principal;
//        } else {
//            return null;
//        }
    }
}
