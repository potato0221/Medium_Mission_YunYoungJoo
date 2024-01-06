package com.ll.medium.domain.member.membership.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.membership.entity.Membership;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {


    public Membership create(SiteMember member) {
        Membership membership= Membership.builder()
                .siteMember(member)
                .build();
        return null;
    }
}
