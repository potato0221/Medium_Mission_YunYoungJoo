package com.ll.medium.global.initdata;

import com.ll.medium.domain.article.article.entity.Post;
import com.ll.medium.domain.article.article.service.PostService;
import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod")
@Configuration
public class NotProd {

    @Bean
    public ApplicationRunner initNotProd(
            MemberService memberService,
            PostService postService
    ) {
        return args -> {

            if (memberService.count() > 0) return;
            SiteMember siteMember1 = new SiteMember();
            SiteMember siteMember2 = new SiteMember();
            siteMember1 = memberService.create("user1", "www1@email.com", "1234");
            siteMember2 = memberService.create("user2", "www2@email.com", "1234");

            for (int i = 1; i <= 60; i++) {
                Post post = new Post();
                postService.create(
                        "글 제목 " + i,
                        "글 내용 " + i,
                        siteMember1
                );
            }


        };
    }

}

