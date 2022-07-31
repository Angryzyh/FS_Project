package com.angryzyh.mall.product.service.impl;

import com.angryzyh.mall.product.dao.BrandDao;
import com.angryzyh.mall.product.dao.CategoryDao;
import com.angryzyh.mall.product.entity.BrandEntity;
import com.angryzyh.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.CategoryBrandRelationDao;
import com.angryzyh.mall.product.entity.CategoryBrandRelationEntity;
import com.angryzyh.mall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询根据品牌 查询拥有的分类信息
     * @param brandId 品牌id
     * @return 品牌所拥有的 分类信息
     */
    @Override
    public List<CategoryBrandRelationEntity> getCatelogListByBrand(Long brandId) {
        return baseMapper.selectList(new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                .eq(brandId != null, CategoryBrandRelationEntity::getBrandId, brandId)
        );
    }

    /**
     * 保存 品牌下 新增的分类名
     * @param categoryBrandRelation 品牌id 和 分类id
     */
    @Override
    public void saveCategoryBrandRelation(CategoryBrandRelationEntity categoryBrandRelation) {
        BrandEntity brandEntity = brandDao.selectById(categoryBrandRelation.getBrandId());
        CategoryEntity categoryEntity = categoryDao.selectById(categoryBrandRelation.getCatelogId());
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        baseMapper.insert(categoryBrandRelation);
    }

    /**
     * ∵brand(品牌)表有可能更新brand_name(品牌名称)
     * ∴同步更新category_brand_relation(分类-品牌关系)表中的brand_name(品牌名称)
     * @param brandId 根据 [品牌id]
     * @param name    更新 [品牌名称]
     * 业务关联-->{@link BrandServiceImpl#updateByIdAndAllTable}
     */
    @Override
    public void updateBrandById(Long brandId, String name) {
        this.update(new LambdaUpdateWrapper<CategoryBrandRelationEntity>().
                eq(CategoryBrandRelationEntity::getBrandId, brandId)
                .set(CategoryBrandRelationEntity::getBrandName, name));
    }

    /**
     * ∵category(分类)表有可能更新name(分类名称)
     * ∴同步更新category_brand_relation(分类-品牌关系)表中的name(分类名称)
     * @param catId 根据 [分类id]
     * @param name  更新 [分类名称]
     * 业务关联-->{@link CategoryServiceImpl#updateByIdAndAllTable}
     */
    @Override
    public void updateCategoryById(Long catId, String name) {
        baseMapper.updateCategoryById(catId, name);
    }
}