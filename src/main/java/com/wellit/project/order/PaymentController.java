package com.wellit.project.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
            String impUid = paymentRequest.getImp_uid();
            String merchantUid = paymentRequest.getMerchant_uid();


            //액세스 토큰 발급
            String accessToken = getAccessToken();

            // 결제 정보 조회
            Map<String, Object> paymentData = getPaymentData(impUid, accessToken);

            // 결제 검증 및 처리 로직 추가 가능
            if (paymentData != null && "paid".equals(paymentData.get("status"))) {
                PurchaseOrder po = orderService.getOnePO(orderId);
                paymentService.savePayment(paymentRequest, po); // 결제 정보를 저장하는 서비스

                return ResponseEntity.ok("결제 정보 저장 성공");
            } else {
                // 결제 실패 시 처리
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 실패");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장 실패: " + e.getMessage());

        }
    }
    // 포트원 API로 액세스 토큰 발급받기
    private String getAccessToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("imp_key", impApikey); // REST API 키
        tokenRequest.put("imp_secret", impSecret); // REST API Secret

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(tokenRequest, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.iamport.kr/users/getToken", entity, Map.class);

        Map<String, Object> body = (Map<String, Object>) response.getBody().get("response");
        String accToken = (String) body.get("access_token");
        log.info(accToken);
        return accToken;
    }

    // 받은 accessToken과 imp_uid로 결제 정보 조회
    private Map<String, Object> getPaymentData(String impUid, String accessToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/" + impUid, HttpMethod.GET, entity, Map.class);

        return (Map<String, Object>) response.getBody().get("response");
    }

}
