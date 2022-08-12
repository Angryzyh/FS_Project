package com.angryzyh.common.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/*
"skuId": {
    "type": "long"
},
"spuId": {
    "type": "keyword"
},
"skuTitle": {
    "type": "text",
    "analyzer": "ik_smart"
},
"skuPrice": {
    "type": "keyword"
},
"skuImg": {
    "type": "keyword",
    "index": false,
    "doc_values": false
},
"saleCount": {
    "type": "long"
},
"hasStock": {
    "type": "boolean"
},
"hotScore": {
    "type": "long"
},
"brandId": {
    "type": "long"
},
"catalogId": {
    "type": "long"
},
"brandName": {
    "type": "keyword",
    "index": false,
    "doc_values": false
},
"brandImg": {
    "type": "keyword",
    "index": false,
    "doc_values": false
},
"catalogName": {
    "type": "keyword",
    "index": false,
    "doc_values": false
},
"attrs": {
    "type": "nested",
    "properties": {
        "attrId": {
            "type": "long"
        },
        "attrName": {
            "type": "keyword",
            "index": false,
            "doc_values": false
        },
        "attrValue": {
            "type": "keyword"
        }
    }
}
*/

@Data
public class SkuEsModel {

    //From SkuInfoEntity
    private Long skuId;
    private Long spuId;
    private String skuTitle;
    private BigDecimal skuPrice; // 字段不匹配
    private String skuImg;       // 字段不匹配
    private Long saleCount;
    private Long brandId;
    private Long catalogId;
    // From 业务
    private Boolean hasStock;
    private Long hotScore;
    // From CategoryEntity
    private String catalogName;
    // From BrandEntity
    private String brandName;
    private String brandImg;
    // From SkuSaleAttrValueEntity
    private List<Attrs> attrs;

    @Data
    public static class Attrs {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}

