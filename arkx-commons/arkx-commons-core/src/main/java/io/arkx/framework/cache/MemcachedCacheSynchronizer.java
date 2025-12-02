package io.arkx.framework.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.extend.ExtendManager;
import io.arkx.framework.json.JSON;
import io.arkx.framework.json.JSONObject;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

public class MemcachedCacheSynchronizer implements ICacheSynchronizer {

	private MemcachedClient memcachedClient;

	private String hash;

	public static final String MEMCACHEKEY = "_Ark_Cache_Refresh_";

	private BlockingQueue<String[]> queue;

	public MemcachedCacheSynchronizer() {
		this.queue = new LinkedBlockingQueue<>();
		this.hash = UUID.randomUUID().toString();
		this.memcachedClient = MemCachedManager.getClient();
		if (this.memcachedClient != null) {
			final int expires = Integer.parseInt(Config.getValue("App.CacheSyncExpires"));
			final JSONObject o = new JSONObject();

			new Thread() {
				public void run() {
					JSONObject json = new JSONObject();
					o.put(MemcachedCacheSynchronizer.this.hash, json);
					for (;;) {
						try {
							String[] p = (String[]) MemcachedCacheSynchronizer.this.queue.take();
							JSONObject pj = json.getJSONObject(p[0]);
							if (pj == null) {
								pj = new JSONObject();
								json.put(p[0], pj);
							}
							if (p[2] == null) {
								pj.put(p[1], Integer.valueOf(0));
							}
							else {
								Object o = pj.get(p[1]);
								if (o == null) {
									JSONObject tj = new JSONObject();
									pj.put(p[1], tj);
									tj.put(p[2], Integer.valueOf(0));
								}
								else if ((o instanceof JSONObject)) {
									JSONObject tj = (JSONObject) o;
									tj.put(p[2], Integer.valueOf(0));
								}
							}
							if (MemcachedCacheSynchronizer.this.queue.isEmpty()) {
								if (MemcachedCacheSynchronizer.this.memcachedClient.add("_Ark_Cache_Refresh_", expires,
										o.toString())) {
									json.clear();
								}
								else {
									Thread.sleep(expires * 1000);
									if (!MemcachedCacheSynchronizer.this.queue.isEmpty()) {
									}
								}
							}
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
						catch (TimeoutException e) {
							e.printStackTrace();
						}
						catch (MemcachedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	}

	public void refresh(String provider, String type, String key) {
		try {
			this.queue.put(new String[] { provider, type, key });
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void refresh(String provider, String type) {
		refresh(provider, type, null);
	}

	public void sync() {
		try {
			Object obj = this.memcachedClient.get("_Ark_Cache_Refresh_");
			if ((obj instanceof String)) {
				JSONObject m = JSON.parseJSONObject(obj.toString());
				for (Map.Entry<String, Object> si : m.entrySet()) {
					if (!this.hash.equals(si.getKey())) {
						JSONObject sj = (JSONObject) si.getValue();
						for (Map.Entry<String, Object> pi : sj.entrySet()) {
							String p = (String) pi.getKey();
							CacheDataProvider cp = CacheManager.getCache(p);
							if (cp != null) {
								JSONObject pj = (JSONObject) pi.getValue();
								for (Map.Entry<String, Object> ti : pj.entrySet()) {
									String t = (String) ti.getKey();
									Object o = ti.getValue();
									if ((o instanceof JSONObject)) {
										Map<String, Object> map = (Map) cp.TypeMap.get(t);
										if (map == null) {
											LogUtil.warn("CacheRefresh.run():Can't found cache type '" + t
													+ "' in CacheProvider " + p);
										}
										else {
											JSONObject tj = (JSONObject) o;
											for (Map.Entry<String, Object> ki : tj.entrySet()) {
												map.remove(ki.getKey());
											}
										}
									}
									else {
										CacheManager.setMapx(cp, t, null);
									}
								}
							}
						}
						ExtendManager.invoke("io.arkx.framework.AfterClusteringRefresh", new Object[] { sj });
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
