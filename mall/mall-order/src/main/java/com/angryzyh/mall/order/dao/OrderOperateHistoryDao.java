package com.angryzyh.mall.order.dao;

import com.angryzyh.mall.order.entity.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-21 22:23:31
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
