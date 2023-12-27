package com.ll.medium.domain.member.member.service;

import com.ll.medium.domain.base.genFile.service.GenFileService;
import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.app.AppConfig;
import com.ll.medium.global.exception.DataNotFoundException;
import com.ll.medium.global.rsData.RsData;
import com.ll.medium.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenFileService genFileService;

    @Transactional
    public RsData<SiteMember> create(String username, String email, String password, String nickname) {
        return create(username, email, password, nickname, null);
    }

    @Transactional
    public RsData<SiteMember> create(String username, String email, String password, String nickname, String profileImgFilePath) {
        SiteMember user = SiteMember.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .count(0)
                .isPaid(false)
                .build();
        this.memberRepository.save(user);

        if (Ut.str.hasLength(profileImgFilePath)) {
            saveProfileImg(user, profileImgFilePath);
        }

        return RsData.of("200", "%s님 회원가입이 완료 되었습니다. 로그인 후 이용 해 주세요.".formatted(user.getUsername()), user);
    }

    private void saveProfileImg(SiteMember member, String profileImgFilePath) {
        genFileService.save(member.getModelName(), member.getId(), "common", "profileImg", 1, profileImgFilePath);
    }

    @Transactional
    public void alreadyPaid(SiteMember siteMember) {
        siteMember.setPaid(true);
        this.memberRepository.save(siteMember);
    }


    public SiteMember getUser(String username) {
        Optional<SiteMember> siteUser = this.memberRepository.findByUsername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }


    public long count() {
        return memberRepository.count();
    }

    public Optional<SiteMember> findByUsername(String username) {

        return memberRepository.findByUsername(username);
    }

    @Transactional
    public void save(SiteMember siteMember) {
        memberRepository.save(siteMember);

    }

    @Transactional
    public RsData<SiteMember> whenSocialLogin(String providerTypeCode, String username, String nickname, String profileImgUrl) {
        Optional<SiteMember> opMember = findByUsername(username);

        if (opMember.isPresent()) return RsData.of("200", "이미 존재합니다.", opMember.get());

        String filePath = Ut.str.hasLength(profileImgUrl) ? Ut.file.downloadFileByHttp(profileImgUrl, AppConfig.getTempDirPath()) : "";

        return create(username, username + "@email.com", "", nickname, filePath);
    }
}
