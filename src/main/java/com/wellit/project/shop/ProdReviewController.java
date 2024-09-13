package com.wellit.project.shop;


import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.order.OrderService;
import com.wellit.project.order.PoHistoryForm;
import com.wellit.project.shop.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shop/review")
@Log4j2
public class ProdReviewController {

    private final ShopService shopService;
    private final MemberService memberService;
    private final OrderService orderService;

    private static final String UPLOAD_DIR = "C:/uploads/";


    /*Get : 상품 리뷰 폼 열기*/
    @GetMapping("/{prodId}")
    public String getReviewForm(Model model, @PathVariable("prodId") Long prodId) {

        Product product = shopService.getOneProd(prodId);

        model.addAttribute("product", product);
        model.addAttribute("prodReviewForm", new ProdReviewForm()); // 폼 객체 추가
        return "/order/order_reviewForm";
    }


    /*

        // 유효성 검사에 실패한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", shopService.getOneProd(prodId));
            // 유효성 검사 에러가 있으면 다시 폼을 보여줌
            return "order/order_reviewForm";
        }
*/

    /*Post : 상품 리뷰 저장*/
    @PostMapping("/{prodId}")
    public String getReviewForm(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable("prodId") Long prodId,
                                @Valid @ModelAttribute ProdReviewForm prodReviewForm,
                                BindingResult bindingResult, Model model, @RequestParam("prodRevImgList") List<MultipartFile> images) throws IOException {

        if(userDetails ==null){
            return "redirect:/member/login";
        }

        Member author = memberService.getMember(userDetails.getUsername());


        ProdReview review = new ProdReview();
        review.setProduct(shopService.getOneProd(prodId));
        review.setRevText(prodReviewForm.getRevText());
        review.setAuthor(author);
        review.setWriter(author.getMemberAlias());
        review.setRevRating(prodReviewForm.getRating());
        //review.setRevImg("https://img.freepik.com/premium-photo/vegetable-sandwich-paper-wrap-vegan-healthy-food-takeaway-food_1149271-62531.jpg?ga=GA1.1.1488286810.1713965085&semt=ais_hybrid");
// 리뷰 저장



        List<MultipartFile> prodMultiList = prodReviewForm.getProdRevImgList();
        List<ProdReviewImg> prodReviewImgList = new ArrayList<>();

        if (prodMultiList != null && !prodMultiList.isEmpty()) {
            for (int i = 0; i < prodMultiList.size(); i++) {
                MultipartFile multiImg = prodMultiList.get(i);

                // 파일 저장 로직
                String fileName = UUID.randomUUID().toString() + "_" + multiImg.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.write(filePath, multiImg.getBytes());

                // ProdReviewImg 엔티티 생성 및 설정
                ProdReviewImg prodReviewImg = new ProdReviewImg();
                prodReviewImg.setImagePath("/imgs/shop/review/" + fileName);
                if( i == 0 ){
                    review.setRevImg("/imgs/shop/review/" + fileName);
                }
                log.info(prodReviewImg.getImagePath());
                prodReviewImg.setProdReview(review);
                prodReviewImgList.add(prodReviewImg);
                //shopService.addProdRevImg(prodReviewImg);
            }
        }
        review.setProdReviewImgList(prodReviewImgList);
        ProdReview savedReview = shopService.createProdReview(review);


        return "redirect:/shop/detail/" + prodId + "#prodReview"; //상세페이지 : 리뷰 위치로 리다이렉트
    }




    @GetMapping("/test")
    public String testPoHistoryReview(Model model, @AuthenticationPrincipal UserDetails userDetail){


        List<PoHistoryForm> poHistoryList = orderService.getPoHistoryList(userDetail.getUsername());
        model.addAttribute("poHistoryList", poHistoryList);
        model.addAttribute("member", memberService.getMember(userDetail.getUsername()) );
        model.addAttribute("prodReviewForm", new ProdReviewForm()); // 폼 객체 추가

        return "/order/mypage_orderHistory";
    }













}








































































