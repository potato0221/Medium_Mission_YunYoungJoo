package com.ll.medium.global.payment.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean checkPaymentStatusFromPaymentSystem(String username) {
        // 결제를 도입하지 않아서 가짜로 결제 되었다고 설정
        if(username.contains("premium")){
            return true;
        }
        return false;
    }


}
