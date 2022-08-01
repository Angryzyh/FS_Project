package com.angryzyh.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import com.angryzyh.mall.product.vo.AttrRespVo;
import com.angryzyh.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angryzyh.mall.product.entity.AttrEntity;
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
     * 列表 查询
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R listByCatelogId(@RequestParam Map<String, Object> params,
                             @PathVariable("catelogId") Long catelogId,
                             @PathVariable("attrType") String type){
        PageUtils page = attrService.listByCatelogId(params,catelogId);
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
     * @param attrVo
     * @return
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
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));
        return R.ok();
    }

}
