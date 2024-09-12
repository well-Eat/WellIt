package com.wellit.project.order;

import com.wellit.project.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMember(Member member);

    Optional<Cart> findByMember_MemberId(String memberId);

}
