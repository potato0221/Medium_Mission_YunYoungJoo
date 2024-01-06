package com.ll.medium.domain.member.membership.entity;


import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class Membership extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "site_member_id")
    private SiteMember siteMember;

}

