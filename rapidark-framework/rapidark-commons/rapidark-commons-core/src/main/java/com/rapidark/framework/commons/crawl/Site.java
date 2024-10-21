package com.rapidark.framework.commons.crawl;

import java.util.ArrayList;
import java.util.List;

/**   
 * 
 * @author Darkness
 * @date 2015-1-9 下午3:04:41 
 * @version V1.0   
 */
public class Site {
	
	private String domain;
	private List<String> startUrls;
	
	public Site(String domain) {
		this.domain = domain;
		this.startUrls = new ArrayList<>();
	}
	
	public String domain() {
		return domain;
	}

	public void addStartUrl(String startUrl) {
		this.startUrls.add(startUrl);
	}
	
	public List<String> startUrls() {
		return this.startUrls;
	}

}
