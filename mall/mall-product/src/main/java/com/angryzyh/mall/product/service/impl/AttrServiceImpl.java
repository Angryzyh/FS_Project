package com.angryzyh.mall.product.service.impl;

import com.angryzyh.mall.product.dao.AttrAttrgroupRelationDao;
import com.angryzyh.mall.product.entity.AttrAttrgroupRelationEntity;
import com.angryzyh.mall.product.vo.AttrVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
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
}