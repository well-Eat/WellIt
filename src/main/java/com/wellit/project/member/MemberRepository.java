package com.wellit.project.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String>{
	Member findByMemberId(String memberId);
	boolean existsByMemberId(String memberId);
	Optional<Member> findByMemberNameAndMemberEmail(String memberName,String memberEmail);
	Optional<Member> findByMemberEmail(String memberEmail);
	Optional<Member> findByMemberIdAndMemberNameAndMemberEmail(String memberId, String memberName,String memberEmail);
	Optional<Member> findByResetToken(String token);
	boolean existsByMemberEmail(String email);
		
}