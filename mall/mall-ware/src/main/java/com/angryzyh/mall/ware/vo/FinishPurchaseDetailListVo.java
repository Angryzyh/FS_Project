package com.angryzyh.mall.ware.vo;

import lombok.Data;

@Data
public class FinishPurchaseDetailListVo {
//    items: [{itemId:1,status:3,reason:""},
//    {itemId:3,status:4,reason:"缺货"}] //采购需求单


    // 采购需求单id
    private Long itemId;

    // 采购需求单状态
    private Integer status;

    // 原因
    private String reason;

}
