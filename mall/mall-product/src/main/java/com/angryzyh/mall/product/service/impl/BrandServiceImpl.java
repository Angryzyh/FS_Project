package com.angryzyh.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.BrandDao;
import com.angryzyh.mall.product.entity.BrandEntity;
import com.angryzyh.mall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    /**
     * 品牌的 分页 关键词匹配 查询
     *
     * @param params 前端传来的接口参数
     * @return 查询结果
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 获取检索条件
        String key = (String) params.get("key");
        LambdaQueryWrapper<BrandEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(StringUtils.isNotBlank(key), x -> {
            x.eq(BrandEntity::getBrandId, key)
                    .or().like(BrandEntity::getName, key)
                    .or().like(BrandEntity::getDescript, key)
                    .or().eq(BrandEntity::getFirstLetter, key);
        });
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }
}