package com.angryzyh.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:24:50
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

