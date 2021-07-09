package com.rapidark.cloud.gateway.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestApiHelper
{

	private final static Logger logger = LoggerFactory.getLogger(RestApiHelper.class);

	public static RestTemplate restTemplate;

	public static void init() {
		restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
	}

	/**
	 * 通过GET方式发送请求
	 * 
	 * @param url
	 * @param urlParams
	 * @return
	 */
	public static String doGet(String url, HashMap<String, String> urlParams)
	{
		return doGet(url, urlParams, null);
	}

	public static String doGet(String url, HashMap<String, String> urlParams, HashMap<String, String> headers)
	{

		if(StringUtils.isBlank(url))
		{
			throw new RuntimeException("Incorrect url " + url);
		}

		String queryString = buildQueryString(urlParams);
		if(queryString != null)
		{
			url = url + "?" + queryString;
		}

		logger.info("Prepare sending request to {}", url);

		HttpHeaders httpHeaders = buildHttpHeaders(headers);

		HttpEntity<String> requestEntity = new HttpEntity<String>(httpHeaders);


		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

		if(responseEntity.getStatusCodeValue() != 200)
		{
			throw new RuntimeException("Fail , response code is " + responseEntity.getStatusCodeValue());
		}

		String result = responseEntity.getBody();

		logger.info("Get {} from {}", result, url);

		return result;
	}

	/**
	 * 通过POST方式发送请求
	 * 
	 * @param url
	 * @param httpBody
	 * @return
	 */
	public static String doPost(String url, String httpBody)
	{
		return doPost(url, httpBody, null);
	}

	public static String doPost(String url, String httpBody, HashMap<String, String> headers)
	{

		logger.info("Prepare sending request to {}", url);

		if(StringUtils.isBlank(url))
		{
			throw new RuntimeException("Incorrect url " + url);
		}

		HttpHeaders httpHeaders = buildHttpHeaders(headers);

		HttpEntity<String> requestEntity = new HttpEntity<String>(httpBody, httpHeaders);

		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		if(responseEntity.getStatusCodeValue() != 200)
		{
			throw new RuntimeException("Fail , response code is " + responseEntity.getStatusCodeValue());
		}

		String result = responseEntity.getBody();

		logger.info("Get {} from {}", result, url);

		return result;
	}

	private static String buildQueryString(HashMap<String, String> urlParams)
	{

		if(urlParams == null || urlParams.keySet().size() == 0)
		{
			return null;
		}

		StringBuilder queryStringBuilder = null;
		Iterator<String> iterator = urlParams.keySet().iterator();
		while(iterator.hasNext())
		{
			if(queryStringBuilder == null)
			{
				queryStringBuilder = new StringBuilder();
			}
			else
			{
				queryStringBuilder.append('&');
			}
			String paramKey = iterator.next();
			String paramValue = urlParams.get(paramKey);
			queryStringBuilder.append(paramKey).append('=').append(paramValue);
		}

		if(queryStringBuilder == null)
		{
			return null;
		}
		else
		{
			return queryStringBuilder.toString();
		}

	}

	private static HttpHeaders buildHttpHeaders(HashMap<String, String> headers)
	{

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		httpHeaders.add("Accept", MediaType.APPLICATION_JSON_UTF8.toString());

		if(headers != null && headers.keySet().size() > 0)
		{
			Iterator<String> headerIterator = headers.keySet().iterator();
			while(headerIterator.hasNext())
			{
				String headerName = headerIterator.next();
				String headerValue = headers.get(headerName);
				httpHeaders.add(headerName, headerValue);
			}
		}

		return httpHeaders;
	}

	/**
	 * 通过POST方式发送请求
	 * 
	 * @param url
	 * @param httpBody
	 * @return
	 */
	public static String doPostByServiceId(String url, String httpBody)
	{
		return doPostByServiceId(url, httpBody, null);
	}

	public static String doPostByServiceId(String url, String httpBody, HashMap<String, String> headers)
	{

		logger.info("Prepare sending request to {}", url);

		if(StringUtils.isBlank(url))
		{
			throw new RuntimeException("Incorrect url " + url);
		}

		HttpHeaders httpHeaders = buildHttpHeaders(headers);

		HttpEntity<String> requestEntity = new HttpEntity<String>(httpBody, httpHeaders);

		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

		if(responseEntity.getStatusCodeValue() != 200)
		{
			throw new RuntimeException("Fail , response code is " + responseEntity.getStatusCodeValue());
		}

		String result = responseEntity.getBody();

		logger.info("Get {} from {}", result, url);

		return result;
	}

	public static void main(String[] args) {
		RestApiHelper.init();
		String url = "http://localhost:8888/ltc/openapi/03";
		HashMap<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Bearer 541e2b95-24ca-4192-97b7-a9367ac676cc");
		headers.put("content-type", "application/json");
		String responseText = RestApiHelper.doPost(url, "{\"bizType\":1,\"endDate\":\"2022-07-31\",\"handlingId\":\"YYf8bbFQqRrFYBxPpFNrHV\",\"isMedicalInstitution\":1,\"operateDate\":\"2021-06-23\",\"orgId\":\"pdtsty001\",\"orgName\":\"测试评定\",\"orgType\":1,\"startDate\":\"2021-06-22\",\"uniformSocialCreditCode\":\"pdtsty001\"}", headers);
		System.out.println(responseText);
	}

}
