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
            siteMember1 = memberService.create("user1", "www1@email.com", "1234",0);
            siteMember2 = memberService.create("premium1", "www2@email.com", "1234",0);

            for (int i = 1; i <= 60; i++) {
                Post post = new Post();
                siteMember1.setCount(siteMember1.getCount()+1);
                memberService.save(siteMember1);
                postService.create(
                        "글 제목 " + i,
                        "글 내용 " + i,
                        siteMember1
                        ,false,
                        false,
                        siteMember1.getCount()
                );


            }

            Integer count2=0;
            for(int i=1;i<=20;i++){
                Post post=new Post();
                siteMember2.setCount(siteMember2.getCount()+1);
                memberService.save(siteMember2);
                postService.create(
                        "유료 글 "+i,
                        "유료 글 내용" + i,
                        siteMember2
                        , true,
                        false,
                        siteMember2.getCount()
                );
            }

        };
    }

}

