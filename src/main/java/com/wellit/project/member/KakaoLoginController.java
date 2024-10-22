package com.wellit.project.member;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.wellit.project.CustomUserAlreadyExistsException;
import com.wellit.project.order.Cart;
import com.wellit.project.order.CartRepository;

import org.springframework.security.core.GrantedAuthority;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	private final MemberRepository memberRepository;
	private final AuthenticationManager authenticationManager; // 로그인 처리를 위한 AuthenticationManager
	private final CartRepository cartRepository;

	@GetMapping("/callback")
	public String callback(@RequestParam(value = "code", required = false) String code,
			RedirectAttributes redirectAttributes, HttpServletRequest request, HttpSession session) {
		try {

			if (code == null || code.isEmpty()) {
				// code 파라미터가 없는 경우 처리
				redirectAttributes.addFlashAttribute("errorMessage", "인증 코드가 없습니다. 다시 시도해주세요.");
				return "redirect:/member/login"; // 로그인 페이지로 리다이렉트
			}

			// 현재 사용자의 인증 상태 확인
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			// 로그인된 사용자가 있으며, 익명 사용자가 아닌 경우
			if (authentication != null && authentication.isAuthenticated()
					&& !(authentication instanceof AnonymousAuthenticationToken)) {
				return "redirect:/member/mypage"; // 마이페이지로 리다이렉트
			}

			// 1. 카카오에서 accessToken을 얻음
			String accessToken = kakaoService.getAccessTokenFromKakao(code);

			// 액세스 토큰 세션에 저장
			session.setAttribute("accessToken", accessToken);

			// 2. accessToken을 사용하여 사용자 정보를 DB에 저장 또는 가져오기
			Member member = kakaoService.registerKakaoUser(accessToken);
			session.setAttribute("UserId", member.getMemberId());

			// Cart 연동 생성
			if (member.getCart() == null) {
				Cart cart = new Cart(member);
				member.setCart(cart);
				cartRepository.save(cart); // 카트도 DB에 저장
			}

			// 3. 신규 회원의 경우, 추가 정보 입력 폼으로 리다이렉트
			if (member.getMemberPhone() == null || member.getMemberAddress() == null) {
				redirectAttributes.addFlashAttribute("message", "회원 가입이 완료되었습니다. 추가 정보를 입력해주세요.");
				redirectAttributes.addFlashAttribute("KakaoSignupForm", new KakaoSignupForm()); // 추가 정보 입력 폼 객체
				return "redirect:/member/kakao_signup"; // 나머지 정보 입력 폼 페이지로 이동
			}

			// 사용자 인증 처리
			authenticateUser(member, request);

			return "redirect:/"; // 홈 페이지로 이동

		} catch (CustomUserAlreadyExistsException e) {
			// 기존 회원일 경우, 로그인 처리 후 메인 페이지로 리다이렉트
			String kakaoUserId = e.getKakaoUserId(); // 커스텀 예외에서 카카오 사용자 ID 추출
			Member existingMember = memberRepository.findByMemberId(kakaoUserId);

			if (code == null || code.isEmpty()) {
				// code 파라미터가 없는 경우 처리
				redirectAttributes.addFlashAttribute("errorMessage", "인증 코드가 없습니다. 다시 시도해주세요.");
				return "redirect:/member/login"; // 로그인 페이지로 리다이렉트
			}

			// 현재 사용자의 인증 상태 확인
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			// 로그인된 사용자가 있으며, 익명 사용자가 아닌 경우
			if (authentication != null && authentication.isAuthenticated()
					&& !(authentication instanceof AnonymousAuthenticationToken)) {
				return "redirect:/member/mypage"; // 마이페이지로 리다이렉트
			}

			if (existingMember == null) {
				throw new NoSuchElementException("Member not found");
			}
			session.setAttribute("UserId", existingMember.getMemberId());

			// Cart 연동 생성 (기존 회원일 경우도 확인)
			if (existingMember.getCart() == null) {
				Cart cart = new Cart(existingMember);
				existingMember.setCart(cart);
				cartRepository.save(cart); // 카트도 DB에 저장
			}

			if (existingMember.getMemberPhone() == null || existingMember.getMemberAddress() == null) {

				redirectAttributes.addFlashAttribute("message", "추가 정보를 입력해주세요.");
				redirectAttributes.addFlashAttribute("memberId", existingMember.getMemberId());
				redirectAttributes.addFlashAttribute("KakaoSignupForm", new KakaoSignupForm()); // 추가 정보 입력 폼 객체
				return "redirect:/member/kakao_signup"; // 나머지 정보 입력 폼 페이지로 이동
			} else {
				// 사용자 인증 처리
				authenticateUser(existingMember, request);
			}

			return "redirect:/"; // 홈 페이지로 이동
		}
	}

	private void authenticateUser(Member member, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				member.getMemberId(), null, member.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		// 인증된 사용자 세션 생성
		HttpSession session = request.getSession();
		session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		request.getSession().setAttribute("UserId", member.getMemberId());

	}

	// 카카오 로그인 후 callback (POST 방식) - 액세스 토큰 직접 처리
	@PostMapping("/callback")
	public ResponseEntity<String> kakaoLogin(@RequestParam String accessToken) {

		// 액세스 토큰을 사용해 사용자 정보를 저장
		Member member = kakaoService.registerKakaoUser(accessToken);

		// 사용자 정보를 성공적으로 저장하면 성공 메시지 리턴
		return ResponseEntity.ok("Kakao user registered: " + member.getMemberAlias());
	}

	@GetMapping("/member/kakao_signup")
	public String geKakaoSignup(HttpSession session, Model model) {
		String userId = (String) session.getAttribute("UserId");

		// 현재 사용자의 인증 상태 확인
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 로그인된 사용자가 있으며, 익명 사용자가 아닌 경우
		if (authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken)) {
			return "redirect:/member/mypage"; // 마이페이지로 리다이렉트
		}

		if (userId != null) {
			log.info("Session UserId: {}", userId);
			// UserId로 Member 정보를 조회
			Member member = memberRepository.findByMemberId(userId);
			if (member != null) {
				model.addAttribute("member", member);
			} else {
				log.warn("No member found for UserId: {}", userId);
				// 사용자가 없으면 새 폼을 제공
				model.addAttribute("member", new KakaoSignupForm());
			}
		} else {
			log.warn("No UserId found in session");
			// 세션에 UserId가 없으면 새 폼을 제공
			model.addAttribute("member", new KakaoSignupForm());
		}

		return "member/kakao_signup";
	}

	// 추가 정보 입력 폼 처리 (회원 가입 완료)
	@PostMapping("/member/kakao_signup")
	public String kakaoSignup(@Valid @ModelAttribute("member") KakaoSignupForm kakaoSignupForm,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

		if (bindingResult.hasErrors()) {
			// 에러 메시지를 추출하여 줄바꿈으로 구분된 문자열로 변환
			String errorMessages = bindingResult.getAllErrors().stream().map(error -> {
				// 필드 오류 메시지
				if (error instanceof FieldError) {
					FieldError fieldError = (FieldError) error;
					return fieldError.getDefaultMessage();
				}
				// 글로벌 오류 메시지
				return error.getDefaultMessage();
			}).collect(Collectors.joining("\n")); // 줄바꿈으로 메시지 구분

			// 에러 메시지를 모델에 추가
			model.addAttribute("errorMessage", errorMessages);
			model.addAttribute("member", kakaoSignupForm); // 현재 폼 객체를 모델에 추가
			System.out.println(kakaoSignupForm);
			System.out.println("Member Alias: " + kakaoSignupForm.getMemberAlias());

			// 추가 정보 입력 폼 페이지로 이동
			return "member/kakao_signup";
		}

		Boolean isEmailVerified = (Boolean) session.getAttribute("emailVerified");
		if (isEmailVerified == null || !isEmailVerified) {
			model.addAttribute("errorMessage", "이메일 인증이 완료되지 않았습니다.");
			return "/member/kakao_signup";
		}

		try {
			// 카카오 로그인으로 저장된 회원을 업데이트
			Member member = kakaoService.updateKakaoMember(kakaoSignupForm);

			// 회원 가입 완료 후 리다이렉트
			model.addAttribute("loginMessage", "회원가입이 완료되었습니다. 로그인해주세요");
			return "/member/login";

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("updateFailed", "이미 등록된 사용자 정보입니다.");
			model.addAttribute("errorMessage", "이미 등록된 사용자 정보입니다.");
			model.addAttribute("member", kakaoSignupForm);
			return "member/kakao_signup";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("updateFailed", e.getMessage());
			model.addAttribute("errorMessage", e.getMessage());
			model.addAttribute("member", kakaoSignupForm);
			return "member/kakao_signup";
		}
	}

	@GetMapping("/member/update_kakao")
	public String getUpdateKakao(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Authentication: " + authentication); // 디버깅용 로그

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			// principal이 UserDetails 타입이면 /member/enterpassword로 리다이렉트
			if (principal instanceof UserDetails) {
				return "redirect:/member/enter_password";
			}

			String memberId = (String) principal;
			Member member = memberService.getMember(memberId);

			model.addAttribute("member", member);
			return "member/update_kakao"; // 경로 수정
		}

		// 인증 정보가 없거나 인증되지 않은 경우 로그인 페이지로 리다이렉트
		return "redirect:/member/login";
	}

	@PostMapping("/member/update_kakao")
	public String updateKakao(@Valid KakaoUpdateForm kakaoUpdateForm, BindingResult bindingResult,
			@RequestParam("imageFile") MultipartFile imageFile, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			// 에러 메시지를 추출하여 줄바꿈으로 구분된 문자열로 변환
			String errorMessages = bindingResult.getAllErrors().stream().map(error -> {
				// 필드 오류 메시지
				if (error instanceof FieldError) {
					FieldError fieldError = (FieldError) error;
					return fieldError.getDefaultMessage();
				}
				// 글로벌 오류 메시지
				return error.getDefaultMessage();
			}).collect(Collectors.joining("\n")); // 줄바꿈으로 메시지 구분

			// 에러 메시지를 모델에 추가
			model.addAttribute("errorMessage", errorMessages);
			model.addAttribute("member", kakaoUpdateForm); // 현재 폼 객체를 모델에 추가

			// 프로필 수정 폼 페이지로 이동
			return "member/update_profile"; // 경로 수정
		}
		

		// 년도 값이 null인 경우 기존 값을 유지
		if (kakaoUpdateForm.getBirth_year() == null || kakaoUpdateForm.getBirth_year().isEmpty()) {
			kakaoUpdateForm.setBirth_year(kakaoUpdateForm.getBirth_year());
		}

		// 월 값이 null인 경우 기존 값을 유지
		if (kakaoUpdateForm.getBirth_month() == null || kakaoUpdateForm.getBirth_month().isEmpty()) {
			kakaoUpdateForm.setBirth_month(kakaoUpdateForm.getBirth_month());
		}

		// 일 값이 null인 경우 기존 값을 유지
		if (kakaoUpdateForm.getBirth_day() == null || kakaoUpdateForm.getBirth_day().isEmpty()) {
			kakaoUpdateForm.setBirth_day(kakaoUpdateForm.getBirth_day());
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			// Principal이 String 타입으로 가정
			if (principal instanceof String) {
				String memberId = (String) principal;
				Member existingMember = memberService.getMember(memberId);
				
				// 이메일 변경 확인 및 이메일 인증 처리
				if (!existingMember.getMemberEmail().equals(kakaoUpdateForm.getMemberEmail())) {
					Boolean isEmailVerified = (Boolean) session.getAttribute("emailVerified");
					if (isEmailVerified == null || Boolean.FALSE.equals(isEmailVerified)) {
						model.addAttribute("errorMessage", "이메일 인증이 완료되지 않았습니다.");
						model.addAttribute("member", kakaoUpdateForm); // 여기에 추가
						return "/member/update_profile";
					}
				}

				try {
					// 기존 이미지 경로를 폼에서 가져와서 전달
					String existingImagePath = existingMember.getImageFile();
					int mileage = existingMember.getMileage();

					memberService.updateMember(existingMember, kakaoUpdateForm.getMemberPassword(),
							kakaoUpdateForm.getMemberName(), kakaoUpdateForm.getMemberAlias(),
							kakaoUpdateForm.getMemberEmail(), kakaoUpdateForm.getMemberPhone(),
							kakaoUpdateForm.getMemberAddress(), kakaoUpdateForm.getMemberBirth(),
							kakaoUpdateForm.getMemberGender(), kakaoUpdateForm.getMemberVeganType(),
							kakaoUpdateForm.getZipcode(), kakaoUpdateForm.getRoadAddress(),
							kakaoUpdateForm.getAddressDetail(), kakaoUpdateForm.getBirth_year(),
							kakaoUpdateForm.getBirth_month(), kakaoUpdateForm.getBirth_day(), imageFile,
							existingImagePath, mileage);

					redirectAttributes.addFlashAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");

				} catch (DataIntegrityViolationException e) {
					e.printStackTrace();
					bindingResult.reject("updateFailed", "이미 등록된 사용자 정보입니다.");
					model.addAttribute("member", kakaoUpdateForm); // 현재 폼 객체를 모델에 추가
					return "member/update_profile"; // 경로 수정
				} catch (IOException e) {
					e.printStackTrace();
					bindingResult.reject("fileError", "파일 처리 중 오류가 발생했습니다.");
					model.addAttribute("member", kakaoUpdateForm); // 현재 폼 객체를 모델에 추가
					return "member/update_profile"; // 경로 수정
				} catch (Exception e) {
					e.printStackTrace();
					bindingResult.reject("updateFailed", e.getMessage());
					model.addAttribute("member", kakaoUpdateForm); // 현재 폼 객체를 모델에 추가
					return "member/update_profile"; // 경로 수정
				}
			} else {
				return "redirect:/member/login";
			}

			model.addAttribute("updateMessage", "회원 수정이 완료되었습니다. 다시 로그인해주세요.");

		}
		session.invalidate();
		return "redirect:/member/login"; // 리다이렉트
	}

	@GetMapping("/member/delete_kakao")
	public String getDeleteKakao(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("Authentication: " + authentication); // 디버깅용 로그

		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			// principal이 UserDetails 타입이면 /member/enterpassword로 리다이렉트
			if (principal instanceof UserDetails) {
				return "redirect:/member/enter_password";
			}

			String memberId = (String) principal;
			Member member = memberService.getMember(memberId);

			model.addAttribute("member", member);
			return "member/delete_kakao";
		}

		// 인증 정보가 없거나 인증되지 않은 경우 로그인 페이지로 리다이렉트
		return "redirect:/member/login";
	}

	@PostMapping("/member/delete_kakao")
	public String deleteMember(HttpSession session, RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			String memberId = (String) principal;

			// 회원 정보 조회
			Member member = memberService.getMember(memberId);

			if (member != null) {
				// 세션에서 카카오 액세스 토큰 가져오기
				String kakaoAccessToken = (String) session.getAttribute("accessToken");
				System.out.println("회원탈퇴 액세스 토큰" + kakaoAccessToken);

				if (kakaoAccessToken != null) {
					// 카카오 계정 연결 해제
					unlinkKakaoAccount(kakaoAccessToken);
				}

				// 애플리케이션 내 회원 삭제
				memberService.deleteMember(memberId);

				// 세션 무효화
				session.invalidate();

				redirectAttributes.addFlashAttribute("successMessage", "회원 탈퇴가 완료되었습니다.");
				return "redirect:/"; // 홈 페이지로 리다이렉트
			}
		}

		// 인증 정보가 없거나 인증되지 않은 경우 홈 페이지로 리다이렉트
		return "redirect:/";
	}

	private void unlinkKakaoAccount(String kakaoAccessToken) {
		String url = "https://kapi.kakao.com/v1/user/unlink";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + kakaoAccessToken); // 액세스 토큰을 헤더에 추가

		HttpEntity<String> requestEntity = new HttpEntity<>(headers);

		RestTemplate restTemplate = new RestTemplate();
		try {
			// 카카오 API에 POST 요청을 보냄
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				System.out.println("카카오 연결이 성공적으로 해제되었습니다.");
			} else {
				System.out.println("카카오 연결 해제 실패: " + response.getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("카카오 연결 해제 요청 중 오류가 발생했습니다.");
		}
	}

	@GetMapping("/member/kakao-logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String accessToken = (String) request.getSession().getAttribute("accessToken");

		// 1. 카카오 API를 통해 액세스 토큰을 만료시킵니다.
		if (accessToken != null) {
			try {
				kakaoService.logoutKakaoUser(accessToken);
			} catch (Exception e) {
				// 카카오 로그아웃 실패 시 로그 출력
				log.error("카카오 로그아웃 실패: {}", e.getMessage());
			}
		} else {
			log.error("액세스 토큰이 세션에 없습니다.");
			// 액세스 토큰이 없으면 애플리케이션 로그아웃만 수행하고 종료
			SecurityContextHolder.clearContext();
			request.getSession().invalidate();
			return "redirect:/";
		}

		// 2. 카카오 계정 로그아웃을 위해 리다이렉트합니다.
		String kakaoLogoutUrl = kakaoService.redirectToKakaoAccountLogout();
		response.sendRedirect(kakaoLogoutUrl);

		// 애플리케이션 로그아웃 처리
		SecurityContextHolder.clearContext();
		request.getSession().invalidate();

		// 카카오 계정 로그아웃 후 리다이렉션 처리
		return null; // 리다이렉트가 이미 호출되었으므로 null을 반환합니다.
	}

	@PostMapping("/clear-session")
	@ResponseBody
	public ResponseEntity<String> clearSession(HttpSession session) {
		// 세션에서 사용자 ID를 먼저 가져옵니다.
		String memberId = (String) session.getAttribute("UserId");
		System.out.println("/clear-session 멤버아이디" + memberId);
		// 세션 무효화 전에 데이터베이스에서 삭제 작업을 수행합니다.
		if (memberId != null) {
			memberService.deleteMember(memberId);
		}

		return ResponseEntity.ok("Session and DB data cleared");

	}

}