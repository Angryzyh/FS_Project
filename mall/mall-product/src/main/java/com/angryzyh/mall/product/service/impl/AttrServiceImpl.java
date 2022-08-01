package com.angryzyh.mall.product.service.impl;

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
                new QueryWrapper<AttrEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * 新增属性信息
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
        // 再保存 attr&attrGroup中间po到数据库
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
        attrGroupRelationDao.insert(attrAttrgroupRelationEntity);
    }

    /**
     * @param params    获取前端传值
     * @param catelogId 分类id
     * @return 查询结果  属性&属性组信息 封装进attrRespVo
     */
    @Override
    public PageUtils listByCatelogId(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrEntity> queryWrapper = new LambdaQueryWrapper<>();
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
            // 查询 属性组 信息
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrGroupRelationDao.selectOne(
                    new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));
            // 根据属性id查找  属性&属性组关联表 可能为空
            if (attrAttrgroupRelationEntity != null) {
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
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
     * 查询 单个属性信息,用于修改回显
     *
     * @param attrId 属性id
     * @return 包含分类路径, 属性组id 的 attrRespVo
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        // 查询属性信息
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);
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