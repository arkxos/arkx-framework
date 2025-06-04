package io.arkx.framework.avatarmq.model;

import org.apache.commons.lang3.builder.EqualsBuilder;

import io.netty.channel.Channel;

/**
 * @filename:RemoteChannelData.java
 * @description:RemoteChannelData功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class RemoteChannelData {

    private Channel channel;
    private String clientId;

    private SubscriptionData subcript;

    public SubscriptionData getSubcript() {
        return subcript;
    }

    public void setSubcript(SubscriptionData subcript) {
        this.subcript = subcript;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getClientId() {
        return clientId;
    }

    public RemoteChannelData(Channel channel, String clientId) {
        this.channel = channel;
        this.clientId = clientId;
    }

    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && RemoteChannelData.class.isAssignableFrom(obj.getClass())) {
            RemoteChannelData info = (RemoteChannelData) obj;
            result = new EqualsBuilder().append(clientId, info.getClientId())
                    .isEquals();
        }
        return result;
    }

}
