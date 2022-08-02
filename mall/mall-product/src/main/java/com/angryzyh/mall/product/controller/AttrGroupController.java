package com.angryzyh.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.angryzyh.mall.product.dao.AttrAttrgroupRelationDao;
import com.angryzyh.mall.product.entity.AttrEntity;
import com.angryzyh.mall.product.service.AttrService;
import com.angryzyh.mall.product.service.CategoryService;
import com.angryzyh.mall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.angryzyh.mall.product.entity.AttrGroupEntity;
import com.angryzyh.mall.product.service.AttrGroupService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.R;

/**
 * 属性分组
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
@RestController
//               /product/attrgroup/list/{catelogId}
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrService attrService;


    //  查询属性组 所 关联的 属性
    //  /product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("{attrgroupId}/attr/relation")
    public R getAttrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> attrEntitys = attrService.getAttrRelation(attrgroupId);
        return R.ok().put("data", attrEntitys);
    }

    /**
     * GET
     * /product/attrgroup/{attrgroupId}/noattr/relation
     *  新增 关联 ,需要查询 当前分类下的属性,对应的没有关联属性组的 属性
     * @param attrgroup
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNoRelaionAttr(params,attrgroupId);
        return R.ok().put("page", page);
    }


    /**
     * deleteList
     * POST: /product/attrgroup/attr/relation/delete
     * 删除 关联属性  即 属性po
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteAttrRelation(@RequestBody AttrGroupRelationVo[] vos) {
        attrGroupService.deleteAttrRelation(vos);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
               @PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     * 回显的时候需要携带,分类路径,捆绑在AttrGroupEntity的 数据库不存在的字段上
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        // 提取出分类id
        Long catelogId = attrGroup.getCatelogId();
        // 调用分类的方法查询 递归查询分类的路径
        Long[] path = categoryService.findCatelogPath(catelogId);
        // 最后把path 塞进去 AttrGroupEntity 实体类内,一起传回给前端
        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }
}
