package com.angryzyh.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.ProductAttrValueDao;
import com.angryzyh.mall.product.entity.ProductAttrValueEntity;
import com.angryzyh.mall.product.service.ProductAttrValueService;
import org.springframework.transaction.annotation.Transactional;

@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> getAttrForSpu(Long spuId) {
        return this.list(new LambdaQueryWrapper<ProductAttrValueEntity>()
                .eq(ProductAttrValueEntity::getSpuId, spuId));
    }

    /**
     * 修改商品规格
     * @param spuId spuId
     * @param productAttrValueEntityList [{
     *   "attrId": 7,
     *   "attrName": "入网型号",
     *   "attrValue": "LIO-AL00",
     *   "quickShow": 1 }]
     */
    @Transactional
    @Override
    public void updateAttrBySpuId(Long spuId, List<ProductAttrValueEntity> productAttrValueEntityList) {
        this.remove(new LambdaQueryWrapper<ProductAttrValueEntity>()
                .eq(spuId != null, ProductAttrValueEntity::getSpuId, spuId));
        List<ProductAttrValueEntity> collect = productAttrValueEntityList.stream()
            .peek(item -> item.setSpuId(spuId))
            .collect(Collectors.toList());
        this.saveBatch(collect);
    }
}