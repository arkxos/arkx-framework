package io.arkx.framework.avatarmq.consumer;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * @filename:ClustersRelation.java
 * @description:ClustersRelation功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class ClustersRelation {

    private String id;

    private ConsumerClusters clusters;

    ClustersRelation() {

    }

    ClustersRelation(String id, ConsumerClusters clusters) {
        this.clusters = clusters;
        this.id = id;
    }

    public ConsumerClusters getClusters() {
        return clusters;
    }

    public void setClusters(ConsumerClusters clusters) {
        this.clusters = clusters;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && ClustersRelation.class.isAssignableFrom(obj.getClass())) {
            ClustersRelation clusters = (ClustersRelation) obj;
            result = new EqualsBuilder().append(id, clusters.getId()).isEquals();
        }
        return result;
    }

}
