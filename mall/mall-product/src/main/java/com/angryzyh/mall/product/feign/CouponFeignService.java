package com.angryzyh.mall.product.feign;

import com.angryzyh.common.to.SpuBoundsTo;
import com.angryzyh.common.to.SpuReductionTo;
import com.angryzyh.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R save(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/save/spu")
    R saveAllCouponFromSpu(@RequestBody SpuReductionTo spuReductionTo);
}
