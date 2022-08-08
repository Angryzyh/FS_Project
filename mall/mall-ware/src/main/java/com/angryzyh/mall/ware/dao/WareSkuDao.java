package com.angryzyh.mall.ware.dao;

import com.angryzyh.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:24:50
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {


    void saveOrUpdateOwn(@Param("wareSkuEntity") WareSkuEntity wareSkuEntity);
}
