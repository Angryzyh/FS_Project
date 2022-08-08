package com.angryzyh.mall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class FinishPurchaseListVo {
  /*  {
        id: 1,//采购单id
                items: [{itemId:1,status:3,reason:""},
                {itemId:3,status:4,reason:"缺货"}] //采购需求单
    }*/

    // 采购单id
    private Long id;

    // 需求单集合
    private List<FinishPurchaseDetailListVo> items;



}
