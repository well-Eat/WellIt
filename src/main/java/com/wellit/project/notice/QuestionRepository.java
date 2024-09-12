package com.wellit.project.notice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 추가적인 쿼리 메소드가 필요하다면 여기에 정의
}
