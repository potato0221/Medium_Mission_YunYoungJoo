package com.ll.medium.global.payment.handler;

import com.ll.medium.domain.member.member.entity.SiteMember;
import com.ll.medium.domain.member.security.service.MemberSecurityService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentEventHandler {

    private final MemberSecurityService memberSecurityService;


    public void handlePaymentEvent(SiteMember siteMember) {
        // 결제가 완료되었을 때 사용자의 역할을 업데이트 ( 결제를 도입하면 사용할 예정)
        memberSecurityService.addPaidRole(siteMember);
    }
}
