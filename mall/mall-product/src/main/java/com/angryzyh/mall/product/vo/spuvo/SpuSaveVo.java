package com.angryzyh.mall.product.vo.spuvo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveVo {

    //1. pms_spu_info
    private String spuName; //商品名称
    private String spuDescription; //商品描述
    private Long catalogId; // 所属分类id
    private Long brandId; // 品牌id
    private BigDecimal weight; //商品重量
    private int publishStatus; // 0下架 1上架

    //2. pms_spu_info_desc
    private List<String> decript; //商品介绍 海报图
    // 3. pms_spu_images
    private List<String> images; //images对象
    // 6. mall_sms->sms_spu_bounds
    private Bounds bounds;
    // 4. pms_product_attr_value
    private List<BaseAttrs> baseAttrs; //规格参数对象
    // 5. pms_sku_info
    private List<Skus> skus;  // sku对象

}