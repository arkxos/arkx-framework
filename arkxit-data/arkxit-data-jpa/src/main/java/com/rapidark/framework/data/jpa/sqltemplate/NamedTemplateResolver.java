package com.rapidark.framework.data.jpa.sqltemplate;

import java.util.Iterator;

import org.springframework.core.io.Resource;

/**
 * 
 * @author Darkness
 * @date 2020年10月29日 下午4:22:49
 * @version V1.0
 */
public interface NamedTemplateResolver {
	
    Iterator<Void> doInTemplateResource(Resource resource, final NamedTemplateCallback callback) throws Exception;
}