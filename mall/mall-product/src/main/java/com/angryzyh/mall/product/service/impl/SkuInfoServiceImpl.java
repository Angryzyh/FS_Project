package com.angryzyh.mall.product.service.impl;

import com.angryzyh.mall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.SkuInfoDao;
import com.angryzyh.mall.product.entity.SkuInfoEntity;
import com.angryzyh.mall.product.service.SkuInfoService;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     * sku检索 模糊 条件匹配 分页查询
     *
     * @param params{ page: 1,//当前页码
     *                limit: 10,//每页记录数
     *                sidx: 'id',//排序字段
     *                order: 'asc/desc',//排序方式
     *                <p>
     *                key: '华为',//检索关键字
     *                catelogId: 0,
     *                brandId: 0,
     *                min: 0,
     *                max: 0
     *                }
     * @return page
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        String key = (String) params.get("key");
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(x -> {
                x.eq(SkuInfoEntity::getSkuId, key)
                .or().like(SkuInfoEntity::getSkuName, key);
            });
        }
        if (StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq(SkuInfoEntity::getCatalogId, catelogId);
        }
        if (StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq(SkuInfoEntity::getBrandId, brandId);
        }
        if (StringUtils.isNotBlank(min)) {
            queryWrapper.gt(SkuInfoEntity::getPrice, min);
        }
        if (StringUtils.isNotBlank(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(new BigDecimal(0)) > 0) {
                    queryWrapper.lt(SkuInfoEntity::getPrice, max);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }
}