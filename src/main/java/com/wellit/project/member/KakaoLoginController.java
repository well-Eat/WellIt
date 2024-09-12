package com.wellit.project.member;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping()
public class KakaoLoginController {

    private final KakaoService kakaoService;
    private final MemberService memberService;

    
    // 카카오 로그인 후 callback (GET 방식)
    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, RedirectAttributes redirectAttributes, Model model) throws IOException {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        Member member = kakaoService.registerKakaoUser(accessToken);

        // 사용자 정보로 세션 설정
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        session.setAttribute("loggedInMember", member);

        // 추가 정보 입력 페이지로 리다이렉트
        model.addAttribute("member", member);
        model.addAttribute("KakaoSignupForm", new KakaoSignupForm());
        
        return "member/kakao_signup";
    }

    // 카카오 로그인 후 callback (POST 방식) - 액세스 토큰 직접 처리
    @PostMapping("/callback")
    public ResponseEntity<String> kakaoLogin(@RequestParam String accessToken) {
        Member member = kakaoService.registerKakaoUser(accessToken);
        return ResponseEntity.ok("Kakao user registered: " + member.getMemberAlias());
    }
    
    @GetMapping("/member/kakao_signup")
    public String geKakaoSignup(Model model) {
        model.addAttribute("member", new KakaoSignupForm());
        return "member/kakao_signup";  // 추가 정보 입력 폼 페이지
    }

 // 추가 정보 입력 폼 처리 (회원 가입 완료)
    @PostMapping("/member/kakao_signup")
    public String kakaoSignup(@Valid @ModelAttribute("member") KakaoSignupForm kakaoSignupForm,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes,
                               HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> {
                        if (error instanceof FieldError) {
                            FieldError fieldError = (FieldError) error;
                            return fieldError.getDefaultMessage();
                        }
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.joining("\n"));

            model.addAttribute("errorMessage", errorMessages);
            model.addAttribute("member", kakaoSignupForm);

            return "member/kakao_signup";
        }

        try {
            Member member = kakaoService.updateKakaoMember(kakaoSignupForm);

            // 회원 가입 완료 후 로그인 처리
            session.setAttribute("loggedInMember", member);

            return "redirect:/home"; // 로그인 후 이동할 페이지
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("updateFailed", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("member", kakaoSignupForm);
            return "member/kakao_signup";
        }
    }
}
