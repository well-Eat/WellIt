package com.wellit.project.life;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CookOrderCardForm {
    private Long id;

    private Integer cookOrderNum; //요리 순서

    private MultipartFile cookOrderImg; //요리 이미지

    private String cookOrderText; //요리 방법

    private String existingCookOrderImg; // 기존 요리 이미지 URL 추가



    private Recipe recipe;



}
