/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.arkxos.framework.boot.ip2region;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import com.arkxos.framework.boot.ip2region.ext.RegionAddress;
import com.arkxos.framework.boot.ip2region.ext.RegionEnum;
import com.arkxos.framework.boot.ip2region.ext.XdbSearcher;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class IP2regionTemplate implements DisposableBean {

	/**
	 * 用于IP定位转换
	 */
	public static final String REGION = "内网IP|内网IP";
	// IP归属地查询
	public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp?ip=%s&json=true";

	protected ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	protected XdbSearcher xdbSearcher;

	public IP2regionTemplate(XdbSearcher xdbSearcher) throws IOException {
		this.xdbSearcher = xdbSearcher;
	}

	/**
	 * 根据ip获取详细地址
	 */
	public String getLocalCityInfo(String ip) {
		try {
			String region = getRegion(ip);
			String address = region.replace("0|", "");
			char symbol = '|';
			if (address.charAt(address.length() - 1) == symbol) {
				address = address.substring(0, address.length() - 1);
			}
			return address.equals(REGION) ? "内网IP" : address;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * 根据ip获取详细地址
	 */
	public String getCityInfo(String ip) {
		if (xdbSearcher != null) {
			return getLocalCityInfo(ip);
		} else {
			return getHttpCityInfo(ip);
		}
	}

	/**
	 * 根据ip获取详细地址
	 */
	public static String getHttpCityInfo(String ip) {
		String api = String.format(IP_URL, ip);
		JSONObject object = JSONUtil.parseObj(HttpUtil.get(api));
		return object.get("addr", String.class);
	}

	public String memorySearch(long ip) throws IOException {
		return xdbSearcher.memorySearch(ip);
	}

	public String memorySearch(String ip) throws IOException {
		return xdbSearcher.memorySearch(ip);
	}

	public static boolean isIpAddress( String ip ) {
		String[] p = ip.split("\\.");
		if ( p.length != 4 ) return false;

		for ( String pp : p ) {
			if ( pp.length() > 3 ) return false;
			int val = Integer.valueOf(pp);
			if ( val > 255 ) return false;
		}

		return true;
	}

	/**
	 * 解析IP
	 *
	 * @param ip
	 * @return
	 */
	public String getRegion(String ip) {
		if("localhost".equals(ip)
				|| "127.0.0.1".equals(ip)) {
			return "127.0.0.1";
		}
//		if (!Util.isIpAddress(ip)) {
//			log.warn("warning: Invalid ip address");
//		}
		try {
			rwl.readLock().lock();
			String region = xdbSearcher.memorySearch(ip);
			return region;
		} catch (Exception e) {
			log.error("IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return XdbSearcher.NOT_MATCH;
		} finally {
			rwl.readLock().unlock();
		}
	}

	public RegionAddress getRegionAddress(String ip) {
		try {
			rwl.readLock().lock();
			// {region: 美国|0|华盛顿|0|谷歌, ioCount: 7, took: 82 μs}
			String region = xdbSearcher.memorySearch(ip);
			return new RegionAddress(region.split("\\|"));
		} catch (Exception e) {
			log.error(" IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return XdbSearcher.NOT_MATCH_REGION_ADDRESS;
		} finally {
			rwl.readLock().unlock();
		}
	}

	public RegionEnum getRegionByIp(String ip) {
		try {
			rwl.readLock().lock();
			String region = xdbSearcher.memorySearch(ip);
			String[] regionArr = region.split("\\|");
			log.info(" IP : {} >> Country : {} ", ip, regionArr[0]);
			if(XdbSearcher.NOT_MATCH.contains(regionArr[0])){
				return RegionEnum.UK;
			}
			RegionAddress address = new RegionAddress(regionArr);
			return RegionEnum.getByRegionAddress(address);
		} catch (Exception e) {
			log.error(" IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return RegionEnum.UK;
		} finally {
			rwl.readLock().unlock();
		}
	}

	public String getCountryByIp(String ip) {
		try {
			rwl.readLock().lock();
			String region = xdbSearcher.memorySearch(ip);
			String country = region.split("\\|")[0];
			log.info(" IP : {} >> Country/Region : {} ", ip, country);
			return XdbSearcher.NOT_MATCH.contains(country) ? RegionEnum.UK.getCname() : country;
		} catch (Exception e) {
			log.error(" IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return RegionEnum.UK.getCname();
		} finally {
			rwl.readLock().unlock();
		}
	}

	public boolean isMainlandIp(String ip) {
		RegionEnum regionEnum = this.getRegionByIp(ip);
		return RegionEnum.CN.compareTo(regionEnum) == 0 &&
				RegionEnum.HK.compareTo(regionEnum) != 0 &&
				RegionEnum.MO.compareTo(regionEnum) != 0 &&
				RegionEnum.TW.compareTo(regionEnum) != 0;
	}

	@Override
	public void destroy() throws Exception {
		if(Objects.nonNull(xdbSearcher)){
			xdbSearcher.destroy();
		}
	}

}
