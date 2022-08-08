package com.angryzyh.mall.ware.service;

import com.angryzyh.mall.ware.vo.FinishPurchaseListVo;
import com.angryzyh.mall.ware.vo.MergeVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.ware.entity.PurchaseEntity;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:24:50
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    // 查询未领取的采购单
    PageUtils unReceiveList(Map<String, Object> params);

    // 合并采购订单
    void getMergePurchase(MergeVo vo);

    //模拟采购员APP领取采购单
    void receivedPurchaseList(List<Long> ids);

    //postman 模拟采购员APP 完成采购
    void finishPurchaseList(FinishPurchaseListVo purchaseListVo);
}

