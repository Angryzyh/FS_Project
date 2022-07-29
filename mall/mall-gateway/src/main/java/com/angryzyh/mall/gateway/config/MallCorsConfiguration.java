package com.angryzyh.mall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class MallCorsConfiguration {
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //实际请求中可以出现在响应中的headers集合
        corsConfiguration.addAllowedHeader("*");
        //实际请求中可以使用到的方法集合
        corsConfiguration.addAllowedMethod("*");
        //允许请求的域，多数情况下，就是预检请求中的Origin的值
        corsConfiguration.addAllowedOrigin("*");
        //一个布尔值，表示服务器是否允许使用cookies 跨域共享
        corsConfiguration.setAllowCredentials(true);
        //把上面的设置都注册进跨域设置中,设置对所有路径生效
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }
}
