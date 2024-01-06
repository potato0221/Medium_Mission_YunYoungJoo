package com.ll.medium.global.security;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;
    private final PaymentService paymentService;

    // 카카오톡 로그인이 성공할 때 마다 이 함수가 실행된다.
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String oauthId = oAuth2User.getName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map attributesProperties = (Map) attributes.get("properties");
        String nickname = (String) attributesProperties.get("nickname");
        String profileImgUrl = (String) attributesProperties.get("profile_image");

        String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        String username = providerTypeCode + "__%s".formatted(oauthId);

        SiteMember member = memberService.whenSocialLogin(providerTypeCode, username, nickname, profileImgUrl).getData();

        // 권한 목록 초기화 및 USER 권한 부여
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // PAID 권한 확인 및 부여
        if (paymentService.checkPaymentStatusFromPaymentSystem(member)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PAID"));
        }

        return new SecurityUser(member.getId(), member.getUsername(), member.getPassword(), authorities);
    }
}
