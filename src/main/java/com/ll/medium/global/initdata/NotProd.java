package com.ll.medium.global.initdata;

import com.ll.medium.domain.user.user.entity.SiteUser;
import com.ll.medium.domain.user.user.service.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!prod")
@Configuration
public class NotProd {

    @Bean
    public ApplicationRunner initNotProd(
            UserService userService
    ) {
        return args -> {

            if (userService.count() > 0) return;
            SiteUser siteUser1 = new SiteUser();
            SiteUser siteUser2 = new SiteUser();
            siteUser1 = userService.create("user1", "www1@email.com", "1234");
            siteUser2 = userService.create("user2", "www2@email.com", "1234");


        };
    }

}

