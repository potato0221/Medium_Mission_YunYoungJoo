package com.ll.medium.global.initdata;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.service.PostService;
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
            siteMember2 = memberService.create("premium1", "www2@email.com", "1234");

            for (int i = 1; i <= 60; i++) {
                Post post = new Post();
                postService.create(
                        "글 제목 " + i,
                        "글 내용 " + i,
                        siteMember1
                        ,false
                );
            }

            for(int i=61;i<=80;i++){
                Post post=new Post();
                postService.create(
                        "유료 글 "+i,
                        "유료 글 내용" + i,
                        siteMember2
                        , true
                );
            }

            Post post = new Post();
            postService.create(
                    "내 글 테스트 용",
                    "글 내용 ",
                    siteMember2,
                    false
            );


        };
    }

}

