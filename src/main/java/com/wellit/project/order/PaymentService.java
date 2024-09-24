package com.wellit.project.order;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final String TOKEN_KEY = "iamport:accessToken";
    private final String EXPIRE_TIME_KEY = "iamport:expireTime";

    private final String impApikey = "7751726468745650";
    private final String impSecret = "hxURD07lY4IosdfUyWh00sz4KyxWwD2ox5gYrLUF9lqjzCaJCLt4u4JXRwZDQ23jpfaJ5LDtq4QHHq02";

    private String cachedAccessToken;
    private long tokenExpireTime; // 토큰 만료 시간


    // 결제 정보 조회 (결제 검증용)
    public Map<String, Object> getPaymentData(String impUid) throws Exception {
        String accessToken = getAccessToken();  // 공통 메서드 사용

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/" + impUid, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return (Map<String, Object>) response.getBody().get("response");
        } else {
            throw new RuntimeException("결제 정보 조회 실패");
        }
    }







    // 결제 정보 저장 및 검증
    @Transactional
    public void savePayment(PaymentRequest paymentRequest, String orderId, PurchaseOrder po) throws Exception {
        String impUid = paymentRequest.getImp_uid();
        log.info("impUid: " + impUid);

        // 결제 정보 조회
        Map<String, Object> paymentData = getPaymentData(impUid);

        // 결제 검증
        if (paymentData != null && "paid".equals(paymentData.get("status"))) {

            // 결제 정보 저장
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
            payment.setPurchaseOrder(po);

            paymentRepository.save(payment);
            log.info("결제 정보 저장 성공");
        } else {
            log.error("결제 검증 실패");
            throw new RuntimeException("결제 실패");
        }
    }

    // 결제 취소 처리
    @Transactional
    public void cancelPayment(String impUid, String reason) throws Exception {
        log.info("결제 취소 요청 - impUid: " + impUid + ", reason: " + reason);

        // 공통 메서드를 사용하여 액세스 토큰 발급
        String accessToken = getAccessToken();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        // 결제 취소 요청 데이터 구성
        Map<String, String> cancelData = new HashMap<>();
        cancelData.put("imp_uid", impUid);
        cancelData.put("reason", reason);

        // API 요청을 위한 HttpEntity 생성
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(cancelData, headers);

        // 포트원 결제 취소 API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.iamport.kr/payments/cancel", entity, Map.class);

        // 응답 처리
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("결제 취소 성공");
        } else {
            log.error("결제 취소 실패: " + response.getBody());
            throw new RuntimeException("결제 취소 실패");
        }
    }



    /* payment에서 저장하니까 빼보자 우선*/
    public Payment getPayment(String orderId){
        return paymentRepository.findPaymentByPurchaseOrder_OrderId(orderId);
    }

    public String getAccessToken() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                                         .uri(URI.create("https://api.iamport.kr/users/getToken"))
                                         .header("Content-Type", "application/json")
                                         .method("POST", HttpRequest.BodyPublishers.ofString("{\"imp_key\":\"7751726468745650\",\"imp_secret\":\"hxURD07lY4IosdfUyWh00sz4KyxWwD2ox5gYrLUF9lqjzCaJCLt4u4JXRwZDQ23jpfaJ5LDtq4QHHq02\"}"))
                                         .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        // 응답 내용 출력
        String responseBody = response.body();
        log.info(responseBody);

        // "access_token" 추출
        String accessToken = responseBody.split("\"access_token\":\"")[1].split("\"")[0];

        // 토큰 출력
        System.out.println("Access Token: " + accessToken);

        // 추출한 토큰 반환
        return accessToken;

    }



    // 결제 취소 요청
    public Payment updateStateCancel(String impUid, String reason){
       Payment payment = paymentRepository.findByImpUid(impUid);
       payment.setPaymentStatus("CANCELLED");
       payment.setCancelReason(reason);
       return paymentRepository.save(payment);
    }


}
