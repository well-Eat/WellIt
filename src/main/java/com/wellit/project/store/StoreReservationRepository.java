package com.wellit.project.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wellit.project.member.Member;

@Repository
public interface StoreReservationRepository extends JpaRepository<StoreReservation, Long> {

	boolean existsByMemberAndAllStore(Member member, AllStore allStore);

	List<StoreReservation> findByMember(Member member);
	
    List<StoreReservation> findByMember_BusinessNameAndAllStore_StoName(String businessName, String stoName);
}