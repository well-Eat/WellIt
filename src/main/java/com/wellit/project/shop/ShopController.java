package com.wellit.project.shop;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@RequestMapping("/shop")
@Controller
@Log4j2
public class ShopController {

    private final ShopService shopService;

    private static final String UPLOAD_DIR = "C:/uploads/";

    /*인기상품 리스트 이동*/
    @GetMapping("/")
    @ResponseBody
    public String getShopPopular() {
        return "shop_popular";
    }

    /*상품 리스트 페이지 이동*/
    @GetMapping("/list")
    public String getShopList(Model model) {

        List<Product> prodList = shopService.getProdCateList();

        model.addAttribute("prodlist", prodList);

        return "shop/shop_list";
    }


    /*상품 상세페이지 이동*/
    @GetMapping("/detail/{prodId}")
    public String getShopDetail(Model model, @PathVariable("prodId") Long prodId) {

        Product product = shopService.getOneProd(prodId);
        List<ProdReview> imgReviewList = shopService.getImgReviews(product);


        model.addAttribute("product", product);
        model.addAttribute("imgReviewList", imgReviewList);

        return "shop/shop_detail";
    }

    //리뷰 페이징
    @GetMapping("/review/{prodId}/{revPage}")
    @ResponseBody
    public Map<String, Object> listProducts(
            @PathVariable("revPage") int revPage,
            @PathVariable("prodId") int prodId) {

        Page<ProdReview> pagedRevList = shopService.getPagedRevList(revPage, prodId);

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", pagedRevList.getContent());
        response.put("totalPages", pagedRevList.getTotalPages());

        return response;
    }


    //상품 삭제 페이지
    @DeleteMapping("/delete/{prodId}")
    public String deleteProduct(@PathVariable(name="prodId") Long prodId) {

        shopService.deleteProduct(prodId);

        return "redirect:/shop/list";
    }


    //상품 생성 폼 열기
    @GetMapping("/form")
    public String productForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return "/shop/shop_form";
    }

    //상품 저장하기
    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute ProductForm productForm) throws IOException {

        List<MultipartFile> imageFiles = productForm.getProdImages();
        MultipartFile thumbFile = productForm.getProdMainImg();

        shopService.saveProduct(productForm, thumbFile, imageFiles);

        return "redirect:/shop/list";
    }

    //상품수정 폼 열기
    @GetMapping("/edit/{prodId}")
    public String editProduct(@PathVariable("prodId") Long prodId, Model model) {
        Product product = shopService.getOneProd(prodId);  // 기존 상품 조회
        ProductForm productForm = new ProductForm(product);  // ProductForm에 매핑
        List<ProdImage> imageFiles = product.getProdImages();
        List<String> toBeDeleted = new ArrayList<>();

        String prodMainImg = product.getProdMainImg();

        model.addAttribute("productForm", productForm);
        model.addAttribute("imageFiles", imageFiles);
        model.addAttribute("prodMainImg", prodMainImg);
        model.addAttribute("prodDiscount", (product.getProdDiscount() != null) ? product.getProdDiscount() : 0);
        model.addAttribute("toBeDeleted", toBeDeleted);

        return "/shop/shop_form";  // 수정 폼으로 이동
    }

    //상품 수정내용 저장하기
    @PostMapping("/update/{prodId}")
    public String updateProduct(@PathVariable Long prodId,
                                @ModelAttribute ProductForm productForm,
                                @RequestParam(required=false) List<String> toBeDeleted) throws IOException {

        List<MultipartFile> imageFiles = productForm.getProdImages();
        MultipartFile thumbFile = productForm.getProdMainImg();

        // 삭제할 이미지 처리
        if (toBeDeleted != null) {
            for (String src : toBeDeleted) {
                // 이미지 경로에 해당하는 이미지를 삭제하는 로직
                shopService.deleteImageByPath(src);
            }
        }

        shopService.updateProduct(prodId, productForm, thumbFile, imageFiles);
        return "redirect:/shop/list"; // 상품 리스트로 리다이렉트
    }

}






