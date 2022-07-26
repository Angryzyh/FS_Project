package com.angryzyh.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.angryzyh.mall.product.entity.BrandEntity;
import com.angryzyh.mall.product.vo.BrandRespVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angryzyh.mall.product.entity.CategoryBrandRelationEntity;
import com.angryzyh.mall.product.service.CategoryBrandRelationService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.R;

/**
 * 品牌分类关联
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
@RestController
//            /product/categorybrandrelation/catelog/list
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * selectAll
     * 查询分类关联的品牌名称
     * GET
     * /product/categorybrandrelation/brands/list
     * @return
     */
    @GetMapping("brands/list")
    public R relationBrandsList(@RequestParam("catId") Long catId) {
        List<BrandRespVo> brandEntities = categoryBrandRelationService.getBrandByCatId(catId);
        return R.ok().put("data",brandEntities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 获取品牌关联的分类
     * 根据id
     */
    //
    @GetMapping("/catelog/list")
    //@RequiresPermissions("product:categorybrandrelation:list")
    public R catelogList(@RequestParam Long brandId){
        List<CategoryBrandRelationEntity> categoryBrandRelationEntityList = categoryBrandRelationService.getCatelogListByBrand(brandId);
        return R.ok().put("data", categoryBrandRelationEntityList);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);
        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @PostMapping ("/save")
    //@RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveCategoryBrandRelation(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }
}
