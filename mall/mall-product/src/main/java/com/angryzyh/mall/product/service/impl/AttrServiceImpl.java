package com.angryzyh.mall.product.service.impl;

import com.angryzyh.common.constant.ProductConstant;
import com.angryzyh.mall.product.controller.AttrGroupController;
import com.angryzyh.mall.product.dao.AttrAttrgroupRelationDao;
import com.angryzyh.mall.product.dao.AttrGroupDao;
import com.angryzyh.mall.product.dao.CategoryDao;
import com.angryzyh.mall.product.entity.AttrAttrgroupRelationEntity;
import com.angryzyh.mall.product.entity.AttrGroupEntity;
import com.angryzyh.mall.product.entity.CategoryEntity;
import com.angryzyh.mall.product.service.CategoryService;
import com.angryzyh.mall.product.vo.AttrRespVo;
import com.angryzyh.mall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.AttrDao;
import com.angryzyh.mall.product.entity.AttrEntity;
import com.angryzyh.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao attrGroupRelationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    /**
     *  insert新增属性信息
     * ∵前端传值包含attrPo和attrGroupPo
     * ∴采用 attrVo接收传值
     * (●'◡'●) BeanUtils.copyProperties(源VO,目标PO)
     *
     * @param attrVo 前端传值封装进该Vo
     */
    @Override
    @Transactional
    public void saveAttrVo(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        // 使用spring提供的BeanUtils工具 把vo属性copy到po上
        BeanUtils.copyProperties(attrVo, attrEntity);
        // 保存attrPo到数据库
        this.save(attrEntity);
        // 判断要存储的属性  他的AttrType 0为销售属性sale(sku) ,1为规格参数base(spu)
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_ENUM_BASE.getCode()&& attrVo.getAttrGroupId()!=null) {
            // 再保存 attr&attrGroup中间po到数据库
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrGroupRelationDao.insert(attrAttrgroupRelationEntity);
        }


    }

    /**
     * selectAll
     * @param params    获取前端传值
     * @param catelogId 分类id
     * @return 查询结果  属性&属性组信息 封装进attrRespVo
     */
    @Override
    public PageUtils listByCatelogId(Map<String, Object> params, Long catelogId, String type) {
        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        // type 为"base"基本规格参数spu  "sale"销售属性 sku
        // 判断如果 属性类型[0-销售属性，1-基本属性]
        queryWrapper.eq(AttrEntity::getAttrType,
                type.equalsIgnoreCase("base") ?
                        ProductConstant.AttrEnum.ATTR_ENUM_BASE.getCode() : ProductConstant.AttrEnum.ATTR_ENUM_SALES.getCode());
        // 判断如果 catelogId=0 则为全查询, 如果catelogId不等于0 则根据id查询
        if (catelogId != 0) {
            queryWrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        queryWrapper.and(StringUtils.isNotBlank(key),
                x -> x.eq(AttrEntity::getAttrId, key)
                        .or().like(AttrEntity::getAttrName, key));
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        // 缺少 分类名 以及 属性组名
        // 需要根据catelogId 查询 分类信息表 和 属性组表
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> collect = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // 如果是 spu 规格参数才 拼接 属性组信息
            if (type.equalsIgnoreCase("base")) {
                // 查询 属性组 信息
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrGroupRelationDao.selectOne(
                        new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
                // 根据属性id查找  属性&属性组关联表 可能为空
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId()!=null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            // 查询 分类信息
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            attrRespVo.setCatelogName(categoryEntity.getName());
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(collect);
        return pageUtils;
    }

    /**
     * selectOne
     * 查询 单个属性信息,用于修改回显
     * @param attrId 属性id
     * @return 包含分类路径, 属性组id 的 attrRespVo
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        // 查询属性信息
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        // 判断 属性类型[0-销售属性，1-基本属性]
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_ENUM_BASE.getCode()) {
            // 查询 属性关系表:属性组id -> 查询 属性组表:属性组name
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrGroupRelationDao.selectOne(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(attrId != null, AttrAttrgroupRelationEntity::getAttrId, attrId));
            if (attrAttrgroupRelationEntity != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                if (attrGroupEntity!=null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }
        // 查询分类路径
        // 调用之前写好的 分类业务 写过的找到分类路径方法, 获得分类路径
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity!= null) {
            attrRespVo.setCatelogPath(catelogPath);
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }

    /**
     * update
     * 修改属性信息
     * @param attrVo 前端传参
     */
    @Transactional
    @Override
    public void updateAttrVo(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        // 先修改 attrPo
        this.updateById(attrEntity);
        // 判断 属性类型[0-销售属性，1-基本属性]
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_ENUM_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            // 特殊情况, 如果没有属性组 就不是修改了需要 添加
            // 先判断 attrId对应有没有属性组,没有就添加
            Long aLong = attrGroupRelationDao.selectCount(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq(attrEntity.getAttrId() != null, AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
            if (aLong > 0) {
                // 再修改 attrVo-attrPo ,如所属分组, 修改属性&属性组关系维护表
                attrGroupRelationDao.update(attrAttrgroupRelationEntity, new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq(attrEntity.getAttrId() != null, AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
            }else {
                attrGroupRelationDao.insert(attrAttrgroupRelationEntity);
            }
        }
    }

    /**
     * 为 属性组 查询所关联的 属性id ,name,valueSelect
     * @param attrgroupId 属性组id
     * @return 属性po
     * 业务调用--> {@link AttrGroupController#getAttrRelation}
     */
    @Override
    public List<AttrEntity> getAttrRelation(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrGroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .eq(attrgroupId != null, AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId)
        );
        // 这样写很傻逼,要一次一次查询数据库
        /*List<AttrEntity> collect = attrAttrgroupRelationEntities.stream()
                .map(x -> {
                    return baseMapper.selectById(x.getAttrId());
                })
                .filter(x -> {
                    return x.getAttrType() == ProductConstant.AttrEnum.ATTR_ENUM_BASE.getCode();
                })
                .collect(Collectors.toList());*/
        // 这样写 还像个人
        List<Long> longs = attrAttrgroupRelationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        List<AttrEntity> attrEntities = null;
        if (!longs.isEmpty()) {
            attrEntities = baseMapper.selectList(new LambdaQueryWrapper<AttrEntity>()
                    .in(AttrEntity::getAttrId, longs)
                    .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_ENUM_BASE.getCode()));
        }
        return attrEntities;
    }

    /**
     * 先查询 当前属性组 一同的 分类 下的属性表中没有被关联的属性
     * @param params      前端分页参数
     * @param attrgroupId 属性组id
     * @return
     */
    @Override
    public PageUtils getNoRelaionAttr(Map<String, Object> params, Long attrgroupId) {
        //1. 先查询 属性组 对应的分类id ---> 查询到 分类id
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2. 当前分组 只能关联别的分组没有引用的属性
        // 2.1) 当前分类下的其它分组 包括自己
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new LambdaQueryWrapper<AttrGroupEntity>()
                .eq(AttrGroupEntity::getCatelogId, catelogId));
        // 收集属性组id
        List<Long> attrGroupIds = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        // 2.2) 这些分组关联的属性
        List<AttrAttrgroupRelationEntity> attrGroupRelationEntities = attrGroupRelationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                .in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));
        // 收集 属性组 下的 所有已用的属性id
        List<Long> usedAttrIds = attrGroupRelationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttrEntity::getCatelogId, catelogId)
                .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_ENUM_BASE.getCode());
        if (!usedAttrIds.isEmpty()) {
            // 2.3)从当前分类的所有属性中移除这些属性
            queryWrapper.notIn(AttrEntity::getAttrId, usedAttrIds);
        }
        String key = (String) params.get("key");
        if (!key.isEmpty()) {
            queryWrapper
                    .and(x->x.eq(AttrEntity::getAttrId, key)
                            .or().like(AttrEntity::getAttrName, key));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 查找出可以被检索的attrIds
     * @param attrIds
     * @return
     */
    @Override
    public List<Long> listBySearchType(List<Long> attrIds) {
        return baseMapper.listBySearchType(attrIds);
    }
}