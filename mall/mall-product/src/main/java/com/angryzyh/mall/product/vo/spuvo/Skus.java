package com.angryzyh.mall.product.vo.spuvo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Skus {

    //5.3 pms_sku_sale_attr_value
    private List<Attr> attr; //销售属性 对象

    //当前 5.1 pms_sku_info
    private String skuName; //sku名称
    private BigDecimal price; // 价格
    private String skuTitle; // sku标题
    private String skuSubtitle; // sku副标题

    // 5.2 pms_sku_images
    private List<Images> images;

    private List<String> descar;

    // 5.3 mall_sms->sms_sku_ladder  满几件打折表
    private Integer fullCount;
    private BigDecimal discount;
    private BigDecimal countStatus;
    // 5.4 mall_sms->sms_sku_full_reduction 满减表
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer priceStatus;
    //  5.5 mall_sms->sms_member_price  会员价表
    private List<MemberPrice> memberPrice;

}