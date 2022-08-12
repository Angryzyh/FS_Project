package com.angryzyh.mall.product.dao;

import com.angryzyh.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> listBySearchType(@Param("attrIds") List<Long> attrIds);
}
