package com.ll.medium.global.payment.service;

import com.ll.medium.domain.member.member.entity.SiteMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    @Transactional
    public boolean checkPaymentStatusFromPaymentSystem(SiteMember siteMember) {
        if (siteMember.isPaid()) {
            return true;
        }
        return false;
    }


}
