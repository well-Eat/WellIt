package com.wellit.project.shop;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.order.OrderItem;
import com.wellit.project.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ShopService {


    private final ProductRepository productRepository;
    private final ProdReviewRepository prodReviewRepository;
    private final ProdImageRepository prodImageRepository;
    private final ProdInfoRepository prodInfoRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final MemberService memberService;
    private final OrderItemRepository orderItemRepository;
    private final ProdReviewRepository reviewRepository;


    // 파일 업로드 위치 (서버 대신 업로드 할 임시 위치)
    private static final String UPLOAD_DIR = "C:/uploads/";


    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAllProducts();
    }



    /*서비스 : 상품 리스트 리턴*/
    @Transactional(readOnly = true)
    public List<Product> getProductsByCriteria(String category, String itemSort, int page, int size) {
        //return productRepository.findProductsByCriteria(category, itemSort);  // 매개변수 이름 확인
        return productRepository.findProductsByCriteria(category, itemSort, page, size);  // 매개변수 이름 확인
    }

    /*상품 리스트 리턴*/
    public List<Product> getProdCateList() {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "prodId"));
        return products;
    }*/


    /*상품DTO 1품목 리턴*/
    public Product getOneProd(Long prodId) {

        Optional<Product> optionalProduct = productRepository.findById(prodId);
        return optionalProduct.get();
    }

    //상품 리뷰 리스트 리턴
    public ReviewCnt getCountProdReview(Long prodId) {

        // OrderItem을 통해 해당 Product의 리뷰를 조회
        List<OrderItem> orderItems = orderItemRepository.findAllByProduct_ProdId(prodId);
        ReviewCnt reviewCnt = new ReviewCnt();

        //주문 건수가 0인 경우 -> 리뷰0, 평점0.0
        if(orderItems == null || orderItems.size()==0){
            reviewCnt.setCnt(0);
            reviewCnt.setAvg(0.0);
            return reviewCnt;
        }

        List<ProdReview> reviewList = orderItems.stream()
                                                .map(OrderItem::getProdReview)  // OrderItem에서 ProdReview 가져오기
                                                .filter(review -> review != null)
                                                .collect(Collectors.toList());

        reviewCnt.setCnt(reviewList.size());

        if(reviewList.size()>0){
            reviewCnt.setAvg(reviewList.stream()
                                       .mapToDouble(ProdReview::getRevRating)
                                       .average()
                                       .orElse(0.0));

        } else reviewCnt.setAvg(0.0);

        return reviewCnt;
    }


    /*이미지 리뷰 리스트*/
    public List<ProdReview> getImgReviews(long prodId) {

        // OrderItem을 통해 해당 Product의 리뷰를 조회
        List<OrderItem> orderItems = orderItemRepository.findAllByProduct_ProdId(prodId);

        List<ProdReview> imgReviewList = orderItems.stream()
                                                   .map(OrderItem::getProdReview)  // OrderItem에서 ProdReview 가져오기
                                                   .filter(review -> review != null && review.getRevImg() != null)
                                                   .collect(Collectors.toList());

        log.info("############### ShopService(getImgReview)");
        log.info("Image Review Count: " + imgReviewList.size());
        return imgReviewList;
    }



    //상품상세페이지 리뷰 리스트(페이징)
    public Page<ProdReviewDTO> getPagedRevList(int revPage, long prodId) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(revPage, 5, Sort.by(sorts));

        // OrderItem을 통해 리뷰를 가져오는 방식으로 변경
        List<OrderItem> orderItems = orderItemRepository.findAllByProduct_ProdId(prodId);
        List<Long> orderItemIds = orderItems.stream()
                                            .map(OrderItem::getId)
                                            .collect(Collectors.toList());

        // OrderItem ID를 기준으로 ProdReview 조회
        Page<ProdReview> pagedRevlist = prodReviewRepository.findAllByOrderItemIdIn(orderItemIds, pageable);

        // 엔티티를 DTO로 변환
        List<ProdReviewDTO> reviewDTOs = pagedRevlist.getContent().stream()
                                                     .map(this::convertToProdReviewDTO)
                                                     .collect(Collectors.toList());

        return new PageImpl<>(reviewDTOs, pageable, pagedRevlist.getTotalElements());
    }

    // 엔티티 -> DTO 변환 메서드
    private ProdReviewDTO convertToProdReviewDTO(ProdReview review) {
        ProdReviewDTO dto = new ProdReviewDTO();
        dto.setRevId(review.getRevId());
        dto.setRevText(review.getRevText());
        dto.setRevRating(review.getRevRating());
        dto.setWriter(review.getWriter());
        dto.setCreatedAt(review.getCreatedAt());

        // 이미지 리스트 설정
        List<String> imgList = review.getProdReviewImgList().stream()
                                     .map(ProdReviewImg::getImagePath) // 이미지 경로만 가져오기
                                     .collect(Collectors.toList());
        dto.setProdReviewImgList(imgList);

        return dto;
    }



    /* Create : 상품 생성 */
    @Transactional
    public Product saveProduct(ProductForm productForm, MultipartFile thumbFile, List<MultipartFile> imageFiles)
            throws IOException {


        Product product = new Product();
        product.setProdName(productForm.getProdName());
        product.setProdCate(productForm.getProdCate());
        product.setProdStatus(productForm.getProdStatus());
        product.setProdDesc(productForm.getProdDesc());
        product.setProdStock(productForm.getProdStock());
        product.setProdOrgPrice(productForm.getProdOrgPrice());
        product.setViewCnt(0);

        if (productForm.getProdDiscount() == null) {
            product.setProdFinalPrice(productForm.getProdOrgPrice());
            product.setProdDiscount(0.0);
        } else if (productForm.getProdDiscount() > 0) {
            double v = productForm.getProdOrgPrice() * (1 - productForm.getProdDiscount());
            int i = ((int) (v / 100)) * 100;
            product.setProdFinalPrice(i);
            product.setProdDiscount(productForm.getProdDiscount());
        } else {
            product.setProdDiscount(0.0);
            product.setProdFinalPrice(productForm.getProdOrgPrice());
        }


        // 썸네일 이미지 업로드 처리
        if (!thumbFile.isEmpty()) {
            String thumbFileName = UUID.randomUUID().toString() + "_" + thumbFile.getOriginalFilename();
            Path thumbFilePath = Paths.get(UPLOAD_DIR, thumbFileName);
            Files.write(thumbFilePath, thumbFile.getBytes());
            product.setProdMainImg("/imgs/shop/product/" + thumbFileName);
        }

        /*상품 정보 저장*/
        List<ProdInfo> prodInfoList = new ArrayList<>();
        for (ProdInfo info : productForm.getProdInfoList()) {
            info.setProduct(product);
            prodInfoList.add(info);
        }
        product.setProdInfoList(prodInfoList);


        // 여러 이미지 업로드 처리
        List<ProdImage> prodImageList = new ArrayList<>();
        for (MultipartFile imageFile : imageFiles) {
            if (!imageFile.isEmpty()) {
                String imageFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path imagePath = Paths.get(UPLOAD_DIR, imageFileName);
                Files.write(imagePath, imageFile.getBytes());

                ProdImage prodImage = new ProdImage();
                prodImage.setImagePath("/imgs/shop/product/" + imageFileName);
                prodImage.setProduct(product);
                prodImageList.add(prodImage);
            }
        }
        product.setProdImages(prodImageList);

        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }


    /*기존 상세 이미지 삭제*/
    @Transactional
    public void deleteImageByPath(String imagePath) throws IOException {
        // 파일 경로에서 이미지 삭제
        Path path = Paths.get(imagePath);
        Files.deleteIfExists(path);

        // DB에서 해당 이미지 레코드 삭제
        prodImageRepository.deleteByImagePath(imagePath);
    }

    @Transactional
    public void updateProduct(Long prodId, ProductForm productForm, MultipartFile thumbFile,
                              List<String> toBeDeleted,  // 삭제할 이미지 리스트
                              List<String> existingImages,  // 기존 이미지 경로 리스트
                              List<Integer> existingImageOrders,  // 기존 이미지 순서
                              List<MultipartFile> newImages,  // 새로운 이미지 리스트
                              List<Integer> newImageOrders) throws IOException {

        Product product = getOneProd(prodId);

        // 상품 정보 업데이트
        product.setProdName(productForm.getProdName());
        product.setProdCate(productForm.getProdCate());
        product.setProdStatus(productForm.getProdStatus());
        product.setProdDesc(productForm.getProdDesc());
        product.setProdStock(productForm.getProdStock());
        product.setProdDiscount(productForm.getProdDiscount());
        product.setProdOrgPrice(productForm.getProdOrgPrice());

        // 기존 상품 세부 정보 삭제 -> 수정 내용으로 새로운 리스트 추가
        prodInfoRepository.deleteAllByProduct(product);
        List<ProdInfo> prodInfoList = productForm.getProdInfoList();
        if (prodInfoList != null && !prodInfoList.isEmpty()) {
            for (ProdInfo prodInfo : prodInfoList) {
                prodInfo.setProduct(product);
                prodInfoRepository.save(prodInfo);
            }
        }




        // 썸네일 이미지 처리
        if (!thumbFile.isEmpty()) {
            String thumbFileName = UUID.randomUUID().toString() + "_" + thumbFile.getOriginalFilename();
            Path thumbFilePath = Paths.get(UPLOAD_DIR, thumbFileName);
            Files.write(thumbFilePath, thumbFile.getBytes());
            product.setProdMainImg("/imgs/shop/product/" + thumbFileName);
        }

        // 삭제할 이미지 처리
        if (toBeDeleted != null && !toBeDeleted.isEmpty()) {
            for (String src : toBeDeleted) {
                deleteImageByPath(src);
            }
        }

        // 기존 이미지 순서 업데이트
        if (existingImages != null && existingImageOrders != null) {
            for (int i = 0; i < existingImages.size(); i++) {
                String imagePath = existingImages.get(i);
                int order = existingImageOrders.get(i);
                updateExistingImageOrder(imagePath, order);
            }
        }

        // 새로운 이미지 처리
        if (newImages != null && newImageOrders != null) {
            for (int i = 0; i < newImages.size(); i++) {
                MultipartFile newImageFile = newImages.get(i);
                int order = newImageOrders.get(i);

                if (!newImageFile.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "_" + newImageFile.getOriginalFilename();
                    Path imagePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.write(imagePath, newImageFile.getBytes());

                    ProdImage newImage = new ProdImage();
                    newImage.setImagePath("/imgs/shop/product/" + fileName);
                    newImage.setProdImageNum(order);  // 이미지 순서 저장
                    newImage.setProduct(product);
                    prodImageRepository.save(newImage);
                }
            }
        }

        // 상품 정보 업데이트
        productRepository.save(product);
    }

    //기존 이미지 순서
    public void updateExistingImageOrder(String imagePath, int order) {
        ProdImage existingImage = prodImageRepository.findByImagePath(imagePath);
        if (existingImage != null) {
            existingImage.setProdImageNum(order);  // 이미지 순서 업데이트
            prodImageRepository.save(existingImage);
        }
    }


    //상품 삭제 -> 삭제 미사용. ProdStatus로 관리
    /*public void deleteProduct(Long prodId) {
        Product product = productRepository.findById(prodId)
                                           .orElseThrow(() -> new IllegalArgumentException(
                                                   "해당 상품  id가 존재하지 않습니다. :" + prodId));

        // 썸네일 이미지 파일 삭제
        String thumbImgPath = UPLOAD_DIR + product.getProdMainImg();
        try {
            Files.deleteIfExists(Paths.get(thumbImgPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 여러 상품 이미지 파일 삭제
        for (ProdImage prodImage : product.getProdImages()) {
            String imagePath = UPLOAD_DIR + prodImage.getImagePath();
            try {
                Files.deleteIfExists(Paths.get(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 데이터베이스에서 상품 삭제
        productRepository.deleteById(prodId);
    }*/


    //찜목록 검색 - /shop/detail/{prodId} 상품 상세페이지 찜버튼 확인
    public boolean isFavoriteProduct(Long prodId, String memberId){


        return favoriteProductRepository.existsByProduct_ProdIdAndMember_MemberId(prodId, memberId);
    }

    //찜목록 추가
    @Transactional
    public boolean addFavoriteProduct(Long prodId, String memberId){
        Product product = productRepository.findById(prodId).get();
        Member member = memberService.getMember(memberId);

        FavoriteProduct favoriteProduct = new FavoriteProduct();
        favoriteProduct.setProduct(product);
        favoriteProduct.setMember(member);

        member.getFavoriteProductList().add(favoriteProduct);
        product.getFavoriteProductList().add(favoriteProduct);

        FavoriteProduct saved = favoriteProductRepository.save(favoriteProduct);

        if (saved != null ) return true;
        else  return false;
    }

    //찜목록 삭제
    @Transactional
    public boolean removeFavoriteProduct(Long prodId, String memberId){
        favoriteProductRepository.deleteFavoriteProductByProduct_ProdIdAndMember_MemberId(prodId, memberId);

        //리스트 삭제 여부 확인
        return isFavoriteProduct(prodId, memberId);
    }



    //찜한 상품 리스트 리턴
    public List<FavoriteProductDTO> getFavoriteProductList(String memberId){
        List<FavoriteProduct> productList = favoriteProductRepository.findAllByMember_MemberIdOrderByCreatedAtDesc(memberId);

        List<FavoriteProductDTO> dtoList = new ArrayList<>();

        for (FavoriteProduct favProd: productList) {
            FavoriteProductDTO dto = new FavoriteProductDTO();
            dto.setFavoriteProductId(favProd.getId());
            dto.setMemberName(favProd.getMember().getMemberName());
            dto.setMemberId(favProd.getMember().getMemberId());
            dto.setProdId(favProd.getProduct().getProdId());
            dto.setProdName(favProd.getProduct().getProdName());
            dto.setProdMainImg(favProd.getProduct().getProdMainImg());
            dtoList.add(dto);
        }

        return dtoList;
    }


    // 상품별 리뷰, 찜 개수 dto 리스트로 리턴
    public List<ProdCnt> getProdCntList(List<Product> productList){

        List<ProdCnt> prodCntList = new ArrayList<>();

        for (Product product : productList) {
            ProdCnt prodCnt = new ProdCnt();
            prodCnt.setProdId(product.getProdId());
            prodCnt.setRevCnt(reviewRepository.countByOrderItem_Product_ProdId(product.getProdId()));
            prodCnt.setFavoriteCnt(favoriteProductRepository.countByProduct_ProdId(product.getProdId()));
            prodCntList.add(prodCnt);
        }

        return prodCntList;
    }



    //admin : 상품 리스트 페이징 리턴
    public Page<ProductAdminDTO> findProducts(String search, String prodCate, String status, String startDate, String endDate, int page) {
        // 페이징 처리 및 정렬
        Sort createdAtDesc = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, 100, createdAtDesc);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // 날짜 필터링 처리
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate);
            startDateTime = start.atStartOfDay();
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate).plusDays(1);  // 종료일을 포함하기 위해 하루 더함
            endDateTime = end.atStartOfDay();
        }

        // 상품 정보와 판매 수량 및 매출 금액을 조회
        Page<Product> productPage;

        // 조건에 맞는 상품 필터링
        if(status == null || status.isEmpty()){
            if (search != null && !search.isEmpty() && prodCate != null && !prodCate.isEmpty()) {
                productPage = productRepository.findByProdNameContainingAndProdCate(search, prodCate, pageable);
            } else if (search != null && !search.isEmpty()) {
                productPage = productRepository.findByProdNameContaining(search, pageable);
            } else if (prodCate != null && !prodCate.isEmpty()) {
                productPage = productRepository.findByProdCate(prodCate, pageable);
            } else {
                // 검색 조건이 없으면 모든 상품을 조회
                productPage = productRepository.findAll(pageable);
            }
        } else {
            if (search != null && !search.isEmpty() && prodCate != null && !prodCate.isEmpty()) {
                productPage = productRepository.findByProdNameContainingAndProdCateAndProdStatus(search, prodCate, ProdStatus.valueOf(status), pageable);
            } else if (search != null && !search.isEmpty()) {
                productPage = productRepository.findByProdNameContainingAndProdStatus(search, ProdStatus.valueOf(status), pageable);
            } else if (prodCate != null && !prodCate.isEmpty()) {
                productPage = productRepository.findByProdCateAndProdStatus(prodCate, ProdStatus.valueOf(status), pageable);
            } else {
                productPage = productRepository.findByProdStatus(ProdStatus.valueOf(status), pageable);
            }
        }


        // 판매 수량 및 매출 집계 데이터 조회
        List<Object[]> salesData = orderItemRepository.findProductSalesByDateRange(startDateTime, endDateTime);

        // ProductAdminDTO 리스트로 변환 및 매출 데이터 추가
        return productPage.map(product -> {
            ProductAdminDTO dto = convertToProductAdminDTO(product);

            // 매출 데이터 반영
            salesData.stream()
                     .filter(data -> data[0].equals(product.getProdId()))  // 상품 ID로 매칭
                     .findFirst()
                     .ifPresent(data -> {
                         dto.setSumQuantity((data[1] != null) ? ((Number) data[1]).intValue() : 0);  // 판매 수량
                         dto.setTotalFinalPrice((data[2] != null) ? ((Number) data[2]).intValue() : 0);  // 매출 금액
                     });

            return dto;
        });
    }

    // Product 엔티티를 ProductAdminDTO로 변환하는 메서드
    private ProductAdminDTO convertToProductAdminDTO(Product product) {
        ProductAdminDTO dto = new ProductAdminDTO();
        dto.setProdId(product.getProdId());
        dto.setProdStatus(product.getProdStatus());
        dto.setProdName(product.getProdName());
        dto.setProdOrgPrice(product.getProdOrgPrice());
        dto.setProdDiscount(product.getProdDiscount());
        dto.setProdCate(product.getProdCate());
        dto.setProdFinalPrice(calculateFinalPrice(product.getProdOrgPrice(), product.getProdDiscount()));
        dto.setProdStock(product.getProdStock());
        dto.setViewCnt(product.getViewCnt());
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }



    // 개당 단가 최종 가격 계산(할인율 반영)(
    private Integer calculateFinalPrice(Integer orgPrice, Double discount) {
        double v = orgPrice * (1 - discount);
        int prodFinalPrice = ((int) (v / 100)) * 100;

        return prodFinalPrice;
    }





}


