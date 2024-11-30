package com.rapidark.cloud.platform.gateway.service.load;

import com.rapidark.cloud.platform.gateway.framework.entity.GroovyScript;
import com.rapidark.cloud.platform.gateway.framework.service.GroovyScriptService;
import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import com.rapidark.cloud.platform.gateway.framework.util.Md5Utils;
import com.rapidark.cloud.platform.gateway.service.DynamicGroovyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * @Description 初始化加载groovyScript规则引擎动态脚本
 * @Author JL
 * @Date 2022/2/22
 * @Version V1.0
 */
@Slf4j
@Service
public class InitGroovyService implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private GroovyScriptService groovyScriptService;
    @Resource
    private DynamicGroovyService dynamicGroovyService;

    /**
     *  初始化加载groovyScript规则引擎动态脚本，并缓存实例化对象
     */
    public void initLoadGroovyScript(){
        //查询已启用的groovyScript规则引擎动态脚本
        GroovyScript groovyScript = new GroovyScript();
        groovyScript.setStatus(Constants.YES);
        List<GroovyScript> groovyScriptList = groovyScriptService.list(groovyScript);
        if (CollectionUtils.isEmpty(groovyScriptList)){
            log.info("未初始化groovyScript规则引擎动态脚本，脚本集合数量：0！");
            return ;
        }
        String md5;
        for (GroovyScript script : groovyScriptList){
            md5 = Md5Utils.md5Str(script.getId() + script.getContent());
            dynamicGroovyService.instance(script, md5, true);
        }
    }

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		this.initLoadGroovyScript();
	}
}
