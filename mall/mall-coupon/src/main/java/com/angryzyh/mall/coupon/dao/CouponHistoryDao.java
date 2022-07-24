package com.angryzyh.mall.coupon.dao;

import com.angryzyh.mall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:23:09
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
