package com.wellit.project.shop;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.order.OrderItem;
import com.wellit.project.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final ShopService shopService;
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

    // 리뷰 내용을 저장/업데이트
    public ProdReview saveReview(Long prodId, UserDetails userDetails, ProdReviewForm prodReviewForm, List<MultipartFile> images){
        log.info(prodId);
        Member author = memberService.getMember(userDetails.getUsername());
        log.info(prodReviewForm.getOrderItemId());


        //orderItemId로 리뷰 상태 설정
        Long orderItemId = Long.parseLong( prodReviewForm.getOrderItemId().replace(",", ""));
        log.info(orderItemId);
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();
        ProdReview review;
        List<ProdReviewImg> prodReviewImgList;
        //기존에 작성한 리뷰가 없는 경우에만 마일리지 적립
        if(!orderItem.isReviewed()){
            // 마일리지 적립 (5%)
            Integer plusMileage = Integer.parseInt(prodReviewForm.getPaid().replace(",", ""));  //prodReviewForm.getPaid() : 맨앞에 붙은 ','를 제거
            author.setMileage(author.getMileage()+(int)(plusMileage*0.05));
                review = new ProdReview();
            prodReviewImgList = new ArrayList<>();
        } else {
            review = orderItem.getProdReview();
            prodReviewImgList = review.getProdReviewImgList();
            prodReviewImgList.clear();
        }

        // 리뷰 내용(DTO)을 ProdReview 객체로 변환

        //review.setProduct(shopService.getOneProd(prodId));
        review.setRevText(prodReviewForm.getRevText());
        review.setAuthor(author);
        review.setWriter(author.getMemberAlias());
        review.setRevRating(prodReviewForm.getRating());

        //orderItemId로 리뷰 상태 설정
        review.setOrderItem(orderItem);
        orderItem.setProdReview(review);
        orderItem.setReviewed(true);

        review.setProduct(orderItem.getProduct());


        List<MultipartFile> prodMultiList = prodReviewForm.getProdRevImgList();


        // 이미지 처리 로직
        if (prodMultiList != null && !prodMultiList.isEmpty()) {
            for (int i = 0; i < prodMultiList.size(); i++) {
                MultipartFile multiImg = prodMultiList.get(i);

                if (multiImg != null && !multiImg.isEmpty()) {  // 이미지가 실제로 존재할 때만 처리
                    // 파일 저장 로직
                    String fileName = UUID.randomUUID().toString() + "_" + multiImg.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    try {
                        Files.write(filePath, multiImg.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    // ProdReviewImg 엔티티 생성 및 설정
                    ProdReviewImg prodReviewImg = new ProdReviewImg();
                    prodReviewImg.setImagePath("/imgs/shop/review/" + fileName);

                    if (i == 0) {  // 첫 번째 이미지를 대표 이미지로 설정
                        review.setRevImg("/imgs/shop/review/" + fileName);
                    }

                    prodReviewImg.setProdReview(review);
                    prodReviewImgList.add(prodReviewImg);
                }
            }
        }

        if (prodReviewImgList.isEmpty()) {
            review.setRevImg(null);  // 이미지가 없을 경우 null로 설정
        } else {
            review.setProdReviewImgList(prodReviewImgList);
        }

        // 리뷰를 db로 저장
        ProdReview savedReview = reviewRepository.save(review);
        return savedReview;

    }





    /* create : 리뷰 작성 */
/*    public ProdReview createProdReview(ProdReview prodReview) throws IOException {


    }*/


    /* 리뷰 작성 : 이미지 등록 */
/*    public void addProdRevImg(ProdReviewImg prodReviewImg) {
        reviewImgRepository.save(prodReviewImg);
    }*/



}
