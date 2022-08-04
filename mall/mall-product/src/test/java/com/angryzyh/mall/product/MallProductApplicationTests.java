package com.angryzyh.mall.product;

import com.angryzyh.mall.product.service.CategoryBrandRelationService;
import com.angryzyh.mall.product.service.CategoryService;
import com.angryzyh.mall.product.service.SpuCommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    SpuCommentService commentService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Test
    void contextLoads() {
//        SpuCommentEntity spuCommentEntity = new SpuCommentEntity();
//        spuCommentEntity.setSpuName("憨憨平已上架");
//        boolean save = commentService.save(spuCommentEntity);
//        System.out.println("save = " + save);

        // 测试 递归调用查询 分类路径
       /* Long[] catelogPath = categoryService.findCatelogPath(225L);
        System.out.println("catelogPath = " + Arrays.asList(catelogPath));*/
    }
}
