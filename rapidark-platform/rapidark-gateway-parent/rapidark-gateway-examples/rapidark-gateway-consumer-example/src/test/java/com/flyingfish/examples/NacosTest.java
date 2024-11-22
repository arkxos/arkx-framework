package com.flyingfish.examples;

import java.util.List;
import java.util.Properties;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;

/**
 * @Description
 * @Author JL
 * @Date 2022/12/22
 * @Version V1.0
 */
public class NacosTest {


    public static void main(String[] args) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", "127.0.0.1");
//        properties.setProperty("namespace", "public");

        NamingService naming = NamingFactory.createNamingService(properties);


        List<Instance> instances = naming.getAllInstances("provider-examples");
        for (Instance instance : instances){
            System.out.println(instance.getServiceName());
            System.out.println(instance.getIp());
            System.out.println(instance.getPort());
            System.out.println("====================");
        }

//        naming.registerInstance("provider-examples", "11.11.11.11", 8888, "TEST1");
//        naming.registerInstance("provider-examples", "2.2.2.2", 9999, "DEFAULT");
        System.out.println(naming.getAllInstances("provider-examples"));
//        naming.deregisterInstance("provider-examples", "2.2.2.2", 9999, "DEFAULT");
//        System.out.println(naming.getAllInstances("provider-examples"));

        naming.subscribe("provider-examples", new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(((NamingEvent)event).getServiceName());
                System.out.println(((NamingEvent)event).getInstances());
            }
        });
    }
}
