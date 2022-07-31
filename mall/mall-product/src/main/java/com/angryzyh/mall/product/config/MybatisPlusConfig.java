package com.angryzyh.mall.product.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement //开启事务注解
@MapperScan("com.angryzyh.mall.product.dao")
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        //Mybatis-Plus 插件
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //Mybatis-Plus分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        // 设置溢出总页数后是否进行处理,true 跳转到首页
        paginationInnerInterceptor.setOverflow(true);
        // 设置单页分页条数限制
        paginationInnerInterceptor.setMaxLimit(1000L);
        // 把分页插件添加到插件管理内
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        //Mybatis-Plus乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
