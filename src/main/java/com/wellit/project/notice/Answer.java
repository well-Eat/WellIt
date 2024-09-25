package com.wellit.project.notice;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "answers")
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id")
    private Long questionId; // 질문 ID

    @Column(length = 4000) // 최대 길이를 4000으로 설정
    private String content;   // 답변 내용

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime; // 답변 생성 시간
}
