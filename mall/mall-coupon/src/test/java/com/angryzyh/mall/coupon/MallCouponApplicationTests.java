package com.angryzyh.mall.coupon;

import com.angryzyh.mall.coupon.entity.CouponEntity;
import com.angryzyh.mall.coupon.service.CouponService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallCouponApplicationTests {

    @Autowired
    CouponService couponService;

    @Test
    void contextLoads() {
     /*   CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("憨憨平优惠劵");
        boolean save = couponService.save(couponEntity);
        System.out.println("save = " + save);*/
    }

}
