package com.wellit.project.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    //private final OrderService orderService;


    //@Transactional(rollbackFor = Exception.class) : 예외 발생 클래스 추가 지정
    @Transactional
    public void savePayment(PaymentRequest paymentRequest, PurchaseOrder po) {
        Payment payment = new Payment();
        payment.setImpUid(paymentRequest.getImp_uid());
        payment.setMerchantUid(paymentRequest.getMerchant_uid());
        payment.setPaidAmount(paymentRequest.getPaid_amount());
        payment.setBuyerEmail(paymentRequest.getBuyer_email());
        payment.setBuyerName(paymentRequest.getBuyer_name());
        payment.setPaymentStatus(paymentRequest.getStatus());
        payment.setSuccess(paymentRequest.isSuccess());
        payment.setPayMethod(paymentRequest.getPay_method());
        payment.setPgProvider(paymentRequest.getPg_provider());
        payment.setPayMethod(paymentRequest.getPay_method());
        payment.setPurchaseOrder(po);

        paymentRepository.save(payment);
    }


/* payment에서 저장하니까 빼보자 우선*/
    public Payment getPayment(String orderId){
        return paymentRepository.findPaymentByPurchaseOrder_OrderId(orderId);
    }


}
