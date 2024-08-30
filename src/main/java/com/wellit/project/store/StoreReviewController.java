package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/load/place/{sto_id}/reviews") // 장소 ID를 포함한 경로
public class StoreReviewController {

    private final StoreReviewService storeReviewService;

    @Autowired
    public StoreReviewController(StoreReviewService storeReviewService) {
        this.storeReviewService = storeReviewService;
    }

    @GetMapping
    public List<StoreReview> getAllReviews(@PathVariable Integer sto_id) {
        // 특정 장소에 대한 리뷰를 가져오는 로직을 추가해야 합니다.
        return storeReviewService.getAllReviewsByPlaceId(sto_id);
    }

    @GetMapping("/{revId}")
    public ResponseEntity<StoreReview> getReviewById(@PathVariable Integer sto_id, @PathVariable Integer revId) {
        StoreReview review = storeReviewService.getReviewById(revId);
        if (review != null) {
            return ResponseEntity.ok(review);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public StoreReview createReview(@PathVariable Integer sto_id, @RequestBody StoreReview storeReview) {
        // 장소 ID를 설정하여 리뷰를 생성합니다.
        storeReview.setStore(new Store(sto_id)); // Store 객체를 생성하여 설정
        return storeReviewService.createReview(storeReview);
    }

    @PutMapping("/{revId}")
    public ResponseEntity<StoreReview> updateReview(@PathVariable Integer sto_id, @PathVariable Integer revId, @RequestBody StoreReview updatedReview) {
        StoreReview review = storeReviewService.updateReview(revId, updatedReview);
        if (review != null) {
            return ResponseEntity.ok(review);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{revId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer sto_id, @PathVariable Integer revId) {
        storeReviewService.deleteReview(revId);
        return ResponseEntity.noContent().build();
    }
}
