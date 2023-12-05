package com.ll.medium.domain.member.security.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.domain.member.member.role.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SiteMember> _siteMember = this.memberRepository.findByusername(username);
        if (_siteMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        SiteMember siteMember = _siteMember.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getRoleName()));
        } else {
            authorities.add(new SimpleGrantedAuthority(MemberRole.USER.getRoleName()));
        }
        return new User(siteMember.getUsername(), siteMember.getPassword(), authorities);
    }
}
