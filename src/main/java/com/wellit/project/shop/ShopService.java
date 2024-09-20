package com.wellit.project.shop;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final ProdReviewImgRepository prodReviewImgRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final MemberService memberService;

    // 파일 업로드 위치 (서버 대신 업로드 할 임시 위치)
    private static final String UPLOAD_DIR = "C:/uploads/";


/*    public ShopService(ProductRepository productRepository, ProdReviewRepository prodReviewRepository,
                       ProdInfoRepository prodInfoRepository, ProdReviewImgRepository prodReviewImgRepository,
                       ProdImageRepository prodImageRepository) {
        this.prodReviewRepository = prodReviewRepository;
        this.productRepository = productRepository;
        this.prodInfoRepository = prodInfoRepository;
        this.prodReviewImgRepository = prodReviewImgRepository;
        this.prodImageRepository = prodImageRepository;
    }*/

    /*상품 리스트 리턴*/
    public List<Product> getProdCateList() {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "prodId"));
        return products;
    }


    /*상품DTO 1품목 리턴*/
    public Product getOneProd(Long prodId) {

        Optional<Product> optionalProduct = productRepository.findById(prodId);
        return optionalProduct.get();
    }

    public List<ProdReview> getOneProdReviewList(Long prodId) {
        Product product = productRepository.findById(prodId).orElseThrow();
        log.info(product.getProdReview());
        return product.getProdReview();
    }


    /*이미지 리뷰 리스트*/
    public List<ProdReview> getImgReviews(Product product) {
        List<ProdReview> imgReviewList = product.getProdReview().stream()
                                                .filter(review -> review.getRevImg() != null)
                                                .collect(Collectors.toList());

        log.info("############### ShopService(getImgReview)");
        log.info(imgReviewList.size());
        return imgReviewList;
    }


    /*리뷰 페이지별로 가져오기*/
    public Page<ProdReview> getPagedRevList(int revPage, long prodId) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(revPage, 5, Sort.by(sorts));

        Product product = productRepository.findById(prodId).orElseThrow();

        Page<ProdReview> pagedRevlist = prodReviewRepository.findAllByProduct(product, pageable);

        return pagedRevlist;
    }


    /* Create : 상품 생성 */
    @Transactional
    public Product saveProduct(ProductForm productForm, MultipartFile thumbFile, List<MultipartFile> imageFiles)
            throws IOException {


        Product product = new Product();
        product.setProdName(productForm.getProdName());
        product.setProdCate(productForm.getProdCate());
        product.setProdDesc(productForm.getProdDesc());
        product.setProdStock(productForm.getProdStock());
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


    /* update : 상품 수정 */
    @Transactional
    public void updateProduct(Long prodId, ProductForm productForm, MultipartFile thumbFile,
                              List<MultipartFile> imageFiles)
            throws IOException {

        Product product = getOneProd(prodId);

        prodInfoRepository.deleteAllByProduct(product);
        //prodImageRepository.deleteAllByProduct(product);

        //product.getProdImages().clear();
        //product.getProdInfoList().clear();

        product.setProdName(productForm.getProdName());
        product.setProdCate(productForm.getProdCate());
        product.setProdDesc(productForm.getProdDesc());
        product.setProdStock(productForm.getProdStock());
        product.setProdDiscount(productForm.getProdDiscount());
        product.setProdOrgPrice(productForm.getProdOrgPrice());

        if (productForm.getProdDiscount() > 0) {
            product.setProdFinalPrice(
                    (int) (productForm.getProdOrgPrice() * (1 - product.getProdDiscount()) / 100) * 100);
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

        //product.getProdInfoList().clear();

        /*상품 정보 저장*/
        List<ProdInfo> prodInfoList = new ArrayList<>();
        for (ProdInfo prodInfo : productForm.getProdInfoList()) {
            prodInfo.setProduct(product);
            prodInfoList.add(prodInfo);
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

        productRepository.save(product);

    }

    //상품 삭제
    public void deleteProduct(Long prodId) {
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
    }


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








}
