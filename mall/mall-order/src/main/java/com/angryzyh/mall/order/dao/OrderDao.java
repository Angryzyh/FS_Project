package com.angryzyh.mall.order.dao;

import com.angryzyh.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-21 22:23:31
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
