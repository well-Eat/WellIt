package com.wellit.project.store;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellit.project.member.Member;

@Repository
public interface FavoriteStoreRepository extends JpaRepository<FavoriteStore, Long> {
    // 특정 memberId와 storeId로 찜 목록을 찾기 위한 메소드 추가
    FavoriteStore findByMemberAndStore(Member member, AllStore store);

	List<FavoriteStore> findAllByMember(Member member);

    List<FavoriteStore> findByMember_MemberId(String memberId); // Member 클래스의 필드 이름에 맞춤

}