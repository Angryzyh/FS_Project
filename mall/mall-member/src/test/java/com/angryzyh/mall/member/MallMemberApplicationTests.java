package com.angryzyh.mall.member;

import com.angryzyh.mall.member.entity.MemberEntity;
import com.angryzyh.mall.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MallMemberApplicationTests {

    @Autowired
    MemberService memberService;

    @Test
    void contextLoads() {
        /*MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("憨憨平是会员");
        boolean save = memberService.save(memberEntity);
        System.out.println("save = " + save);*/
    }

}
