package com.rapidark.framework.commons.crawl;
/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:08:23 
 * @version V1.0   
 */
public interface PageProcessor {

	void process(Page page);
	
	Site getSite();
}
