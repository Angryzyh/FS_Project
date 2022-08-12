package com.angryzyh.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.angryzyh.common.to.es.SkuEsModel;
import com.angryzyh.common.utils.R;
import com.angryzyh.mall.search.config.ElasticSearchConfig;
import com.angryzyh.mall.search.constant.EsConstant;
import com.angryzyh.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        // 批量操作请求
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //创建索引
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //添加索引的 id
            indexRequest.id(skuEsModel.getSkuId().toString());
            String skuEsModelStr = JSON.toJSONString(skuEsModel);
            //添加索引的_source数据
            indexRequest.source(skuEsModelStr, XContentType.JSON);
            //添加到批量操作
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        // 打印错误信息
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
        if (b) {
            log.error("商品上架错误:{},返回数据{}", collect,bulk.toString());
        }else {
            log.info("商品成功:{},返回数据{}", collect,bulk.toString());
        }
        return b;
    }
}