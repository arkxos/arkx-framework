package com.rapidark.cloud.base.server.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.core.io.Resource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.PostConstruct;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/5/24 17:54
 */
@Component
@Slf4j
public class MapperHotDeployPlugin {
//    protected final Log log = LogFactory.getLog(getClass());
    @Value("${mybatis-plus.mapper-locations}")
    private String packageSerchPath;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    private Resource[] mapperLocations;
    private Configuration configuration;
    private HashMap<String, Long> fileMapping = new HashMap<String, Long>();// 记录文件是否变化
    @Value("${mybatis.mapper.reload}")
    private String reloadFlag;

    @PostConstruct
    public void init() {
        try {
            if(prepareEnv()) return;
            Runnable runnable = new Runnable() {
                public void run() {
                    changeCompare();
                }
            };
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            service.scheduleAtFixedRate(runnable, 1, 10, TimeUnit.SECONDS);
            log.info("============mapper热部署已生效=============");
        } catch (Exception e) {
            log.error("packageSearchPath扫描包路径配置错误");
            return;
        }
    }
    /**
     * 功能描述: 准备赋值环境变量
     * @param:
     * @return:
     * @auther: zenglingsheng
     * @date: 2020/10/21 8:43
     */
    public boolean prepareEnv() throws Exception{
        boolean flag=true;
        this.configuration = sqlSessionFactory.getConfiguration();
        this.mapperLocations = new PathMatchingResourcePatternResolver().getResources(packageSerchPath);
        for (Resource resource : mapperLocations) {
            String resourceName = resource.getFilename();
            long lastFrame = resource.contentLength() + resource.lastModified();
            fileMapping.put(resourceName, Long.valueOf(lastFrame));// 文件内容帧值
        }
        if("true".equals(reloadFlag)) flag= false;
        return flag;
    }
    /**
     * 功能描述:判断xml文件是否修改方法
     * @param:
     * @return:
     * @auther: zenglingsheng
     * @date: 2020/10/21 8:35
     */
    public void  changeCompare(){
        // task to run goes here
        try {
            // 判断是否有文件发生了变化
            if (isChanged()) {
                // 清理
                removeConfig(configuration);
                // 重新加载
                for (Resource configLocation : mapperLocations) {
                    try {
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configLocation.getInputStream(),
                                configuration, configLocation.toString(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                    } catch (IOException e) {
                        log.error("mapper文件[" + configLocation.getFilename() + "]不存在或内容格式不对");
                        continue;
                    }
                }
                log.info("-------mapper文件已全部更新-------");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 判断文件是否发生了变化
     */
    boolean isChanged() throws IOException {

        boolean flag = false;

        for (Resource resource : mapperLocations) {
            String resourceName = resource.getFilename();
            boolean addFlag = !fileMapping.isEmpty() && !fileMapping.containsKey(resourceName);// 此为新增标识
            // 修改文件:判断文件内容是否有变化
            Long compareFrame = fileMapping.get(resourceName);
            long lastFrame = resource.contentLength() + resource.lastModified();
            boolean modifyFlag = null != compareFrame && compareFrame != lastFrame;// 此为修改标识
            fileMapping.put(resourceName, Long.valueOf(lastFrame));// 文件内容帧值
            // 新增或是修改时,存储文件
            if (addFlag || modifyFlag) {
                flag = true;
                log.info("-------" + resourceName + "文件 已修改-------");
            }
        }
        return flag;
    }

    /**
     * 清空Configuration中几个重要的缓存
     * @param configuration
     * @throws Exception
     */
    private void removeConfig(Configuration configuration) throws Exception {
        Class<?> classConfig = configuration.getClass().getSuperclass();
        clearMap(classConfig, configuration, "mappedStatements");
        clearMap(classConfig, configuration, "caches");
        clearMap(classConfig, configuration, "resultMaps");
        clearMap(classConfig, configuration, "parameterMaps");
        clearMap(classConfig, configuration, "keyGenerators");
        clearMap(classConfig, configuration, "sqlFragments");
        clearSet(classConfig, configuration, "loadedResources");
    }

    @SuppressWarnings("rawtypes")
    private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Map mapConfig = (Map) field.get(configuration);
        mapConfig.clear();
    }

    @SuppressWarnings("rawtypes")
    private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Set setConfig = (Set) field.get(configuration);
        setConfig.clear();
    }
}

