package com.angryzyh.mall.product.vo;

import lombok.Data;

@Data
public class AttrRespVo extends AttrVo {

    // 分类名称
    private String catelogName;

    // 分组名称
    private String groupName;

    // 分类路径 , 修改 回显是用到
    private Long[] catelogPath;
}
