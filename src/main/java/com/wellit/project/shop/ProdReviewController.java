package com.wellit.project.shop;


import com.wellit.project.member.MemberService;
import com.wellit.project.order.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    private static final String UPLOAD_DIR = "C:/uploads/";
    private Long orderItemId;


    /*Get : 상품 리뷰 폼 열기*/
    /*@GetMapping("/{prodId}")
    public String getReviewForm(Model model, @PathVariable("prodId") Long prodId) {

        Product product = shopService.getOneProd(prodId);

        model.addAttribute("product", product);
        model.addAttribute("prodReviewForm", new ProdReviewForm()); // 폼 객체 추가
        return "/order/order_reviewForm";
    }
*/

    /*

        // 유효성 검사에 실패한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", shopService.getOneProd(prodId));
            // 유효성 검사 에러가 있으면 다시 폼을 보여줌
            return "order/order_reviewForm";
        }
*/

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
    public ResponseEntity<String> saveReview(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable("prodId") Long prodId,
                                             @Valid @ModelAttribute ProdReviewForm prodReviewForm,
                                             @RequestParam(value = "prodRevImgList", required = false) List<MultipartFile> images,
                                             @RequestParam(value = "existingImgUrls", required = false) List<String> existingImgUrls
    ) throws IOException {

        //로그인 상태가 아닐경우, 로그인 창으로 리다이렉트
        if (userDetails == null) {
            throw new RuntimeException("로그인해주세요");
        }

        // 서비스로 전달하기 전에 null 처리
        if (existingImgUrls == null) {
            existingImgUrls = new ArrayList<>(); // 빈 리스트로 초기화
        }

        // 리뷰 내용을 전달
        ProdReview savedReview = reviewService.saveReview(prodId, userDetails, prodReviewForm, images, existingImgUrls);

        return ResponseEntity.ok("리뷰 저장 완료");

    }


/* 원본
    @PostMapping("/{prodId}")
    public String saveReview(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable("prodId") Long prodId,
                                @Valid @ModelAttribute ProdReviewForm prodReviewForm,
                                BindingResult bindingResult, Model model, @RequestParam("prodRevImgList") List<MultipartFile> images) throws IOException {

        if(userDetails ==null){
            return "redirect:/member/login";
        }

        Member author = memberService.getMember(userDetails.getUsername());


        Integer plusMileage = Integer.parseInt(prodReviewForm.getPaid().substring(1));

        author.setMileage(author.getMileage()+(int)(plusMileage*0.05));

        ProdReview review = new ProdReview();
        review.setProduct(shopService.getOneProd(prodId));
        review.setRevText(prodReviewForm.getRevText());
        review.setAuthor(author);
        review.setWriter(author.getMemberAlias());
        review.setRevRating(prodReviewForm.getRating());

        //orderItemId로 리뷰 상태 설정
        Long orderItemId = Long.parseLong( prodReviewForm.getOrderItemId().substring(1));

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow();
        orderItem.setReviewed(true);
        orderItem.setProdReview(review);
        review.setOrderItem(orderItem);


        List<MultipartFile> prodMultiList = prodReviewForm.getProdRevImgList();
        List<ProdReviewImg> prodReviewImgList = new ArrayList<>();

        // 이미지 처리 로직
        if (prodMultiList != null && !prodMultiList.isEmpty()) {
            for (int i = 0; i < prodMultiList.size(); i++) {
                MultipartFile multiImg = prodMultiList.get(i);

                if (multiImg != null && !multiImg.isEmpty()) {  // 이미지가 실제로 존재할 때만 처리
                    // 파일 저장 로직
                    String fileName = UUID.randomUUID().toString() + "_" + multiImg.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.write(filePath, multiImg.getBytes());

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

        ProdReview savedReview = shopService.createProdReview(review);



        return "redirect:/shop/detail/" + prodId + "#prodReview"; //상세페이지 : 리뷰 위치로 리다이렉트
    }
*/














}








































































