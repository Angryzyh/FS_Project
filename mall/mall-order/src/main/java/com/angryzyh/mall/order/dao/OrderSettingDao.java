package com.angryzyh.mall.order.dao;

import com.angryzyh.mall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-21 22:23:31
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
