package com.study.project.member;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("/login")
    public String getLogin() {
        return "/member/login";
    }
    
    @GetMapping("/register")
	public String register(MemberRegisterForm memberRegisterForm, @AuthenticationPrincipal UserDetails userDetails, Model model) {
    	model.addAttribute("memberRegisterForm", new MemberRegisterForm());
		if(userDetails != null) {
			Member member = memberService.getMember(userDetails.getUsername());
			model.addAttribute("profileImage",member.getImageFile());
		}
		
		return "/member/register";
	}
	@PostMapping("/register")
	public String register(@Valid MemberRegisterForm memberRegisterForm, BindingResult bindingResult, @RequestParam("imageFile") MultipartFile imageFile) {
		System.out.println("111111");
    	System.out.println(memberRegisterForm.getMemberId());
    	if(bindingResult.hasErrors()) {
    	    bindingResult.getAllErrors().forEach(error -> {
    	        System.out.println(error.toString());
    	    });
    	    return "/member/register";
    	}
		
		if(!memberRegisterForm.getMemberPassword().equals(memberRegisterForm.getMemberPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect","2개의 패스워드가 서로 일치하지 않습니다.");
			//rejectValue(필드명, 오류 코드, 오류 메세지)
			return "/member/register";
		}
		try {
			memberService.create(memberRegisterForm.getMemberId(), memberRegisterForm.getMemberPassword(), memberRegisterForm.getMemberName(),  memberRegisterForm.getMemberAlias(), memberRegisterForm.getMemberEmail(), memberRegisterForm.getMemberPhone(), memberRegisterForm.getMemberAddress(), memberRegisterForm.getMemberBirth(), memberRegisterForm.getMemberGender(), memberRegisterForm.getMemberVeganType(), imageFile);
			
		}catch(DataIntegrityViolationException e) {
			//DataIntegrityViolationException : unique으로 설정한 값에 같은 데이터가 들어갈 때 발생하는 예외 클래스
			e.printStackTrace();
			bindingResult.reject("registerFailed","이미 등록된 사용자입니다.");
			//reject(오류 코드,오류 메세지)
			return "/member/register";
		}catch (IOException e) {
		    e.printStackTrace();
		    bindingResult.reject("fileError", "파일 처리 중 오류가 발생했습니다.");
		    return "/member/register";
		} catch(Exception e) {
			e.printStackTrace();
			bindingResult.reject("registerFailed",e.getMessage());
			return "/member/register";
		}
		
		return "redirect:/";
	}

    @GetMapping("/mypage")
    public String getMypage() {
        return "/member/mypage";
    }
    
    @GetMapping("/enter_password")
    public String getEnterPassword() {
    	return "/member/enter_password";
    }
    
    @GetMapping("/update_profile")
    public String getUpdateProfile() {
    	return  "/member/update_profile";
    }
}
