package com.angryzyh.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angryzyh.common.to.SkuHasStockTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angryzyh.mall.ware.entity.WareSkuEntity;
import com.angryzyh.mall.ware.service.WareSkuService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.R;



/**
 * 商品库存
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:24:50
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 远程调用接口, 商品维护->商品管理->上架 ,用于查询当前sku商品是否有库存
     * @param skuIds skuIds
     * @return 每个skuIds 对应的 库存信息
     */
    @PostMapping("/has/stock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockTo> tos = wareSkuService.getSkuHasStock(skuIds);
        return R.ok().put("data",tos);
    }

    /**
     * 列表
     *  查询一个sku在 某个库存中的信息
     *  GET
     * /ware/waresku/list
     */
    @GetMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPageByKey(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }
}
