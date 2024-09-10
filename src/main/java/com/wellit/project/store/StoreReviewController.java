package com.wellit.project.store;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/load/place/{stoId}/reviews")
public class StoreReviewController {

    @Autowired
    private StoreReviewService storeReviewService;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<StoreReview> createReview(
            @PathVariable("stoId") Long stoId,
            @RequestParam("revText") String revText,
            @RequestParam("revRating") int revRating,
            @RequestParam(value = "revImg", required = false) MultipartFile revImg,
            HttpSession session
    		) {
    	
    	
        
    	// 로그인 확인
        String userId = (String) session.getAttribute("UserId");
        if (userId == null) {
            // 로그인하지 않은 경우 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null); // 또는 적절한 에러 객체 반환
        }
    	
        // 리뷰 유효성 검사
        if (revText == null || revText.trim().isEmpty()) {
            return ResponseEntity.badRequest().build(); // 잘못된 요청
        }

        // Store 엔티티 설정
        StoreReview review = new StoreReview();
        review.setRevText(revText);
        review.setRevRating(revRating);
        review.setStore(new AllStore(stoId));
        review.setWriter(userId);
        // 이미지 처리 (예: 파일 저장)
        if (revImg != null && !revImg.isEmpty()) {
            // 이미지 저장 로직 추가 (예: 파일 시스템에 저장하고 URL 반환)
            String imageUrl = saveImage(revImg); // saveImage 메서드는 구현 필요
            review.setRevImg(imageUrl);
        }
        

        StoreReview savedReview = storeReviewService.saveReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReviews(@PathVariable("stoId") Long stoId) {
        List<StoreReview> reviews = storeReviewService.getReviewsByStoreId(stoId);
		return null;
    }
    
    
    @DeleteMapping("/{revId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("revId") Long revId) {
        boolean isDeleted = storeReviewService.deleteReviewById(revId); // 서비스 메서드 호출
        if (isDeleted) {
            return ResponseEntity.ok().build(); // 삭제 성공
        } else {
            return ResponseEntity.notFound().build(); // 리뷰를 찾을 수 없음
        }
    }

    private String saveImage(MultipartFile file) {
        // 저장할 디렉토리 경로
        String directory = "C:/Users/GREEN/git/WellIte/src/main/resources/static/imgs/place"; // 실제 경로로 변경
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // 고유한 파일 이름 생성
        Path filePath = Paths.get(directory, fileName);

        try {
            // 디렉토리가 존재하지 않으면 생성
            Files.createDirectories(filePath.getParent());
            // 파일 저장
            file.transferTo(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 저장 실패 시 null 반환
        }

        // 저장된 파일의 URL 또는 경로 반환
        return "/imgs/place/" + fileName; // 웹에서 접근할 수 있는 URL로 변경
    }
    
}
