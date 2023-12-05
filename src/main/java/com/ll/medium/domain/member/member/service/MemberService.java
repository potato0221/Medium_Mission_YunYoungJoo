package com.ll.medium.domain.member.member.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public SiteMember create(String username, String email, String password){
        SiteMember user=new SiteMember();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.memberRepository.save(user);
        return user;
    }
    public SiteMember getUser(String username){
        Optional<SiteMember> siteUser=this.memberRepository.findByusername(username);
        if(siteUser.isPresent()){
            return siteUser.get();
        }else {
            throw new DataNotFoundException("siteuser not found");
        }
    }


    public long count() {
        return memberRepository.count();
    }

}
