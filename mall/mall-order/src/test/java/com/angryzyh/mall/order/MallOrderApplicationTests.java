package com.angryzyh.mall.order;

import com.angryzyh.mall.order.entity.OrderEntity;
import com.angryzyh.mall.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallOrderApplicationTests {

    @Autowired
    OrderService orderService;

    @Test
    void contextLoads() {
       /* OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberUsername("憨憨平");
        boolean save = orderService.save(orderEntity);
        System.out.println("save = " + save);*/

    }

}
