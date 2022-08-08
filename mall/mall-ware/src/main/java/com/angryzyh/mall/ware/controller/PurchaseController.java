package com.angryzyh.mall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.angryzyh.common.constant.WareConstant;
import com.angryzyh.mall.ware.vo.FinishPurchaseListVo;
import com.angryzyh.mall.ware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angryzyh.mall.ware.entity.PurchaseEntity;
import com.angryzyh.mall.ware.service.PurchaseService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.R;

/**
 * 采购信息
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:24:50
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 查询未领取的采购单
     * GET
     * ware/purchase/unreceive/list
     * @param params 无
     * @return page
     */
    @GetMapping("unreceive/list")
    //@RequiresPermissions("ware:purchase:list")
    public R unReceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.unReceiveList(params);
        return R.ok().put("page", page);
    }

    /**
     * POST
     * /ware/purchase/merge
     *  合并采购单
     * @param vo {
     *   purchaseId: 1, //整单id
     *   items:[1,2,3,4] //合并项集合}
     * @return R
     */
    @PostMapping("merge")
    public R getMergeList(@RequestBody MergeVo vo) {
        purchaseService.getMergePurchase(vo);
        return R.ok();
    }

    /**
     *  postman 模拟采购员APP领取采购单
     * /ware/purchase/received
     * @param ids 采购单ids
     * @return R.ok()
     */
    @PostMapping("received")
    public R receivedPurchaseList(@RequestBody List<Long> ids) {
        purchaseService.receivedPurchaseList(ids);
        return R.ok();
    }

    /**
     * postman 模拟采购员APP *完成采购*
     *    POST
     * /ware/purchase/done
     * @param purchaseListVo {
     *    id: 1, //采购单id
     *    items: [{itemId:1,status:3,reason:""},{itemId:3,status:4,reason:"缺货"}]  // 采购需求单完成||失败的需求详情}
     * @return R.ok()
     */
    @PostMapping("done")
    public R finishPurchaseList(@RequestBody FinishPurchaseListVo purchaseListVo) {
        purchaseService.finishPurchaseList(purchaseListVo);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);
        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        purchase.setStatus(WareConstant.PurchaseEnum.CREATED.getCode());
		purchaseService.save(purchase);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
        purchase.setUpdateTime(new Date());
		purchaseService.updateById(purchase);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }
}
