package com.angryzyh.mall.ware;

import com.angryzyh.mall.ware.entity.WareSkuEntity;
import com.angryzyh.mall.ware.service.WareSkuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallWareApplicationTests {

    @Autowired
    WareSkuService wareSkuService;

    @Test
    void contextLoads() {
      /*  WareSkuEntity wareSkuEntity = new WareSkuEntity();
        wareSkuEntity.setSkuName("憨憨平类型产品入库");
        boolean save = wareSkuService.save(wareSkuEntity);
        System.out.println("save = " + save);*/
    }

}
