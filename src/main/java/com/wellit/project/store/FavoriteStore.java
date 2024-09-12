package com.wellit.project.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    @JsonBackReference // 추가
	private Member member;
	
	@ManyToOne
    @JoinColumn(name = "store")
    @JsonBackReference // 추가
	private AllStore store;
}
