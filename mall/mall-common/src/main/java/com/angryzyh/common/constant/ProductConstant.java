package com.angryzyh.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 模块:商品服务(mall-product) 常量
public class ProductConstant {
    //子模块:平台属性
    @Getter
    @AllArgsConstructor
    public enum AttrEnum {
        ATTR_ENUM_SALES(0, "销售属性"),
        ATTR_ENUM_BASE(1, "基本属性");
        private final int code;
        private final String msg;
    }
}