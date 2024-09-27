package com.wellit.project.member;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellit.project.CustomUserAlreadyExistsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private String clientId = "1edf78aa7a0cc88fd28e4ffbc306e0b1";
    private final String KAUTH_TOKEN_URL_HOST;
    private final String KAUTH_USER_URL_HOST;
    private String redirectUri = "http://localhost:8080/callback"; 
    
    @Autowired
    public KakaoService(@Value("${kakao.client_id}") String clientId, MemberRepository memberRepository, WebClient.Builder webClientBuilder) {
        this.clientId = clientId;
        KAUTH_TOKEN_URL_HOST ="https://kauth.kakao.com";
        KAUTH_USER_URL_HOST = "https://kapi.kakao.com";
		this.memberRepository = memberRepository;
    }

    public String getAccessTokenFromKakao(String code) {
        String response = WebClient.create("https://kauth.kakao.com")
            .post()
            .uri(uriBuilder -> uriBuilder
                .path("/oauth/token")
                .build())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .bodyValue("grant_type=authorization_code" +
                       "&client_id=" + clientId +
                       "&redirect_uri=" + redirectUri +
                       "&code=" + code)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.info("Raw response: {}", response);

        try {
            KakaoTokenResponseDto responseDto = new ObjectMapper().readValue(response, KakaoTokenResponseDto.class);

            log.info(" [Kakao Service] Access Token ------> {}", responseDto.getAccessToken());
            log.info(" [Kakao Service] Refresh Token ------> {}", responseDto.getRefreshToken());
            log.info(" [Kakao Service] Id Token ------> {}", responseDto.getIdToken());
            log.info(" [Kakao Service] Scope ------> {}", responseDto.getScope());

            return responseDto.getAccessToken();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse response", e);
            throw new RuntimeException("Failed to parse response", e);
        }
    }
       
    
    private final MemberRepository memberRepository;


    
    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
    	System.out.println("getUserInfo 액세스 토큰 : "+accessToken);
    	
        String response = WebClient.create("https://kapi.kakao.com")
        		
            .get()
            .uri("/v2/user/me")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        log.info("Raw user info response: {}", response);

        try {
            KakaoUserInfoResponseDto userInfo = new ObjectMapper().readValue(response, KakaoUserInfoResponseDto.class);

            if (userInfo.getKakaoAccount() != null) {
                KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = userInfo.getKakaoAccount();
                if (kakaoAccount.getProfile() != null) {
                    KakaoUserInfoResponseDto.KakaoAccount.Profile profile = kakaoAccount.getProfile();
                    log.info(" [Kakao Service] User Nickname ------> {}", profile.getNickName());
                    log.info(" [Kakao Service] User Profile Image ------> {}", profile.getProfileImageUrl());
                } else {
                    log.warn("KakaoAccount profile is null");
                }
            } else {
                log.warn("KakaoAccount is null");
            }

            return userInfo;
        } catch (JsonProcessingException e) {
            log.error("Failed to parse user info response", e);
            throw new RuntimeException("Failed to parse user info response", e);
        }
    }



    public Member saveKakaoUser(KakaoUserInfoResponseDto kakaoUserInfo) {
        if (kakaoUserInfo == null || kakaoUserInfo.getKakaoAccount() == null) {
            throw new RuntimeException("Invalid Kakao user info received.");
        }

        KakaoUserInfoResponseDto.KakaoAccount kakaoAccount = kakaoUserInfo.getKakaoAccount();
        KakaoUserInfoResponseDto.KakaoAccount.Profile profile = kakaoAccount.getProfile();

        if (profile == null) {
            throw new RuntimeException("Profile information is missing in Kakao user info.");
        }

        Member member = new Member();

        // ID 설정
        member.setMemberId(String.valueOf(kakaoUserInfo.getId()));

        // 닉네임
        member.setMemberAlias(profile.getNickName());

        // 이미지 파일 URL
        member.setImageFile(profile.getProfileImageUrl());

        // 나머지 정보들 설정
        member.setMemberRegDate(LocalDateTime.now());
        member.setMileage(0); // 기본 마일리지 설정
        
        // 카카오 회원임을 명시
        member.setMemberType("KAKAO");

        // 엔티티 저장 (Repository를 통해 저장)
        return memberRepository.save(member);
    }


    // 액세스 토큰을 받아 회원 정보를 저장하는 전체 로직
    public Member registerKakaoUser(String accessToken) {
        System.out.println("액세스 토큰 : " + accessToken);

        // 카카오 사용자 정보 가져오기
        KakaoUserInfoResponseDto kakaoUserInfo = getUserInfo(accessToken);
        String kakaoUserId = String.valueOf(kakaoUserInfo.getId());

        // 기존 회원 여부 확인
        Member existingMember = memberRepository.findByMemberId(kakaoUserId);

        if (existingMember != null) {
            // 이미 가입된 회원이 존재할 경우 예외를 발생시킴
            throw new CustomUserAlreadyExistsException(kakaoUserId);
        } else {
            // 신규 회원인 경우, 사용자 정보를 DB에 저장
            return saveKakaoUser(kakaoUserInfo);
        }
    }
    
    
    public Member updateKakaoMember(KakaoSignupForm kakaoSignupForm) {
    	
    	log.info("Updating Kakao member with ID: {}", kakaoSignupForm.getMemberId());
    	    	 
        // 1. 카카오 로그인으로 저장된 회원을 찾아옴
    	Member member = memberRepository.findByMemberId(kakaoSignupForm.getMemberId());
    	if (member == null) {
    	    throw new IllegalArgumentException("존재하지 않는 회원입니다.");
    	}
        log.info("Found member: {}", member);

        // 2. 추가 정보를 업데이트
        member.setMemberName(kakaoSignupForm.getMemberName());
        member.setMemberAlias(kakaoSignupForm.getMemberAlias());
        member.setMemberPhone(kakaoSignupForm.getMemberPhone()); 
        member.setMemberAddress(kakaoSignupForm.getMemberAddress());
        member.setZipcode(kakaoSignupForm.getZipcode());
        member.setRoadAddress(kakaoSignupForm.getRoadAddress());
        member.setAddressDetail(kakaoSignupForm.getAddressDetail());
        member.setMemberGender(kakaoSignupForm.getMemberGender());           
        member.setMemberBirth(kakaoSignupForm.getMemberBirth());
        member.setBirth_year(kakaoSignupForm.getBirth_year());
        member.setBirth_month(kakaoSignupForm.getBirth_month());
        member.setBirth_day(kakaoSignupForm.getBirth_day());
        member.setMemberVeganType(kakaoSignupForm.getMemberVeganType());
        member.setMemberRegDate(LocalDateTime.now());
        member.setMileage(0);
        member.setMemberType("KAKAO");

        // 3. 업데이트된 회원 정보를 저장
        return memberRepository.save(member);
    }
    
    public void logoutKakaoUser(String accessToken) {
        String logoutUrl = "https://kapi.kakao.com/v1/user/logout";
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(logoutUrl, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            // 로그아웃 실패 처리
            e.printStackTrace();
        }
    }
    
    public String redirectToKakaoAccountLogout() {
        String clientId = "1edf78aa7a0cc88fd28e4ffbc306e0b1";
        String logoutRedirectUri = "http://localhost:8080";
        return "https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirectUri;
    }
}