package com.wellit.project;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public String handleDataNotFoundException(DataNotFoundException ex, RedirectAttributes redirectAttributes) {
        // 예외 메시지를 플래시 속성으로 전달
        redirectAttributes.addFlashAttribute("errorMessage", "로그인 해주세요.");
        // 로그인 페이지로 리다이렉트
        return "redirect:/member/login";
    }
}
