package com.angryzyh.mall.product.dao;

import com.angryzyh.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
