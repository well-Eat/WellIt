package com.wellit.project.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberSecurityService implements UserDetailsService{
	
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
	    System.out.println("Requested memberId: " + memberId);  // 요청된 memberId 출력
	    Member member = this.memberRepository.findByMemberId(memberId);
	    System.out.println(member);
	    
	    // null 체크
	    if (member == null) {
	        throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.");
	    }
	    
	    List<GrantedAuthority> authorities = new ArrayList<>();
	    if ("admin".equals(memberId)) {
	        authorities.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
	    } else {
	        authorities.add(new SimpleGrantedAuthority(MemberRole.USER.getValue()));
	    }
	    
	    return new User(member.getMemberId(), member.getMemberPassword(), authorities);
	}
}