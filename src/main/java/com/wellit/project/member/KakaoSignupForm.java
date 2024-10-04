package com.wellit.project.member;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoSignupForm {
	
	@NotBlank(message = "사용자의 성함을 입력해주세요")
	@Length(max = 10, message = "사용자의 성함은 최대 10자 까지만 입력해주세요")
	@Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "이름은 특수기호 없이 입력해주세요")
	private String memberName;
	
	@NotBlank(message = "닉네임을 입력해주세요")
    @Length(max = 10, message = "닉네임은 최대 10자 까지만 입력해주세요")
	private String memberAlias;
	
	private String memberId;
	
	
	private String memberEmail;
	
	@NotBlank(message = "전화번호를 입력해주세요")
	@Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 숫자만 가능합니다")
	private String memberPhone;
	
	@NotBlank(message = "주소를 입력해주세요")
	private String memberAddress;   
	
	private String zipcode;	
	private String roadAddress;	
	private String addressDetail;
    
    private String memberBirth;      
    private String birth_year;
    private String birth_month;   
    private String birth_day;
    
    private String memberGender;
    
    private String memberVeganType;
    
    private MultipartFile imageFile;
}
