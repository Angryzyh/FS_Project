package com.angryzyh.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angryzyh.mall.product.entity.ProductAttrValueEntity;
import com.angryzyh.mall.product.service.ProductAttrValueService;
import com.angryzyh.mall.product.vo.AttrRespVo;
import com.angryzyh.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angryzyh.mall.product.service.AttrService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.R;

/**
 * 商品属性
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService ProductAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     *  获取spu规格参数ProductAttrValueEntity
     * GET /product/attr/base/listforspu/{spuId}
     * @param spuId spuId
     * @return list<Attr>
     */
    @GetMapping("base/listforspu/{spuId}")
    public R getAttrForSpu(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntities = ProductAttrValueService.getAttrForSpu(spuId);
        return R.ok().put("data", productAttrValueEntities);
    }

    /**
     * 列表 查询 spu sku公用
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R listByCatelogId(@RequestParam Map<String, Object> params,
                             @PathVariable("catelogId") Long catelogId,
                             @PathVariable("attrType") String type){
        PageUtils page = attrService.listByCatelogId(params,catelogId,type);
        return R.ok().put("page", page);
    }

    /**
     *  属性信息 回显 单个attrRespVo信息,包含分类路径,属性组id
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		//AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 新增属性 采用vo多收集 属性分组po 的id字段
     * @param attrVo attrVo
     * @return R.ok
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attrVo){
		attrService.saveAttrVo(attrVo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attrVo){
		attrService.updateAttrVo(attrVo);
        return R.ok();
    }

    /**
     * 修改商品规格
     *  先全删除,再全添加
     * POST  /product/attr/update/{spuId}
     * @param spuId  spuId  [{
     * 	"attrId": 7,
     * 	"attrName": "入网型号",
     * 	"attrValue": "LIO-AL00",
     * 	"quickShow": 1 }]
     * @return R.ok()
     */
   @PostMapping("/update/{spuId}")
   public R updateBySpuId(@PathVariable Long spuId, @RequestBody List<ProductAttrValueEntity> productAttrValueEntityList){
       ProductAttrValueService.updateAttrBySpuId(spuId,productAttrValueEntityList);
       return R.ok();
   }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));
        return R.ok();
    }

}
