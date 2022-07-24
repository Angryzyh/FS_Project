package com.angryzyh.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.coupon.entity.CouponSpuCategoryRelationEntity;

import java.util.Map;

/**
 * 优惠券分类关联
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:23:09
 */
public interface CouponSpuCategoryRelationService extends IService<CouponSpuCategoryRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

