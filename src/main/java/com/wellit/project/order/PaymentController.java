package com.wellit.project.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/save/{orderId}")
    public ResponseEntity<String> savePayment(@RequestBody PaymentRequest paymentRequest, @PathVariable String orderId) {
        try {
            log.info("1");
            PurchaseOrder po = orderService.getOnePO(orderId);
            log.info("#######################");
            log.info(orderId);
            paymentService.savePayment(paymentRequest, po); // 결제 정보를 저장하는 서비스
            log.info("2");

            return ResponseEntity.ok("결제 정보 저장 성공");
        } catch (Exception e) {
            log.info("3");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장 실패: " + e.getMessage());

        }
    }

}
