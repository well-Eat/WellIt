package com.wellit.project.member;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.wellit.project.email.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

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
    public String register(@Valid MemberRegisterForm memberRegisterForm, BindingResult bindingResult, 
                           @RequestParam("imageFile") MultipartFile imageFile, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.toString());
            });
            return "/member/register";
        }

        Boolean isIdVerified = (Boolean) session.getAttribute("idVerified");
        if (Boolean.FALSE.equals(isIdVerified) || isIdVerified==null) {
            model.addAttribute("errorMessage", "아이디 중복확인이 완료되지 않았습니다. 회원가입을 진행할 수 없습니다.");
            return "/member/register";
        }
        Boolean isEmailVerified = (Boolean) session.getAttribute("emailVerified");
        if (Boolean.FALSE.equals(isEmailVerified) || isEmailVerified==null) {
            model.addAttribute("errorMessage", "이메일 인증이 완료되지 않았습니다. 회원가입을 진행할 수 없습니다.");
            return "/member/register";
        }
        if (!memberRegisterForm.getMemberPassword().equals(memberRegisterForm.getMemberPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 서로 일치하지 않습니다.");
            return "/member/register";
        }

        try {
            memberService.create(memberRegisterForm.getMemberId(), memberRegisterForm.getMemberPassword(), 
                                memberRegisterForm.getMemberName(), memberRegisterForm.getMemberAlias(), 
                                memberRegisterForm.getMemberEmail(), memberRegisterForm.getMemberPhone(), 
                                memberRegisterForm.getMemberAddress(), memberRegisterForm.getMemberBirth(), 
                                memberRegisterForm.getMemberGender(), memberRegisterForm.getMemberVeganType(), 
                                imageFile);
            
            session.removeAttribute("emailVerified");
            session.removeAttribute("verificationCode");
            session.removeAttribute("idVerified");
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("registerFailed", "이미 등록된 사용자입니다.");
            return "/member/register";
        } catch (IOException e) {
            e.printStackTrace();
            bindingResult.reject("fileError", "파일 처리 중 오류가 발생했습니다.");
            return "/member/register";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("registerFailed", e.getMessage());
            return "/member/register";
        }

        return "redirect:/";
    }
	
	@PostMapping("/check-id")
	public ResponseEntity<IdCheckResponse> checkId(@RequestBody IdCheckRequest request, HttpSession session) {
	    boolean exists = memberService.isIdExists(request.getMemberId());
	    IdCheckResponse response = new IdCheckResponse();
	    response.setExists(exists);
	    session.setAttribute("idVerified", true); // 인증 완료 상태 저장
	    return ResponseEntity.ok().body(response);
	}
	
	@PostMapping("/send-email")
	public ResponseEntity<String> sendEmail(@RequestParam("email") String email, HttpSession session) {
	    String verificationCode = generateVerificationCode();
	    session.setAttribute("verificationCode", verificationCode);
	    String subject = "Wellit 인증코드";
	    String text = "인증코드는 " + verificationCode + "입니다.";
	    emailService.sendSimpleMessage(email, subject, text);
	    
	    return ResponseEntity.ok("인증 이메일이 전송되었습니다.");
	}

	@PostMapping("/verify-code")
	public ResponseEntity<String> verifyCode(@RequestParam("code") String code, HttpSession session) {
	    String storedCode = (String) session.getAttribute("verificationCode");
	    if (storedCode != null && storedCode.equals(code)) {
	        session.setAttribute("emailVerified", true); // 인증 완료 상태 저장
	        return ResponseEntity.ok("인증 성공");
	    } else {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 실패");
	    }
	}

	    private String generateVerificationCode() {
	        Random random = new Random();
	        int code = random.nextInt(900000) + 100000; // 6 digit code
	        return String.valueOf(code);
	    }
	
	@Getter
	@Setter
	 public static class IdCheckRequest {
	   private String memberId;
	 }
	
	@Getter
	@Setter
	 public static class IdCheckResponse {
	   private boolean exists;
	}

	@GetMapping("/mypage")
    public String getMypage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String memberId = userDetails.getUsername(); // 일반적으로 username이 memberId와 같음
            Member member = memberService.getMember(memberId);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedRegDate = member.getMemberRegDate().format(formatter);
            
            model.addAttribute("member", member);
            model.addAttribute("formattedRegDate", formattedRegDate);
        }
        return "member/mypage";
    }
	
    
    @GetMapping("/enter_password")
    public String getEnterPassword(Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String memberId = userDetails.getUsername(); // 일반적으로 username이 memberId와 같음
            Member member = memberService.getMember(memberId);
        }
    	return "/member/enter_password";
    }
    
    @GetMapping("/update_profile")
    public String getUpdateProfile() {
    	return  "/member/update_profile";
    }
}