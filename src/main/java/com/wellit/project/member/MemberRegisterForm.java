package com.wellit.project.member;


import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterForm {
	
	@NotBlank(message = "아이디를 입력해주세요")
    @Length(min = 4, max = 12, message = "아이디는 4자에서 12자 이내여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문과 숫자만 가능합니다.")
	private String memberId;
	
	@NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$", message = "비밀번호는 최소 6자 이상이어야 하며, 영문과 숫자를 포함해야 합니다.")
    private String memberPassword;
	
	@NotBlank(message = "비밀번호 확인을 입력해주세요")
	private String memberPassword2;
	
	@NotBlank(message = "사용자의 성함을 입력해주세요")
	@Length(max = 10, message = "사용자의 성함은 최대 10자 까지만 입력해주세요")
	@Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "이름은 특수기호 없이 입력해주세요")
	private String memberName;
	
	@NotBlank(message = "닉네임을 입력해주세요")
    @Length(max = 10, message = "닉네임은 최대 10자 까지만 입력해주세요")
    @Pattern(regexp = "^[a-zA-Z가-힣0-9]*$", message = "닉네임은 특수기호 없이 입력해주세요")
    private String memberAlias;
	 
	@NotBlank(message = "이메일을 입력해주세요")
	@Email(message = "유효한 이메일 주소를 입력해주세요")
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