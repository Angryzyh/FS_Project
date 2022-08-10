package com.angryzyh.mall.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.angryzyh.mall.search.config.ElasticSearchConfig;
import com.mysql.cj.QueryBindings;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class MallSearchApplicationTests {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    //查询索引库,聚合操作
    @Test
    void searchTest() throws IOException {
        //1. 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL,索引条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // match匹配语句
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("address", "mill");
        searchSourceBuilder.query(matchQueryBuilder);
        // 聚合 操作语句
        // 按照年龄值分布 聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        // 计算平均工资
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);
        // 封装全部 DSL 语句
        System.out.println("检索条件 = " + searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        // 2. 执行检索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        // 3.1  分析 检索结果
        System.out.println("返回结果 = " + searchResponse.toString());
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            // 把string 转换为 java实体类
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account = " + account);
        }
        // 3.2 分析 聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            System.out.println("年龄: " + bucket.getKeyAsString()+"人数:"+bucket.getDocCount());
        }
        Avg balanceAvg1 = aggregations.get("balanceAvg");
        System.out.println("平均工资 = " + balanceAvg1.getName()+balanceAvg1.getValueAsString());
    }

    // 给 索引库添加 数据
    @Test
    void indexTest() throws IOException {
        // 创建索引库
        IndexRequest indexRequest = new IndexRequest("users");
        // 文档id
        indexRequest.id("1");
        User user = new User();
        user.setUsername("憨憨平");
        user.setPassword("123456");
        user.setAge(18);
        user.setGender("女");
        // 把java实体类转换成json字符串
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println("index = " + index);
    }

    @Data
    class User{
        private String username;
        private String password;
        private Integer age;
        private String gender;
    }

    @Data
    public class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }
}
