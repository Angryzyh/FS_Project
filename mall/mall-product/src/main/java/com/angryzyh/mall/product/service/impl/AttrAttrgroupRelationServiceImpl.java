package com.angryzyh.mall.product.service.impl;

import com.angryzyh.mall.product.vo.AttrGroupRelationVo;
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

import com.angryzyh.mall.product.dao.AttrAttrgroupRelationDao;
import com.angryzyh.mall.product.entity.AttrAttrgroupRelationEntity;
import com.angryzyh.mall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * insert
     * 批量保存要添加的 属性&属性组 关联关系
     *
     * @param vos 前端传值
     */
    @Override
    public void saveRelation(List<AttrGroupRelationVo> vos) {
        List<AttrAttrgroupRelationEntity> collect = vos.stream()
                .map(vo -> {
                    AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(vo, attrAttrgroupRelationEntity);
                    return attrAttrgroupRelationEntity;
                })
                .collect(Collectors.toList());
        this.saveBatch(collect);
    }
}