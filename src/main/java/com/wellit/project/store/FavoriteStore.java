package com.wellit.project.store;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wellit.project.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorite_store")
@Getter
@Setter
public class FavoriteStore {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long favId;
	
	@ManyToOne
    @JoinColumn(name = "memberId")
    @JsonBackReference("member-favorites") // 이름 설정
	private Member member;
	
	@ManyToOne
    @JoinColumn(name = "store")
	@JsonBackReference("favorites")
	private AllStore store;
}
