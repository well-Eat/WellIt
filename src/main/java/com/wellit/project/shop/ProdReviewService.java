package com.wellit.project.shop;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.order.OrderItem;
import com.wellit.project.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProdReviewService {

    private final ProdReviewRepository reviewRepository;
    private final MemberService memberService;
    private final OrderItemRepository orderItemRepository;

    // 리뷰 이미지를 실제로 업로드 할 위치
    private static final String UPLOAD_DIR = "C:/uploads/";

    // 이미 작성된 리뷰가 있는지 여부 확인
    public boolean checkReviewed(Long orderItemId){
        return reviewRepository.existsProdReviewByOrderItem_Id(orderItemId);
    }

    public ProdReview getOneReview(Long orderItemId){
        return reviewRepository.findByOrderItem_Id(orderItemId);
    }

    //리뷰 저장
    public ProdReview saveReview(Long prodId, ProdReviewForm prodReviewForm, List<MultipartFile> images, List<String> existingImgUrls) {
        log.info(prodId);
        Member author = memberService.getMember(memberService.getMemberId());
        log.info(prodReviewForm.getOrderItemId());

        // orderItemId로 리뷰 상태 설정
        Long orderItemId = Long.parseLong(prodReviewForm.getOrderItemId().replace(",", ""));
        log.info(orderItemId);
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();
        ProdReview review;
        List<ProdReviewImg> prodReviewImgList;

        // 기존 리뷰가 있으면 이미지 리스트 초기화
        if (orderItem.isReviewed()) {
            review = orderItem.getProdReview();
            prodReviewImgList = review.getProdReviewImgList();

            // 기존 이미지 DB에서 삭제
            prodReviewImgList.clear();
        } else {

            // 마일리지 적립 (5%)
            Integer plusMileage = Integer.parseInt(prodReviewForm.getPaid().replace(",", ""));
            author.setMileage(author.getMileage() + (int) (plusMileage * 0.05));

            review = new ProdReview();
            prodReviewImgList = new ArrayList<>();
        }


        // 리뷰 내용 설정
        review.setRevText(prodReviewForm.getRevText());
        review.setWriter(author.getMemberAlias());
        review.setRevRating(prodReviewForm.getRating());
        review.setOrderItem(orderItem);
        orderItem.setProdReview(review);
        orderItem.setReviewed(true);


        // 이미지 처리 로직

        // 1. 기존 이미지 URL 처리 (existImageSrc)
        if (existingImgUrls != null && !existingImgUrls.isEmpty()) {
            for (String url : existingImgUrls) {
                ProdReviewImg prodReviewImg = new ProdReviewImg();
                prodReviewImg.setImagePath(url);
                prodReviewImg.setProdReview(review);
                prodReviewImgList.add(prodReviewImg);
            }
        }

        // 2. 새로 업로드된 이미지 파일 처리 (filesArray)
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile multiImg = images.get(i);

                if (!multiImg.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + multiImg.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    try {
                        Files.write(filePath, multiImg.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    ProdReviewImg prodReviewImg = new ProdReviewImg();
                    prodReviewImg.setImagePath("/imgs/shop/review/" + fileName);

                    if (i == 0) {
                        review.setRevImg("/imgs/shop/review/" + fileName);  // 첫 이미지를 대표 이미지로 설정
                    }

                    prodReviewImg.setProdReview(review);
                    prodReviewImgList.add(prodReviewImg);
                }
            }
        }

        // 이미지가 없을 경우 대표 이미지 삭제
        if (prodReviewImgList.isEmpty()) {
            review.setRevImg(null);
        } else { // 있는 경우 첫번째 이미지 저장
            review.setRevImg(prodReviewImgList.get(0).getImagePath());
        }

        // 이미지 리스트 설정
        review.setProdReviewImgList(prodReviewImgList);

        // 리뷰 저장
        return reviewRepository.save(review);
    }







}
