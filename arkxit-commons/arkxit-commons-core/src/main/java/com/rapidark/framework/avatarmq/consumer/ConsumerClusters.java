package com.rapidark.framework.avatarmq.consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.rapidark.framework.avatarmq.model.RemoteChannelData;
import com.rapidark.framework.avatarmq.model.SubscriptionData;
import com.rapidark.framework.avatarmq.netty.NettyUtil;

import io.netty.channel.Channel;

/**
 * 负责定义消费者集群的行为，以及负责消息的路由
 * @filename:ConsumerClusters.java
 * @description:ConsumerClusters功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class ConsumerClusters {

	//轮询调度（Round-Robin Scheduling）位置标记
    private int next = 0;
    private final String clustersId;
    /*生产者消息的主题, 消息对应的topic信息数据结构*/
	private final ConcurrentHashMap<String, SubscriptionData> subMap = new ConcurrentHashMap<>();

	/*消费者标识编码, 对应的消费者的netty网络通信管道信息*/
	private final ConcurrentHashMap<String, RemoteChannelData> channelMap = new ConcurrentHashMap<>();

    private final List<RemoteChannelData> channelList = Collections.synchronizedList(new ArrayList<RemoteChannelData>());

    public ConsumerClusters(String clustersId) {
        this.clustersId = clustersId;
    }

    public String getClustersId() {
        return clustersId;
    }

    public ConcurrentHashMap<String, SubscriptionData> getSubMap() {
        return subMap;
    }

    public ConcurrentHashMap<String, RemoteChannelData> getChannelMap() {
        return channelMap;
    }

	// 添加一个消费者到消费者集群
    public void attachRemoteChannelData(String clientId, RemoteChannelData channelinfo) {
        if (findRemoteChannelData(channelinfo.getClientId()) == null) {
            channelMap.put(clientId, channelinfo);
            subMap.put(channelinfo.getSubcript().getTopic(), channelinfo.getSubcript());
            channelList.add(channelinfo);
        } else {
            System.out.println("consumer clusters exists! it's clientId:" + clientId);
        }
    }

	// 从消费者集群中删除一个消费者
    public void detachRemoteChannelData(String clientId) {
        channelMap.remove(clientId);

        Predicate predicate = new Predicate() {
            public boolean evaluate(Object object) {
                String id = ((RemoteChannelData) object).getClientId();
                return id.compareTo(clientId) == 0;
            }
        };

        RemoteChannelData data = (RemoteChannelData) CollectionUtils.find(channelList, predicate);
        if (data != null) {
            channelList.remove(data);
        }
    }

	// 根据消费者标识编码，在消费者集群中查找定位一个消费者，如果不存在返回null
    public RemoteChannelData findRemoteChannelData(String clientId) {
    	return channelMap.get(clientId);
    }

	// 负载均衡，根据连接到broker的顺序，依次投递消息给消费者。这里的均衡算法直接采用
	// 轮询调度（Round-Robin Scheduling），后续可以加入：加权轮询、随机轮询、哈希轮询等等策略。
    public RemoteChannelData nextRemoteChannelData() {

        Predicate predicate = new Predicate() {
            public boolean evaluate(Object object) {
                RemoteChannelData data = (RemoteChannelData) object;
                Channel channel = data.getChannel();
                return NettyUtil.validateChannel(channel);
            }
        };

        CollectionUtils.filter(channelList, predicate);
        return channelList.get(next++ % channelList.size());
    }

	// 根据生产者的主题关键字，定位于具体的消息结构
    public SubscriptionData findSubscriptionData(String topic) {
        return this.subMap.get(topic);
    }
}
