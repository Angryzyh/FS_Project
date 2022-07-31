package com.angryzyh.mall.product.dao;

import com.angryzyh.mall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    // 同步更新 category_brand_relation(分类-品牌关系)表中的catelog_name(分类名称)
    void updateCategoryById(@Param("catId") Long catId, @Param("name") String name);
}
