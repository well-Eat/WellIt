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
			String memberVeganType, MultipartFile imageFile) throws IOException {

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
	//아이디 중복 체크
	public boolean isIdExists(String memberId) {
        return memberRepository.existsByMemberId(memberId);
    }
}