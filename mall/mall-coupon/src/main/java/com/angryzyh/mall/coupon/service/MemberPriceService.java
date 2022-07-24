package com.angryzyh.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:23:09
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

