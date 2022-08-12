package com.angryzyh.mall.ware.service;

import com.angryzyh.common.to.SkuHasStockTo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:24:50
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    //查询一个sku在 某个库存中的信息
    PageUtils queryPageByKey(Map<String, Object> params);

    //  插入 或 修改
    void saveOrUpdateOwn(WareSkuEntity wareSkuEntity);

    // 远程调用接口, 商品维护->商品管理->上架 ,用于查询当前sku商品是否有库存
    List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds);
}

