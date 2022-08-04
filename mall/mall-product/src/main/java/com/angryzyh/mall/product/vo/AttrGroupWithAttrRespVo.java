package com.angryzyh.mall.product.vo;

import com.angryzyh.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrRespVo extends AttrGroupEntity {

    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    // 当前分组下的  attr
    private List<AttrVo> attrs;

}
