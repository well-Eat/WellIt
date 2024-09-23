package com.wellit.project.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProdReviewLoadForm {



    @NotBlank(message = "리뷰 내용은 필수 입력 사항입니다.")
    @Size(min = 3, message = "리뷰 내용은 최소 10자 이상이어야 합니다.")
    private String revText;

    private Long prodId;

    private Integer rating;

    private String paid;


    private List<String> prodRevImgList;

    private Long orderItemId;

}
