package com.wellit.project.shop;

import com.wellit.project.order.CartItemRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/shop")
@Controller
@Log4j2
public class ShopController {

    private final ShopService shopService;
    private final ProductRepository productRepository;
    private final ProdReviewService reviewService;

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
        List<ProdCnt> prodCnts = shopService.getProdCntList();

        // prodId를 키로, 리뷰, 찜 카운트 Map
        Map<Long, Integer> revCntMap = prodCnts.stream()
                                                    .collect(Collectors.toMap(ProdCnt::getProdId, ProdCnt::getRevCnt));
        Map<Long, Integer> favoriteCntMap = prodCnts.stream()
                                                    .collect(Collectors.toMap(ProdCnt::getProdId, ProdCnt::getFavoriteCnt));

        model.addAttribute("prodlist", prodList);
        model.addAttribute("revCntMap", revCntMap);
        model.addAttribute("favoriteCntMap", favoriteCntMap);

        return "shop/shop_list";
    }


    /*상품 상세페이지 이동*/
    @GetMapping("/detail/{prodId}")
    public String getShopDetail(Model model, @PathVariable("prodId") Long prodId, @AuthenticationPrincipal UserDetails userDetails) {
        String memberId = null;
        //멤버아이디 확인
        if(userDetails!=null){
            memberId = userDetails.getUsername();
            boolean favorite = shopService.isFavoriteProduct(prodId, memberId);
            model.addAttribute("memberId", memberId);
            model.addAttribute("favorite", favorite);
        } else {
            model.addAttribute("memberId", null);
            model.addAttribute("favorite", false);
        }


        Product product = shopService.getOneProd(prodId);
        product.setViewCnt(product.getViewCnt()+1);
        productRepository.save(product);

        List<ProdReview> imgReviewList = shopService.getImgReviews(prodId);
        CartItemRequest cartItemRequest = new CartItemRequest();

        model.addAttribute("cartItemRequest", cartItemRequest);

        model.addAttribute("reviewCnt", shopService.getCountProdReview(prodId));

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

        Page<ProdReviewDTO> pagedRevList = shopService.getPagedRevList(revPage, prodId);

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

    // 상품 상세페이지 : 찜하기 버튼
    @PostMapping("/favorite/change")
    @ResponseBody
    public ResponseEntity<String> isFavoriteProduct(@RequestParam(required = true) Long prodId, @RequestParam(required = true) String memberId){

        log.info(prodId);
        log.info(memberId);
        try{
            if(memberId == null){
                throw new RuntimeException("로그인해주세요");
            }

            //현재 찜 리스트에 있는 지 확인
            boolean isFavorite = shopService.isFavoriteProduct(prodId, memberId);

            //없는 경우 새로 추가
            if(isFavorite == false){
                shopService.addFavoriteProduct(prodId, memberId);
                return ResponseEntity.ok("찜 목록을 추가하였습니다.");
            } else {
                shopService.removeFavoriteProduct(prodId, memberId);
                return ResponseEntity.ok("찜 목록을 해제하였습니다.");
            }


        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/favorite/list/{memberId}")
    @ResponseBody
    public ResponseEntity<List<FavoriteProductDTO>> getFavoriteProductList(@PathVariable(value = "memberId")String memberId){

        List<FavoriteProductDTO> favoriteList = shopService.getFavoriteProductList(memberId);

        return ResponseEntity.ok(favoriteList);
    }



}






