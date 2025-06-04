// Decompiled by DJ v3.12.12.101 Copyright 2016 Atanas Neshkov  Date: 2016/5/16 13:24:54
// Home Page:  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MemCachedManager.java

package io.arkx.framework.cache;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.Config;
import io.arkx.framework.commons.util.StringUtil;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemCachedManager
{

    public MemCachedManager()
    {
    }

    public static MemcachedClient getClient()
    {
        if(client != null)
            return client;
        lock.lock();
        try
        {
            String cfg = Config.getValue("App.MemcachedHost");
            if(StringUtil.isNotEmpty(cfg))
            {
                java.util.List hosts = AddrUtil.getAddresses(cfg);
                MemcachedClientBuilder builder = new XMemcachedClientBuilder(hosts);
                client = builder.build();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        lock.unlock();
        return client;
    }

    private static MemcachedClient client;
    private static Lock lock = new ReentrantLock();

}
