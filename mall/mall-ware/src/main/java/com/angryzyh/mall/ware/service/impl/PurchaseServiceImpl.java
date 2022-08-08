package com.angryzyh.mall.ware.service.impl;

import com.angryzyh.common.constant.WareConstant;
import com.angryzyh.common.utils.R;
import com.angryzyh.mall.ware.entity.PurchaseDetailEntity;
import com.angryzyh.mall.ware.entity.WareSkuEntity;
import com.angryzyh.mall.ware.feign.ProductFeignService;
import com.angryzyh.mall.ware.service.PurchaseDetailService;
import com.angryzyh.mall.ware.service.WareSkuService;
import com.angryzyh.mall.ware.vo.FinishPurchaseDetailListVo;
import com.angryzyh.mall.ware.vo.FinishPurchaseListVo;
import com.angryzyh.mall.ware.vo.MergeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.ware.dao.PurchaseDao;
import com.angryzyh.mall.ware.entity.PurchaseEntity;
import com.angryzyh.mall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RelationException;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单
     * @param params 分页参数
     * @return page
     */
    @Override
    public PageUtils unReceiveList(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new LambdaQueryWrapper<PurchaseEntity>().eq(PurchaseEntity::getStatus, "0")
                        .or().eq(PurchaseEntity::getStatus, "1"));
        return new PageUtils(page);
    }

    /**
     * 合并采购订单
     * @param vo {
     *  purchaseId: 1, //整单id
     *  items:[1,2,3,4] //合并项集合}
     */
    @Transactional
    @Override
    public void getMergePurchase(MergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        // 没有 采购分配单,先生成采购订单
        if (purchaseId == 0) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        // 遍历 采购需求 订单    把分配单信息塞到 需求订单内,修改purchase_id 和 status订单状态
        List<Long> items = vo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream()
        .map(detail -> {
            PurchaseDetailEntity detailEntity = purchaseDetailService.getById(detail);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailEnum.ASSIGNED.getCode());
            return detailEntity;
        }).filter(detail -> detail.getStatus() == WareConstant.PurchaseDetailEnum.CREATED.getCode() || detail.getStatus() == WareConstant.PurchaseDetailEnum.ASSIGNED.getCode())
        .collect(Collectors.toList());
        purchaseDetailService.updateBatchById(collect);

        // 更新 采购分配单 的 更新时间
        PurchaseEntity purchaseEntity1 = new PurchaseEntity();
        purchaseEntity1.setId(finalPurchaseId);
        purchaseEntity1.setUpdateTime(new Date());
        this.updateById(purchaseEntity1);
    }

    /**
     * 模拟采购员APP领取采购单
     * @param ids {采购单id}
     */
    @Override
    public void receivedPurchaseList(List<Long> ids) {
        //1. 先判断 采购单的状态status,是否可以被领取
        List<PurchaseEntity> collect = ids.stream()
                .map(this::getById)
                .filter(item -> item.getStatus() == WareConstant.PurchaseEnum.CREATED.getCode() ||
                        item.getStatus() == WareConstant.PurchaseEnum.ASSIGNED.getCode())
                .peek(item->{
                    item.setStatus(WareConstant.PurchaseEnum.RECEIVED.getCode());
                    item.setUpdateTime(new Date());
                })
                .collect(Collectors.toList());
        //2. 之后在改变采购单的 状态
        this.updateBatchById(collect);
        //3. 在改变每个采购单对应的 采购需求单里面的状态更新
        collect.forEach(item->{
            List<PurchaseDetailEntity> detailEntities = purchaseDetailService.selectByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect1 = detailEntities.stream().map(detailEntity -> {
                PurchaseDetailEntity detailEntity1 = new PurchaseDetailEntity();
                detailEntity1.setStatus(WareConstant.PurchaseDetailEnum.PURCHASING.getCode());
                detailEntity1.setId(detailEntity.getId());
                return detailEntity1;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect1);
        });
    }

    /**
     * postman 模拟采购员APP 完成采购
     *
     * @param purchaseListVo 采购单-采购需求单 完成情况
     */
    @Override
    @Transactional
    public void finishPurchaseList(FinishPurchaseListVo purchaseListVo) {
        boolean flag = true;
        // 2. 拿到 所有采购需求单,修改状态
        List<FinishPurchaseDetailListVo> items = purchaseListVo.getItems();
        List<PurchaseDetailEntity> purchaseDetailEntityList = new ArrayList<>();
        for (FinishPurchaseDetailListVo item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(item.getItemId());
            if (item.getStatus() == WareConstant.PurchaseDetailEnum.ERROR.getCode()) {
                detailEntity.setStatus(WareConstant.PurchaseDetailEnum.ERROR.getCode());
                flag = false;
            } else {
                detailEntity.setStatus(WareConstant.PurchaseDetailEnum.FINISH.getCode());
                // 3. 产品入库, 先根据需求单id, 查询出 wareId 和 skuId
                PurchaseDetailEntity detail = purchaseDetailService.getById(item.getItemId());
                WareSkuEntity wareSkuEntity = new WareSkuEntity();
                // 远程调用mall-product 查询skuName
                // try/catch 远程调用失败事务无需回滚
                try {
                    R r = productFeignService.skuInfo(detail.getSkuId());
                    if (r.getCode() == 0) {
                        Map<String, Object> skuInfo = (Map<String, Object>) r.get("skuInfo");
                        String skuName = (String) skuInfo.get("skuName");
                        wareSkuEntity.setSkuName(skuName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wareSkuEntity.setSkuId(detail.getSkuId());
                wareSkuEntity.setWareId(detail.getWareId());
                wareSkuEntity.setStock(detail.getSkuNum());
                wareSkuEntity.setStockLocked(0);
                wareSkuService.saveOrUpdateOwn(wareSkuEntity);
            }
            purchaseDetailEntityList.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);
        // 1. 拿到 采购单id 更采购单 状态 和 时间
        Long purchaseId = purchaseListVo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        // 1.1 如果 采购需求没有全部完成, 需要 采购单 显示采购异常
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseEnum.FINISH.getCode() : WareConstant.PurchaseEnum.ERROR.getCode());
        this.updateById(purchaseEntity);
    }
}