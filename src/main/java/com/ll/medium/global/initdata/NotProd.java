package com.ll.medium.global.initdata;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Profile("!prod")
@Configuration
public class NotProd {

    @Bean
    @Order(3)
    public ApplicationRunner initNotProd(
            MemberService memberService,
            PostService postService
    ) {
        return args -> {

            if (memberService.count() > 0) return;
            SiteMember siteMember1 = memberService.join("user1", "www1@email.com", "1234", "유저1").getData();
            SiteMember siteMember2 = memberService.join("paid1", "paid1@email.com", "1234", "유료회원1").getData();
            SiteMember siteMember3 = memberService.join("paid2", "paid2@email.com", "1234", "유료회원2").getData();
            memberService.alreadyPaid(siteMember2);
            memberService.alreadyPaid(siteMember3);

            for (int i = 3; i <= 100; i++) {
                SiteMember siteMember = memberService.join("paid" + i, "paid" + i + "@email.com", "1234", "유료회원" + i).getData();
                memberService.alreadyPaid(siteMember);
            }


            for (int i = 1; i <= 60; i++) {
                Post post = postService.create(
                        siteMember1,
                        "글 제목 " + i,
                        "글 내용 " + i,
                        siteMember1
                        , false,
                        false,
                        siteMember1.getCount()
                );


            }

            for (int i = 1; i <= 50; i++) {
                Post post = postService.create(
                        siteMember2,
                        "유료 글 " + i,
                        "유료 글 내용" + i,
                        siteMember2
                        , true,
                        false,
                        siteMember2.getCount()
                );
            }
            for (int i = 51; i <= 100; i++) {
                Post post = postService.create(
                        siteMember3,
                        "유료 글 " + i,
                        "유료 글 내용" + i,
                        siteMember3
                        , true,
                        false,
                        siteMember3.getCount()
                );
            }

        };
    }

}

