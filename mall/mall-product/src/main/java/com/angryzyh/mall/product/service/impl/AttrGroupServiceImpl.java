package com.angryzyh.mall.product.service.impl;

import com.angryzyh.mall.product.controller.AttrGroupController;
import com.angryzyh.mall.product.dao.AttrAttrgroupRelationDao;
import com.angryzyh.mall.product.dao.AttrDao;
import com.angryzyh.mall.product.entity.AttrAttrgroupRelationEntity;
import com.angryzyh.mall.product.entity.AttrEntity;
import com.angryzyh.mall.product.vo.AttrGroupRelationVo;
import com.angryzyh.mall.product.vo.AttrGroupWithAttrRespVo;
import com.angryzyh.mall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.AttrGroupDao;
import com.angryzyh.mall.product.entity.AttrGroupEntity;
import com.angryzyh.mall.product.service.AttrGroupService;
import org.springframework.transaction.annotation.Transactional;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    AttrDao attrDao;

    /** 分页 关键词匹配 查询
     * @param params 前端发来的 请求参数包含分页,排序,关键词等信息
     * @param catelogId 要查询的分类id 0为 全查询  >0的id 为具体的分类
     * @return 返回 孙子分类所拥有的 属性分组
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params,Long catelogId) {
        /*两种查询情况*/
        // 两种查询都必须是可以 根据关键词检索查询
        LambdaQueryWrapper<AttrGroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        //获取关键词
        String key = (String) params.get("key");
        //SELECT attr_group_id,attr_group_name,sort,descript,icon,catelog_id
        // FROM pms_attr_group
        // WHERE (catelog_id = ? AND (attr_group_id = ? OR attr_group_name LIKE ? OR descript LIKE ?))
        //==> Parameters: 225(Long), 2(String), %2%(String), %2%(String)
        queryWrapper
                .and(StringUtils.isNotEmpty(key),
                        x -> x.eq(AttrGroupEntity::getAttrGroupId, key)
                                .or().like(AttrGroupEntity::getAttrGroupName, key)
                                .or().like(AttrGroupEntity::getDescript, key)
                );
        //1. 当前端传入的catelogId为默认值0时, 代表全查询
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    queryWrapper
            );
            return new PageUtils(page);
        } else {
            queryWrapper.and(x->x.eq(AttrGroupEntity::getCatelogId, catelogId));
            //2. 当传入点击获取的 catelogId 为 具体的三级分类时
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    // 判断catelogId 有具体id值后  再追加 查询具体的分类组信息
                    queryWrapper
            );
            return new PageUtils(page);
        }
    }

    /**
     * deleteList
     * 删除 AttrGroupRelation   关联关系表中的信息
     *
     * @param vos 前端传参 attrId , attrGroupId
     */
    @Override
    public void deleteAttrRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> relationEntitys = Arrays.stream(vos).map(x -> {
            AttrAttrgroupRelationEntity entitys = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(x, entitys);
            return entitys;
        }).collect(Collectors.toList());
        // xml批量删除
        attrAttrgroupRelationDao.deleteSelect(relationEntitys);

    }

    /**
     * 获取分类下所有分组&关联属性
     * @param catelogId 分类id
     * @return List<属性组with属性s>
     * 业务场景:商品维护->发布商品->2.规格参数
     * 业务实现-> {@link AttrGroupController#getAttrAttrGroupByCatelogId}
     */
    @Override
    public List<AttrGroupWithAttrRespVo> getAttrAttrGroupByCatelogId(Long catelogId) {


        return baseMapper.selectList(new LambdaQueryWrapper<AttrGroupEntity>()
                .eq(AttrGroupEntity::getCatelogId, catelogId))
                .stream()
                .map(x -> {
                    AttrGroupWithAttrRespVo vo = new AttrGroupWithAttrRespVo();
                    BeanUtils.copyProperties(x, vo);
                    return vo;
                }).peek(x -> {
                    List<Long> attrIds = attrAttrgroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq(AttrAttrgroupRelationEntity::getAttrGroupId, x.getAttrGroupId()))
                        .stream()
                        .map(AttrAttrgroupRelationEntity::getAttrId)
                        .collect(Collectors.toList());
                        List<AttrVo> childAttrs = attrDao.selectBatchIds(attrIds)
                                .stream()
                                .map(y -> {
                                    AttrVo attrVo = new AttrVo();
                                    BeanUtils.copyProperties(y, attrVo);
                                    return attrVo;
                                }).collect(Collectors.toList());
                    x.setAttrs(childAttrs);
                }).collect(Collectors.toList());
    }
    /*@Override
    public List<AttrGroupWithAttrRespVo> getAttrAttrGroupByCatelogId(Long catelogId) {
        // 根据分类id 查询属性组id
        List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(new LambdaQueryWrapper<AttrGroupEntity>()
                .eq(AttrGroupEntity::getCatelogId, catelogId));
        // 拿到属性组 list
        List<AttrGroupWithAttrRespVo> collect = attrGroupEntities.stream()
                .map(attrGroupEntity -> {
                    AttrGroupWithAttrRespVo vo = new AttrGroupWithAttrRespVo();
                    BeanUtils.copyProperties(attrGroupEntity, vo);
                    return vo;
                })
                .peek(vo -> {
                    //  根据属性组id  查询 到一个属性组id 对应的 多个关联关系组
                    List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrGroupId, vo.getAttrGroupId()));
                    // 获取 属性ids
                    List<Long> attrIds = attrAttrgroupRelationEntities
                            .stream()
                            .map(AttrAttrgroupRelationEntity::getAttrId)
                            .collect(Collectors.toList());
                    // 查询每个 属性id list集合 填入到vo里面
                    List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
                    List<AttrVo> childAttrs = attrEntities.stream()
                            .map(attrEntitie -> {
                                AttrVo attrVo = new AttrVo();
                                BeanUtils.copyProperties(attrEntitie, attrVo);
                                return attrVo;
                            }).collect(Collectors.toList());
                    vo.setAttrVos(childAttrs);
                })
                .collect(Collectors.toList());
        return collect;
    }*/
}