package com.angryzyh.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.order.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-21 22:23:31
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

