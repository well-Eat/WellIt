package com.wellit.project.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Log4j2
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    private final String impApikey = "7751726468745650";
    private final String impSecret = "hxURD07lY4IosdfUyWh00sz4KyxWwD2ox5gYrLUF9lqjzCaJCLt4u4JXRwZDQ23jpfaJ5LDtq4QHHq02";


    // 결제정보 저장
    @PostMapping("/save/{orderId}")
    public ResponseEntity<String> savePayment(@RequestBody PaymentRequest paymentRequest, @PathVariable("orderId") String orderId) {
        try {
            PurchaseOrder po = orderService.getOnePO(orderId);
            paymentService.savePayment(paymentRequest, orderId, po);
            orderService.savePurchaseOrder(po);
            return ResponseEntity.ok("결제 정보 저장 성공");
        } catch (Exception e) {
            log.error("결제 정보 저장 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장 실패: " + e.getMessage());
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancelPayment(@RequestBody PaymentCancelRequest cancelRequest) {
        try {
            log.info("결제 취소 요청 - impUid: {}, reason: {}", cancelRequest.getImpUid(), cancelRequest.getReason());

            // 결제 취소 처리 로직 호출
            paymentService.cancelPayment(cancelRequest.getImpUid(), cancelRequest.getReason());

            // 성공 시 JSON 형식으로 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "결제 취소 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 실패 시 JSON 형식으로 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}


// 결제 취소 요청을 위한 DTO 클래스
class PaymentCancelRequest {
    private String impUid;
    private String reason;

    public String getImpUid() {
        return impUid;
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
