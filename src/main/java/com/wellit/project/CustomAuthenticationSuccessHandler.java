package com.wellit.project;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 사용자 아이디를 세션에 저장
        request.getSession().setAttribute("UserId", authentication.getName());
        
        // 로그인 성공 후 리다이렉트할 URL
        response.sendRedirect("/member/mypage");
    }
}