package com.angryzyh.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.angryzyh.common.utils.PageUtils;
import com.angryzyh.common.utils.Query;

import com.angryzyh.mall.product.dao.AttrGroupDao;
import com.angryzyh.mall.product.entity.AttrGroupEntity;
import com.angryzyh.mall.product.service.AttrGroupService;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

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
                .and(StringUtils.isNotBlank("key"),
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
        //2. 当传入点击获取的 catelogId 为 具体的三级分类时
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    // 判断catelogId 有具体id值后  再追加 查询具体的分类组信息
                    queryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId)
            );
            return new PageUtils(page);
        }
    }
}