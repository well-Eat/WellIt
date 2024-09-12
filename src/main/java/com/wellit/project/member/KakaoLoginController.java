package com.wellit.project.member;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping
public class KakaoLoginController {

	private final KakaoService kakaoService;

	@GetMapping("/callback")
    public String callback(@RequestParam("code") String code, RedirectAttributes redirectAttributes) throws IOException {
        // 1. 카카오에서 accessToken을 얻음
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        System.out.println("========================================" + accessToken);

        // 2. accessToken을 사용하여 사용자 정보를 DB에 저장
        Member member = kakaoService.registerKakaoUser(accessToken);

        // 3. 저장 완료 후, 인덱스 페이지로 리다이렉트 (메시지 포함)
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/";
    }
    


    @PostMapping("/callback")
    public ResponseEntity<String> kakaoLogin(@RequestParam String accessToken) {
    	System.out.println("123123123213123123"+accessToken);
        // 액세스 토큰을 사용해 사용자 정보를 저장
        Member member = kakaoService.registerKakaoUser(accessToken);

        // 사용자 정보를 성공적으로 저장하면 성공 메시지 리턴
        return ResponseEntity.ok("Kakao user registered: " + member.getMemberAlias());
    }
}