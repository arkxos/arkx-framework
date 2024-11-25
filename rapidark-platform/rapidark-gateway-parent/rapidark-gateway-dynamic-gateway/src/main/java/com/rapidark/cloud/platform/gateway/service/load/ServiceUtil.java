package com.rapidark.cloud.platform.gateway.service.load;

import com.rapidark.cloud.platform.gateway.framework.util.Constants;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class ServiceUtil {

	/**
	 * 封状URI
	 * @param uri
	 * @return
	 */
	public static URI getURI(String uri){
		if(uri.startsWith(Constants.HTTP) || uri.startsWith(Constants.HTTPS)){
			return UriComponentsBuilder.fromHttpUrl(uri).build().toUri();
		}else{
			// uri为lb://consumer-service
			return URI.create(uri);
		}
	}

}
