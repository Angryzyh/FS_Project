package com.angryzyh.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuReductionTo {
    private Long skuId;
    //打折表
    private Integer fullCount;
    private BigDecimal discount;
    private Integer countStatus;
    //满减表
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer priceStatus;
    // 会员价表
    private List<MemberPrice> memberPrice;

}
