package com.wellit.project.member;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
    private final PasswordEncoder passwordEncoder;

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
                                memberRegisterForm.getZipcode(), memberRegisterForm.getRoadAddress(),
                                memberRegisterForm.getAddressDetail(), memberRegisterForm.getBirth_year(),
                                memberRegisterForm.getBirth_month(), memberRegisterForm.getBirth_day(),
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
	    
	    // 인증 객체가 null이 아니고, 인증된 유저 정보가 존재하는지 확인
	    if (authentication != null && authentication.isAuthenticated() 
	            && authentication.getPrincipal() instanceof UserDetails) {
	        
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        String memberId = userDetails.getUsername(); // 일반적으로 username이 memberId와 같음
	        
	        // 회원 정보 조회
	        Member member = memberService.getMember(memberId);

	        // 모델에 회원 정보를 추가하여 뷰에 전달
	        model.addAttribute("member", member);
	        
	    } else {
	        // 인증 정보가 없는 경우 로그인 페이지로 리다이렉트
	        return "redirect:/login"; 
	    }

	    // 비밀번호 입력 페이지로 이동
	    return "/member/enter_password";
	}
	
	@PostMapping("/enter_password")
	public String checkPassword(@RequestParam("enterpassword") String enterPassword, Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        String memberId = userDetails.getUsername(); // 일반적으로 username이 memberId와 같음
	        Member member = memberService.getMember(memberId);

	        // 사용자가 입력한 비밀번호와 데이터베이스에 저장된 비밀번호를 비교
	        if (passwordEncoder.matches(enterPassword, member.getMemberPassword())) {
	            // 비밀번호가 일치하면 프로필 수정 페이지로 이동
	            return "redirect:/member/update_profile";
	        } else {
	            // 비밀번호가 일치하지 않으면 에러 메시지를 모델에 추가
	            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
	            model.addAttribute("member", member); // 다시 member 정보도 전달
	            return "/member/enter_password";
	        }
	    }

	    return "redirect:/login"; // 인증 정보가 없으면 로그인 페이지로 리다이렉트
	}
    
    @GetMapping("/update_profile")
    public String getUpdateProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    
	    // 인증 객체가 null이 아니고, 인증된 유저 정보가 존재하는지 확인
	    if (authentication != null && authentication.isAuthenticated() 
	            && authentication.getPrincipal() instanceof UserDetails) {
	        
	        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	        String memberId = userDetails.getUsername(); // 일반적으로 username이 memberId와 같음
	        
	        // 회원 정보 조회
	        Member member = memberService.getMember(memberId);

	        // 모델에 회원 정보를 추가하여 뷰에 전달
	        model.addAttribute("member", member);
	        
	    } else {
	        // 인증 정보가 없는 경우 로그인 페이지로 리다이렉트
	        return "redirect:/login"; 
	    }
    	return  "/member/update_profile";
    }
    
    @PostMapping("/update_profile")
    public String updateProfile(@Valid MemberRegisterForm memberRegisterForm, BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile imageFile, HttpSession session, Model model) {
    	if (bindingResult.hasErrors()) {
            // 에러 메시지를 추출하여 줄바꿈으로 구분된 문자열로 변환
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(error -> {
                        // 필드 오류 메시지
                        if (error instanceof FieldError) {
                            FieldError fieldError = (FieldError) error;
                            return fieldError.getDefaultMessage();
                        }
                        // 글로벌 오류 메시지
                        return error.getDefaultMessage();
                    })
                    .collect(Collectors.joining("\n"));  // 줄바꿈으로 메시지 구분

            // 에러 메시지를 모델에 추가
            model.addAttribute("errorMessage", errorMessages);
            model.addAttribute("member", memberRegisterForm);  // 현재 폼 객체를 모델에 추가

            // 프로필 수정 폼 페이지로 이동
            return "member/update_profile";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof UserDetails) {
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String memberId = userDetails.getUsername();

            Member existingMember = memberService.getMember(memberId);
            model.addAttribute("member", memberRegisterForm);  // 여기에 추가
            // 비밀번호 일치 여부 확인
            if (!memberRegisterForm.getMemberPassword().equals(memberRegisterForm.getMemberPassword2())) {
                bindingResult.rejectValue("memberPassword2", "passwordInCorrect", "2개의 패스워드가 서로 일치하지 않습니다.");
                model.addAttribute("errorMessage", "2개의 패스워드가 서로 일치하지 않습니다.");
                model.addAttribute("member", memberRegisterForm);  // 여기에 추가
                return "/member/update_profile";
            }

            // 이메일 변경 확인 및 이메일 인증 처리
            if (!existingMember.getMemberEmail().equals(memberRegisterForm.getMemberEmail())) {
                Boolean isEmailVerified = (Boolean) session.getAttribute("emailVerified");
                if (Boolean.FALSE.equals(isEmailVerified) || isEmailVerified == null) {
                    model.addAttribute("errorMessage", "이메일 인증이 완료되지 않았습니다.");
                    model.addAttribute("member", memberRegisterForm);  // 여기에 추가
                    return "/member/update_profile";
                }
            }

            try {
                // 기존 이미지 경로를 폼에서 가져와서 전달
                String existingImagePath = existingMember.getImageFile();

                memberService.updateMember(existingMember, memberRegisterForm.getMemberPassword(),
                                           memberRegisterForm.getMemberName(), memberRegisterForm.getMemberAlias(),
                                           memberRegisterForm.getMemberEmail(), memberRegisterForm.getMemberPhone(),
                                           memberRegisterForm.getMemberAddress(), memberRegisterForm.getMemberBirth(),
                                           memberRegisterForm.getMemberGender(), memberRegisterForm.getMemberVeganType(),
                                           memberRegisterForm.getZipcode(), memberRegisterForm.getRoadAddress(),
                                           memberRegisterForm.getAddressDetail(), memberRegisterForm.getBirth_year(),
                                           memberRegisterForm.getBirth_month(), memberRegisterForm.getBirth_day(),
                                           imageFile, existingImagePath);

                session.removeAttribute("emailVerified");

            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                bindingResult.reject("updateFailed", "이미 등록된 사용자 정보입니다.");
                model.addAttribute("member", memberRegisterForm);  // 여기에 추가
                return "/member/update_profile";
            } catch (IOException e) {
                e.printStackTrace();
                bindingResult.reject("fileError", "파일 처리 중 오류가 발생했습니다.");
                model.addAttribute("member", memberRegisterForm);  // 여기에 추가
                return "/member/update_profile";
            } catch (Exception e) {
                e.printStackTrace();
                bindingResult.reject("updateFailed", e.getMessage());
                model.addAttribute("member", memberRegisterForm);  // 여기에 추가
                return "/member/update_profile";
            }
        } else {
            return "redirect:/login";
        }

        return "redirect:/member/mypage";
    }

}