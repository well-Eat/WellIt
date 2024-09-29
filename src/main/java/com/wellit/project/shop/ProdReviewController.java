package com.wellit.project.shop;


import com.wellit.project.member.MemberService;
import com.wellit.project.order.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop/review")
@Log4j2
public class ProdReviewController {

    private final OrderService orderService;
    private final ProdReviewService reviewService;
    private final MemberService memberService;

    private static final String UPLOAD_DIR = "C:/uploads/";
    private Long orderItemId;



    @GetMapping("/get/{orderItemId}")
    public ResponseEntity<ProdReviewLoadForm> getReviewContent(@PathVariable Long orderItemId){

        ProdReviewLoadForm prodReviewLoadForm = new ProdReviewLoadForm();
        ProdReview prodReview = reviewService.getOneReview(orderItemId);

        prodReviewLoadForm.setOrderItemId(orderItemId);
        prodReviewLoadForm.setProdId(orderService.getProdIdByOrderItemId(orderItemId));
        prodReviewLoadForm.setRevText(prodReview.getRevText());
        prodReviewLoadForm.setRating(prodReview.getRevRating());
        prodReviewLoadForm.setProdRevImgList(prodReview.getProdReviewImgList().stream().map(prodReviewImg -> prodReviewImg.getImagePath()).collect(
                Collectors.toList()));

        return ResponseEntity.ok(prodReviewLoadForm);
    }



    // 작성된 리뷰가 있는지 여부를 리턴
    @GetMapping("/exist/{orderItemId}")
    public ResponseEntity<Boolean> checkReviewed(@PathVariable Long orderItemId){
        boolean reviewed = reviewService.checkReviewed(orderItemId);
        return ResponseEntity.ok(reviewed);
    }


    /*Post : 상품 리뷰 저장*/
    @PostMapping("/save/{prodId}")
    public ResponseEntity<String> saveReview(@PathVariable("prodId") Long prodId,
                                             @Valid @ModelAttribute ProdReviewForm prodReviewForm,
                                             @RequestParam(value = "prodRevImgList", required = false) List<MultipartFile> images,
                                             @RequestParam(value = "existingImgUrls", required = false) List<String> existingImgUrls
    ) throws IOException {

        String memberId = memberService.getMemberId();

        //로그인 상태가 아닐경우, 로그인 창으로 리다이렉트
        if (memberId == null) {
            throw new RuntimeException("로그인해주세요");
        }

        // 서비스로 전달하기 전에 null 처리
        if (existingImgUrls == null) {
            existingImgUrls = new ArrayList<>(); // 빈 리스트로 초기화
        }

        // 리뷰 내용을 전달
        ProdReview savedReview = reviewService.saveReview(prodId, prodReviewForm, images, existingImgUrls);

        return ResponseEntity.ok("리뷰 저장 완료");

    }













}








































































