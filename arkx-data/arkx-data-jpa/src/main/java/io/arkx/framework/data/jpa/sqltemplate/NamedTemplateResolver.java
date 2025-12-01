package io.arkx.framework.data.jpa.sqltemplate;

import org.springframework.core.io.Resource;

import java.util.Iterator;

/**
 * 
 * @author Darkness
 * @date 2020年10月29日 下午4:22:49
 * @version V1.0
 */
public interface NamedTemplateResolver {
	
    Iterator<Void> doInTemplateResource(Resource resource, final NamedTemplateCallback callback) throws Exception;
}