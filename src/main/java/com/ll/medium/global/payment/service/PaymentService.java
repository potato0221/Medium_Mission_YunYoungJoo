package com.ll.medium.global.payment.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {


    public boolean checkPaymentStatusFromPaymentSystem(SiteMember siteMember) {
        // 결제를 도입하지 않아서 가짜로 결제 되었다고 설정
        if (siteMember.isPaid()) {
            return true;
        }
        return false;
    }


}
