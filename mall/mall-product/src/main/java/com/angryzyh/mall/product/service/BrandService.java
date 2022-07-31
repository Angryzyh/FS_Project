package com.angryzyh.mall.product.service;

import com.angryzyh.mall.product.controller.BrandController;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 在brand表自己更新的时候也要 (AAT AndAllTable) 更新其他冗余的表中品牌名
     * @param brand
     * 业务调用->{@link BrandController#update}
     */
    void updateByIdAndAllTable(BrandEntity brand);
}

