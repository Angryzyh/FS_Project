package com.angryzyh.mall.product.service;

import com.angryzyh.mall.product.vo.AttrGroupRelationVo;
import com.angryzyh.mall.product.vo.AttrGroupWithAttrRespVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    //分页查询 <属性分组>信息, 根据前端传入的三级分类的id来查询 孙子分类所 包含的 属性分组
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    // 删除 AttrGroupRelation   关联关系表中的信息
    void deleteAttrRelation(AttrGroupRelationVo[] vos);

    // 获取分类下所有分组&关联属性
    List<AttrGroupWithAttrRespVo> getAttrAttrGroupByCatelogId(Long catelogId);
}

