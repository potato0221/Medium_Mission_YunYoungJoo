package com.ll.medium.domain.member.member.role;

import lombok.Getter;

@Getter
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    PAID("ROLE_PAID"),
    USER("ROLE_USER");

    private final String roleName;

    MemberRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

}
