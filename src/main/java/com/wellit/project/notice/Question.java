package com.wellit.project.notice;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qId;

    private String qTitle;
    
    @Column(length = 4000) // 최대 길이를 4000으로 설정
    private String qContent;
    
    private String qAuthor;
    
    @Column(name = "created_time", updatable = false)
    private LocalDateTime qCreatedTime;
    
    private String qImageUrl;
    
    private String qCategory;

    
}
