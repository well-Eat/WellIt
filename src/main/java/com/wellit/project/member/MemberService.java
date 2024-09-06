package com.wellit.project.member;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.wellit.project.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public List<Member> findAllMembers() {
		return memberRepository.findAll();
	}

	private static final String UPLOAD_DIR = "src/main/resources/static/imgs/member/";

	public Member create(String memberId, String memberPassword, String memberName, String memberAlias,
			String memberEmail, String memberPhone, String memberAddress, String memberBirth, String memberGender,
			String memberVeganType, String zipcode, String roadAddress, String addressDetail, String birth_year,
			String birth_month, String birth_day, MultipartFile imageFile) throws IOException {

		Member member = new Member();
		member.setMemberId(memberId);
		member.setMemberPassword(passwordEncoder.encode(memberPassword));
		member.setMemberName(memberName);
		member.setMemberAlias(memberAlias);
		member.setMemberEmail(memberEmail);
		member.setMemberPhone(memberPhone);
		member.setMemberAddress(memberAddress);
		member.setMemberBirth(memberBirth);
		member.setMemberGender(memberGender);
		member.setMemberVeganType(memberVeganType);
		member.setZipcode(zipcode);
		member.setRoadAddress(roadAddress);
		member.setAddressDetail(addressDetail);
		member.setBirth_year(birth_year);
		member.setBirth_month(birth_month);
		member.setBirth_day(birth_day);

		// 회원 프로필 이미지 등록한다면 해당 이미지 이름도 DB저장
		if (!imageFile.isEmpty()) {
			// 고유한 이미지 이름 생성
			String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
			// 파일 저장 경로
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			// 디렉토리가 없으면 생성
			Files.createDirectories(filePath.getParent());
			// 파일 저장
			Files.write(filePath, imageFile.getBytes());
			// 사용자 엔티티에 이미지 경로 설정
			member.setImageFile("/imgs/member/" + fileName);
		}

		return memberRepository.save(member);
	}

	// 로그인한 사용자명을 알 수 있는 메소드
	public Member getMember(String memberId) {
		Optional<Member> member = this.memberRepository.findByMemberId(memberId);
//			
		if (member.isPresent()) {
			return member.get();
		} else {
			throw new DataNotFoundException("해당 회원이 없습니다.");
		}
	}

	// 아이디 중복 체크
	public boolean isIdExists(String memberId) {
		return memberRepository.existsByMemberId(memberId);
	}

	// 회원 정보 수정 로직
	public void updateMember(Member member, String newPassword, String newName, String newAlias, String newEmail,
	        String newPhone, String newAddress, String newBirth, String newGender, String newVeganType,
	        String newZipcode, String newRoadAddress, String newAddressDetail, String newBirthYear,
	        String newBirthMonth, String newBirthDay, MultipartFile imageFile, String existingImagePath)
	        throws IOException {

	    // 비밀번호 변경
	    if (newPassword != null && !newPassword.isEmpty()) {
	        String encodedPassword = passwordEncoder.encode(newPassword);
	        member.setMemberPassword(encodedPassword);
	    }

	    // 이름, 닉네임, 이메일, 전화번호 등 업데이트
	    member.setMemberName(newName);
	    member.setMemberAlias(newAlias);
	    member.setMemberEmail(newEmail);
	    member.setMemberPhone(newPhone);
	    member.setMemberAddress(newAddress);
	    member.setBirth_year(newBirthYear);
	    member.setBirth_month(newBirthMonth);
	    member.setBirth_day(newBirthDay);
	    member.setMemberBirth(newBirth);
	    member.setMemberGender(newGender);
	    member.setMemberVeganType(newVeganType);
	    member.setZipcode(newZipcode);
	    member.setRoadAddress(newRoadAddress);
	    member.setAddressDetail(newAddressDetail);

	    // 프로필 이미지 업데이트 처리
	    if (!imageFile.isEmpty()) {
	        // 새 이미지 파일을 저장
	        String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
	        Path filePath = Paths.get(UPLOAD_DIR, fileName);
	        Files.createDirectories(filePath.getParent());
	        Files.write(filePath, imageFile.getBytes());
	        // 새 이미지 경로 설정
	        member.setImageFile("/imgs/member/" + fileName);
	    } else {
	        // 기존 이미지 경로를 유지
	        member.setImageFile(existingImagePath);
	    }

	    // 변경된 회원 정보를 DB에 저장
	    memberRepository.save(member);
	}
}