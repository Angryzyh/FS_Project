package com.angryzyh.mall.product;

import com.angryzyh.mall.product.entity.SpuCommentEntity;
import com.angryzyh.mall.product.service.SpuCommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    SpuCommentService commentService;

    @Test
    void contextLoads() {
//        SpuCommentEntity spuCommentEntity = new SpuCommentEntity();
//        spuCommentEntity.setSpuName("憨憨平已上架");
//        boolean save = commentService.save(spuCommentEntity);
//        System.out.println("save = " + save);
    }
}
