package com.ll.medium.global.rq;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
@Getter
@RequiredArgsConstructor
@Slf4j
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberService memberService;
    private User user;
    private SiteMember member;

    @PostConstruct
    public void init() {
        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            this.user = (User) authentication.getPrincipal();
        }
    }

    private String getMemberUsername() {
        return user.getUsername();
    }

    public boolean isLogined() {
        return user != null;
    }

    public SiteMember getMember() {
        if (!isLogined()) {
            return null;
        }

        if (member == null)
            member = memberService.findByUsername(getMemberUsername()).get();

        return member;
    }

    public boolean isPremium() {
        if (!isLogined()) {
            return false;
        }
        return user.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PREMIUM"));
    }

}