package com.angryzyh.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPrice {

    private Long id;     //skuId
    private String name; //会员等级名
    private BigDecimal price;  //sukId 产品对应的 会员价

}