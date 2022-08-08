package com.angryzyh.mall.ware.service.impl;

import com.angryzyh.mall.ware.service.WareInfoService;
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

import com.angryzyh.mall.ware.dao.WareSkuDao;
import com.angryzyh.mall.ware.entity.WareSkuEntity;
import com.angryzyh.mall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareInfoService wareInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     * 查询一个sku在 某个库存中的信息
     *
     * @param params {
     *  wareId: 123,//仓库id
     *  skuId: 123//商品id}
     * @return page
     */
    @Override
    public PageUtils queryPageByKey(Map<String, Object> params) {
        LambdaQueryWrapper<WareSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        String wareId = (String) params.get("wareId");
        String skuId = (String) params.get("skuId");
        if (!skuId.isEmpty()) {
            queryWrapper.eq(WareSkuEntity::getSkuId, skuId);
        }
        if (!wareId.isEmpty()) {
            queryWrapper.eq(WareSkuEntity::getWareId,wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public void saveOrUpdateOwn(WareSkuEntity wareSkuEntity) {
        List<WareSkuEntity> list = this.list(new LambdaQueryWrapper<WareSkuEntity>()
                .eq(WareSkuEntity::getSkuId, wareSkuEntity.getSkuId())
                .eq(WareSkuEntity::getWareId, wareSkuEntity.getWareId()));
        if (list == null || list.size() == 0) {
            // 没有就插入
            this.save(wareSkuEntity);
        } else {
            //有就修改
            this.baseMapper.saveOrUpdateOwn(wareSkuEntity);
        }
    }
}