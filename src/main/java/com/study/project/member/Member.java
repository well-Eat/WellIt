package com.study.project.member;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

	 	@Column
	    private String memberEmail;
	    
	 	@Column
	    private String memberPhone;
	    
	    @Column
	    private String memberAddress;
	    
	    @Column
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
}
