package com.angryzyh.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.product.entity.ProductAttrValueEntity;

import java.util.Map;

/**
 * spu属性值
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

