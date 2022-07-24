package com.angryzyh.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-21 22:23:31
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

