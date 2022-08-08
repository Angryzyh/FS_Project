package com.angryzyh.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 模块:库存服务(mall-ware) 常量
public class WareConstant {
    //子模块:采购单维护->采购需求

    @Getter
    @AllArgsConstructor
    // 采购单
    public enum PurchaseEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVED(2, "已领取"),
        FINISH(3, "已完成"),
        ERROR(4, "有异常");
        private final int code;
        private final String msg;
    }

    @Getter
    @AllArgsConstructor
    // 采购需求单
    public enum PurchaseDetailEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        PURCHASING(2, "正在采购"),
        FINISH(3, "已完成"),
        ERROR(4, "采购失败");
        private final int code;
        private final String msg;
    }
}