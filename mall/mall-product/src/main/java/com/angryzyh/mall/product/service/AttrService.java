package com.angryzyh.mall.product.service;

import com.angryzyh.mall.product.vo.AttrRespVo;
import com.angryzyh.mall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.mall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author angryzyh
 * @email 1792090548@qq.com
 * @date 2022-07-22 20:21:43
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    // 新增属性
    void saveAttrVo(AttrVo attrVo);

    // 查询分类规格参数   根据分类id查询 属性信息
    PageUtils listByCatelogId(Map<String, Object> params, Long catelogId, String type);

    // 查询 单个属性信息,用于修改回显
    AttrRespVo getAttrInfo(Long attrId);

    // 修改 属性信息
    void updateAttrVo(AttrVo attrVo);

    // 为 属性组 查询所关联的 属性id ,name,valueSelect
    List<AttrEntity> getAttrRelation(Long attrgroupId);

    // 先查询 当前属性组 一同的 分类 下的属性表中没有被关联的属性
    PageUtils getNoRelaionAttr(Map<String, Object> params, Long attrgroupId);

    // 查找出可以被检索的attrIds
    List<Long> listBySearchType(List<Long> attrIds);
}

