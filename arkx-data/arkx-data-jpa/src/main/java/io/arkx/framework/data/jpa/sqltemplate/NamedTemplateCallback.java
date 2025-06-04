package io.arkx.framework.data.jpa.sqltemplate;

/**
 * .
 *
 * @author stormning on 2016/12/17.
 */
public interface NamedTemplateCallback {
	
	void process(String templateName, String content);
}
