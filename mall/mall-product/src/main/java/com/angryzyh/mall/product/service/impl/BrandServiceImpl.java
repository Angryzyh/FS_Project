package com.angryzyh.mall.product.service.impl;

import com.angryzyh.mall.product.dao.CategoryBrandRelationDao;
import com.angryzyh.mall.product.service.CategoryBrandRelationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.BrandDao;
import com.angryzyh.mall.product.entity.BrandEntity;
import com.angryzyh.mall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;

@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 品牌的 分页 关键词匹配 查询
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

    /**
     * ∵brand(品牌)表有可能更新brand_name(品牌名称)
     * ∴同步更新其他表中的brand_name(品牌名称)
     * @param brand 根据 [品牌pojo] 中的id更新其他属性
     */
    @Transactional
    @Override
    public void updateByIdAndAllTable(BrandEntity brand) {
        this.updateById(brand);
        if(StringUtils.isNotBlank(brand.getName())){
            // 同步更新category_brand_relation(分类-品牌关系)表中的brand_name(品牌名称)
            categoryBrandRelationService.updateBrandById(brand.getBrandId(), brand.getName());
            // TODO 更新其他关联brand(品牌)表,当该表更新时,其他表也应当更新
        }
    }
}