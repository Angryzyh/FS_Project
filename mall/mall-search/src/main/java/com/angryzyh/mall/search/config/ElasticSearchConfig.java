package com.angryzyh.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static{
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        /*
        builder.addHeader("Authorization", "Bearer" + TOKEN);
        builder.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory.
                        HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024)); */
        COMMON_OPTIONS=builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.11.130", 9200, "http"));
        return new RestHighLevelClient(builder);
    }
}

