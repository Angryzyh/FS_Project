package com.angryzyh.mall.product.service;

import com.angryzyh.mall.product.entity.BrandEntity;
import com.angryzyh.mall.product.vo.BrandRespVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    // 查询根据品牌 查询拥有的分类信息
    List<CategoryBrandRelationEntity> getCatelogListByBrand(Long brandId);

    // 保存 新增的品牌 拥有的分类
    void saveCategoryBrandRelation(CategoryBrandRelationEntity categoryBrandRelation);

    // 同步更新表中的 品牌名称
    void updateBrandById(Long brandId, String name);

    // 同步更新表中的 分类名称
    void updateCategoryById(Long catId, String name);

    // 关联分类 查询到当前分类下的品牌名
    List<BrandRespVo> getBrandByCatId(Long catId);
}

