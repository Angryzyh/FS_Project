package com.angryzyh.mall.coupon.service;

import com.angryzyh.common.to.SpuReductionTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:23:09
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //mall-product模块商品新增远程调用
    void saveAllCouponFromSpu(SpuReductionTo spuReductionTo);
}

