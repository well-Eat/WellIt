package com.wellit.project;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminRedirectFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String memberId = (String) httpRequest.getSession().getAttribute("UserId"); // 세션에서 memberId 가져오기

        // 요청 URL이 /admin/으로 시작하고 memberId가 "admin"이 아닐 경우                
        if (httpRequest.getRequestURI().contains("/admin/") && !("admin".equals(memberId))) {
            httpResponse.sendRedirect("/member/mypage"); // 마이페이지로 리다이렉트
            return; // 필터 체인 진행 중단
        }

        chain.doFilter(request, response); // 필터 체인 계속 진행
    }
}
