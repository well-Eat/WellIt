package com.wellit.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;


@Component
public class DatabaseConnectionChecker {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void checkConnection() {
        try {
            // 간단한 쿼리 실행
            String sql = "SELECT 1 FROM dual";
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
            if (result != null && result == 1) {
                System.out.println("데이터베이스 연결 성공!");
            } else {
                System.out.println("데이터베이스 연결 실패!");
            }
        } catch (Exception e) {
            System.out.println("데이터베이스 연결 중 오류 발생: " + e.getMessage());
        }
    }
}