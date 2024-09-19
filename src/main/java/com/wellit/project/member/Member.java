package com.wellit.project.member;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "member")
@Getter
@Setter
public class Member {
	
	 	@Id
	    private String memberId; //아이디
	 	
	 	@Column
	 	private String memberPassword; //비밀번호

	 	@Column
	    private String memberName; //이름

	 	@Column
	 	private String memberAlias; //닉네임

	 	@Column(unique = true)
	    private String memberEmail;
	    
	 	@Column
	    private String memberPhone;
	    
	    @Column
	    private String zipcode; //우편번호
		private String roadAddress; //도로명 주소
		private String addressDetail; //상세 주소
	    private String memberAddress; //도로명 + 상세
	    
	    @Column
	    private String birth_year;
	    private String birth_month;
	    private String birth_day;
	    private String memberBirth; //생일
	    
	    @Column
	    private String memberGender; 

	    @Column
	    private String memberVeganType; //비건 타입
	    
	    @Column
	    private String ImageFile; //이미지 링크
	    
	    private LocalDateTime memberRegDate; //가입 일자
	    private LocalDateTime memberUpdateDate; //수정 일자
	    
	    @PrePersist
	    protected void onCreate() {
	        this.memberRegDate = LocalDateTime.now();
	        this.memberUpdateDate = LocalDateTime.now();
	    }

	    @PreUpdate
	    protected void onUpdate() {
	        this.memberUpdateDate = LocalDateTime.now();
	    }
	    
	    private String resetToken; // 비밀번호 재설정 토큰을 저장할 필드
	    
	    private Integer mileage;
	   
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        // 사용자 권한 설정 (예: ROLE_USER)
	        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
	    }
	    
	    private String memberType;
	    
}

