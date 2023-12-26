package com.ll.medium.domain.member.member.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.exception.DataNotFoundException;
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


    @Transactional
    public SiteMember create(String username, String email, String password, Integer count) {
        SiteMember user = new SiteMember();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCount(count);
        user.setPaid(false);
        this.memberRepository.save(user);
        return user;
    }

    @Transactional
    public SiteMember alreadyPaid(SiteMember siteMember){
        siteMember.setPaid(true);
        this.memberRepository.save(siteMember);
        return siteMember;
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
}
