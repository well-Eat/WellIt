package com.wellit.project.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProdReviewForm {


    @NotBlank(message = "상품명은 필수 입력 사항입니다.")
    private String prodName;

    @NotBlank(message = "리뷰 내용은 필수 입력 사항입니다.")
    @Size(min = 3, message = "리뷰 내용은 최소 10자 이상이어야 합니다.")
    private String revText;

    private Long prodId;


    private List<MultipartFile> prodRevImgList;

}
