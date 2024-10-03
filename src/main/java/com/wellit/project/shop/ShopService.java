package com.wellit.project.shop;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.order.OrderItem;
import com.wellit.project.order.OrderItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class ShopService {

    @PersistenceContext
    private EntityManager entityManager;


    private final ProductRepository productRepository;
    private final ProdReviewRepository prodReviewRepository;
    private final ProdImageRepository prodImageRepository;
    private final ProdInfoRepository prodInfoRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final MemberService memberService;
    private final OrderItemRepository orderItemRepository;
    private final ProdReviewRepository reviewRepository;
    private final ProductRepositoryCustom productRepositoryCustom;


    // 파일 업로드 위치 (서버 대신 업로드 할 임시 위치)
    private static final String UPLOAD_DIR = "C:/uploads/";


    /*서비스 : 상품 리스트 리턴*/
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCriteria(String category, String itemSort, int page, int size, String search) {

        String sortDirection;
        if(itemSort.equals("lowPrice")){
            sortDirection = "ASC";
            itemSort = "highPrice";
        }
        else sortDirection= "DESC";

        // 현재 날짜 기준으로 1년 전부터 오늘까지의 기간을 산정
        LocalDateTime startDateTime = LocalDateTime.now().minusYears(1);
        LocalDateTime endDateTime = LocalDateTime.now();

        // java.sql.Timestamp로 변환
        Timestamp sqlStartDate = Timestamp.valueOf(startDateTime);
        Timestamp sqlEndDate = Timestamp.valueOf(endDateTime);

        log.info("sqlStartDate : {}", sqlStartDate);
        log.info("sqlEndDate : {}", sqlEndDate);

        String status = "AVAILABLE";

        // 리포지토리 메서드 호출
        Page<ProductAdminDTO> productAdminPages = productRepositoryCustom.findProductsByCriteria(category, itemSort, sortDirection, page, size, sqlStartDate, sqlEndDate, status, search);

        return new PageImpl<>(productAdminPages.getContent().stream()
                                               .map(productAdminDTO -> productRepository.findById(productAdminDTO.getProdId()).orElse(null))
                                               .filter(Objects::nonNull)
                                               .collect(Collectors.toList()), productAdminPages.getPageable(), productAdminPages.getTotalElements());
    }





    //jpql 실패
    /*
    public Page<Product> getProdCateList(String category, String itemSort, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size); // JPA에서 페이지는 0부터 시작

        log.info("############## getProdCateList(String category, String itemSort, int page, int size)");
        log.info("Category: {}, Sort: {}, Page: {}, Size: {}", category, itemSort, page, size);

        if (category == null || category.equals("all")) {
            // 카테고리 없이 전체 상품을 정렬 기준에 따라 반환
            return getProductsBySort(itemSort, pageable);
        } else {
            // 카테고리가 있는 경우 해당 카테고리를 필터링하고 정렬 기준에 따라 반환
            return getProductsByCategoryAndSort(category, itemSort, pageable);
        }
    }

    private Page<Product> getProductsBySort(String itemSort, Pageable pageable) {
        log.info(itemSort);
        if (itemSort.equals("reviewRating")) {
            return productRepository.findAllByRevRating(pageable); // 리뷰 평점 순
        } else if (itemSort.equals("reviewCount")) {
            return productRepository.findAllByReviewCount(pageable); // 리뷰 개수 순
        } else if (itemSort.equals("salesCount")) {
            return productRepository.findAllBySalesCount(pageable); // 판매량 순
        } else if (itemSort.equals("favoriteProduct")) {
            Page<Product> pageList = productRepository.findAllByFavoriteCount(pageable); // 찜 순
            for (Product product: pageList) {
                log.info("Product: {}", product);
            }
            return pageList; // 찜 순
        } else {
            return productRepository.findAllProducts(pageable); // 기본 정렬 (예: 최신순)
        }
    }

    private Page<Product> getProductsByCategoryAndSort(String category, String itemSort, Pageable pageable) {
        if (itemSort.equals("reviewRating")) {
            return productRepository.findByCategoryAndRevRating(category, pageable); // 리뷰 평점 순
        } else if (itemSort.equals("reviewCount")) {
            return productRepository.findByCategoryAndReviewCount(category, pageable); // 리뷰 개수 순
        } else if (itemSort.equals("salesCount")) {
            return productRepository.findByCategoryAndSalesCount(category, pageable); // 판매량 순
        } else if (itemSort.equals("favoriteCount")) {
            return productRepository.findByCategoryAndFavoriteCount(category, pageable); // 찜 순
        } else {
            return productRepository.findByCategory(category, pageable); // 기본 정렬 (예: 최신순)
        }
    }
*/

/*    public List<Product> getProdCateList(String itemSort) {

        List<Product> products = new ArrayList<>();

        if(itemSort == null){   //id순
            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "prodId"));
        } else if(itemSort.equals("latest")){
            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if (itemSort.equals("lowprice")) {
            products = productRepository.findAll(Sort.by(Sort.Direction.ASC, "prodFinalPrice"));
        }else if (itemSort.equals("highprice")) {
            products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "prodFinalPrice"));
        }
        return products;
    }


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
    public Product saveProduct(ProductForm productForm, MultipartFile thumbFile, List<MultipartFile> newImages, List<Integer> newImageOrders)
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
/*      기존 코드 (241001 이전)
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
        }*/

        // 새로운 이미지 처리 (순서 유지하여 저장)
        if (newImages != null && newImageOrders != null) {
            for (int i = 0; i < newImages.size(); i++) {
                MultipartFile newImageFile = newImages.get(i);
                int order = newImageOrders.get(i);

                // 이미지가 비어있지 않을 경우에만 처리
                if (!newImageFile.isEmpty()) {
                    // 고유한 파일명을 생성하여 이미지 저장
                    String fileName = UUID.randomUUID().toString() + "_" + newImageFile.getOriginalFilename();
                    Path imagePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.write(imagePath, newImageFile.getBytes());

                    // ProdImage 엔티티 생성 후 이미지 경로와 순서를 설정
                    ProdImage newImage = new ProdImage();
                    newImage.setImagePath("/imgs/shop/product/" + fileName); // 저장된 이미지 경로 설정
                    newImage.setProdImageNum(order);  // 이미지 순서 저장
                    newImage.setProduct(product); // 연관된 상품 설정

                    // DB에 새로운 이미지 정보 저장
                    prodImageRepository.save(newImage);
                }
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
//        product.setProdDiscount(productForm.getProdDiscount());
        product.setProdOrgPrice(productForm.getProdOrgPrice());

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

        // 기존 상품 세부 정보 삭제 -> 수정 내용으로 새로운 리스트 추가
        prodInfoRepository.deleteAllByProduct(product);

        entityManager.flush();

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
    public Page<ProductAdminDTO> findProducts(String search, String prodCate, String status, String startDate, String endDate,
                                              int page, int pageSize, String itemSort, String direction) {

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        // 날짜 필터링 처리
        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate); // String을 LocalDate로 변환
            startDateTime = start.atStartOfDay(); // LocalDateTime으로 변환
        }
        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate).plusDays(1);  // 종료일을 포함하기 위해 하루 더함
            endDateTime = end.atStartOfDay(); // LocalDateTime으로 변환
        }

        // LocalDateTime을 java.sql.Timestamp로 변환
        Timestamp sqlStartDate = (startDateTime != null) ? Timestamp.valueOf(startDateTime) : null;
        Timestamp sqlEndDate = (endDateTime != null) ? Timestamp.valueOf(endDateTime) : null;


        if(pageSize== 0) pageSize = 20;

        // 저장 프로시저 호출
        Page<ProductAdminDTO> products = productRepositoryCustom.findProductsByCriteria(
                prodCate, // 카테고리
                itemSort, // 정렬 기준
                direction, //정렬 방향
                page,
                pageSize, // 페이지 크기
                sqlStartDate, // 시작 날짜
                sqlEndDate,   // 종료 날짜
                status,        // 상품 상태
                search // 검색어
        );

        products.stream()
                    .forEach(product -> log.info("sumQuantity: {}", product.getSumQuantity()));

        return products;
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





	public List<Product> findAll() {
        return productRepository.findAll();
	}





}


