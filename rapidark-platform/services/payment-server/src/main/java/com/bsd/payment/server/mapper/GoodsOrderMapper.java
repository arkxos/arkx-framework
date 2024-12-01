package com.bsd.payment.server.mapper;

import com.bsd.payment.server.model.entity.GoodsOrder;
import com.rapidark.framework.data.mybatis.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsOrderMapper extends SuperMapper<GoodsOrder> {
    int deleteByPrimaryKey(String goodsOrderId);

    int insertSelective(GoodsOrder record);

    GoodsOrder selectByPrimaryKey(String goodsOrderId);

    int updateByPrimaryKeySelective(GoodsOrder record);

    int updateByPrimaryKey(GoodsOrder record);
}