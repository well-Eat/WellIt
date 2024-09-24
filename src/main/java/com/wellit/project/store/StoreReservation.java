package com.wellit.project.store;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wellit.project.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "store_reservation")
@Getter
@Setter
public class StoreReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예약 번호

    private String reserveTime; // 예약 시간

    private boolean accepted; // 예약 수락 여부

    @ManyToOne // AllStore와의 관계
    @JoinColumn(name = "stoId", nullable = false) // 외래 키 설정
    @JsonBackReference("member-reservations") // 이름 설정
    private AllStore allStore; // 관련 가게 정보

    @ManyToOne // Member와의 관계
    @JoinColumn(name = "memberId", nullable = false) // 외래 키 설정
    @JsonBackReference("member-reservations") // 이름 설정
    private Member member; // 예약한 회원 정보
}