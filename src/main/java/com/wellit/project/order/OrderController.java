package com.wellit.project.order;


import com.wellit.project.member.Member;
import com.wellit.project.member.MemberRepository;
import com.wellit.project.member.MemberService;
import com.wellit.project.shop.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import oracle.jdbc.proxy.annotation.Post;
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
@RequestMapping("/order")
@Log4j2
public class OrderController {

    private final ShopService shopService;
    private final MemberService memberService;

    private static final String UPLOAD_DIR = "src/main/resources/static/imgs/order/review";


    /*Get : 상품 리뷰 폼 열기*/
    @GetMapping("/review/{prodId}")
    public String getReviewForm(Model model, @PathVariable("prodId") Long prodId) {

        Product product = shopService.getOneProd(prodId);

        model.addAttribute("product", product);
        model.addAttribute("prodReviewForm", new ProdReviewForm()); // 폼 객체 추가
        return "/order/order_reviewForm";
    }


    /*Post : 상품 리뷰 저장*/
    @PostMapping("/review/{prodId}")
    public String getReviewForm(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable("prodId") Long prodId,
                                @Valid @ModelAttribute ProdReviewForm prodReviewForm,
                                BindingResult bindingResult, Model model) throws IOException {

        if(userDetails ==null){
            return "redirect:/member/login";
        }

        Member author = memberService.getMember(userDetails.getUsername());

        // 유효성 검사에 실패한 경우
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", shopService.getOneProd(prodId));
            // 유효성 검사 에러가 있으면 다시 폼을 보여줌
            return "order/order_reviewForm";
        }


        ProdReview review = new ProdReview();
        review.setProduct(shopService.getOneProd(prodId));
        review.setRevText(prodReviewForm.getRevText());
        review.setAuthor(author);
        review.setWriter(author.getMemberAlias());
        review.setRevRating(3);
        review.setRevImg("https://img.freepik.com/premium-photo/vegetable-sandwich-paper-wrap-vegan-healthy-food-takeaway-food_1149271-62531.jpg?ga=GA1.1.1488286810.1713965085&semt=ais_hybrid");
// 리뷰 저장

        ProdReview savedReview = shopService.createProdReview(review);

        List<MultipartFile> prodMultiList = prodReviewForm.getProdRevImgList();

        if (prodMultiList != null && !prodMultiList.isEmpty()) {
            for (MultipartFile multiImg : prodMultiList) {
                if (!multiImg.isEmpty()) {
                    // 파일 저장 로직
                    String fileName = UUID.randomUUID().toString() + "_" + multiImg.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.write(filePath, multiImg.getBytes());

                    // ProdReviewImg 엔티티 생성 및 설정
                    ProdReviewImg prodReviewImg = new ProdReviewImg();
                    prodReviewImg.setImagePath("/imgs/order/review/" + fileName);
                    log.info(prodReviewImg.getImagePath());
                    prodReviewImg.setProdReview(savedReview);
                    shopService.addProdRevImg(prodReviewImg);
                }
            }
        }


        return "redirect:/shop/detail/" + prodId + "#prodReview"; //상세페이지 : 리뷰 위치로 리다이렉트
    }





}
