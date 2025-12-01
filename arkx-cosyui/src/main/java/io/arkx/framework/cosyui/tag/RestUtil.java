package io.arkx.framework.cosyui.tag;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.cosyui.control.PagedData;
import io.arkx.framework.cosyui.control.tree.Tree;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 *  
 * @author Darkness
 * @date 2016年12月20日 下午4:55:22
 * @version V1.0
 */
public class RestUtil {

//	public static Map<String, Object> getResponse(String method) {
//		try {
//			RestTemplate restTemplate = new RestTemplate();
//			JsonResult jsonResult = restTemplate.getForObject(basePath() + method, JsonResult.class);
//			
//			return jsonResult.getExtraData();
//		} catch (RestClientException e) {
//			e.printStackTrace();
//			System.err.println("rest invoke method:" + method);
//		}
//		
//		return new HashMap<>();
//	}
	
	private static String basePath() {
		HttpServletRequest request = WebCurrent.getRequest().getServletRequest();
		String path2 = request.getContextPath();
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path2;
		if(!basePath.endsWith("/")){
			basePath += "/";
		}
		return basePath;
	}
	
	public static JsonResult post(String method) {
		return post(method, WebCurrent.getRequest(), Object.class);
	}
	
	public static JsonResult post(String method, Class<?> clazz) {
		return post(method, WebCurrent.getRequest(), clazz);
	}
	
	public static JsonResult post(String method, Object object, Class<?> clazz) {
		HttpServletRequest request = WebCurrent.getRequest().getServletRequest();
		HttpHeaders headers = new HttpHeaders();
		         MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		       headers.setContentType(type);
		        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		Cookie[] cookies = request.getCookies();
		List<String> cookiesList = new ArrayList<>();
		if(cookies != null) {
			for (Cookie cookie : cookies) {
				String key = cookie.getName();
				String value = cookie.getValue();
				cookiesList.add(key + "=" + value);
			}
		}
		headers.add("Cookie", Joiner.on(";").join(cookiesList));
		HttpEntity<String> requestEntity = new HttpEntity<String>(JSON.toJSONString(object), headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = basePath() + method;
		String result = "";
		try {
			result = restTemplate.postForObject(url, requestEntity, String.class);
		} catch (RestClientException e) {
			System.out.println("rest error:" + url);
			e.printStackTrace();
			throw e;
		}
		JsonResult jsonResult = JSON.parseObject(result, JsonResult.class);
		
		if(clazz == PagedData.class) {
			JSONObject jsonObject = JSON.parseObject(result);
			String data = jsonObject.getString("data");
			PagedData pagedData = JSON.parseObject(data, PagedData.class);
			jsonResult.setData(pagedData);
		} else if(clazz == DataTable.class) {
			JSONObject jsonObject = JSON.parseObject(result);
			String data = jsonObject.getString("data");
			DataTable dataTable = io.arkx.framework.json.JSON.parseBean(data, DataTable.class);
			jsonResult.setData(dataTable);
		} else if(clazz == Tree.class) {
			JSONObject jsonObject = JSON.parseObject(result);
			String data = jsonObject.getString("data");
			Tree tree = JSON.parseObject(data, Tree.class);
			jsonResult.setData(tree);
		}
		
		return jsonResult;
	}

}
