package com.wellit.project.store;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreReviewService {

    @Autowired
    private StoreReviewRepository storeReviewRepository;

    public StoreReview saveReview(StoreReview review) {
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        return storeReviewRepository.save(review);
    }

    public List<StoreReview> getReviewsByStoreId(Long stoId) {
        List<StoreReview> reviews = storeReviewRepository.findByStore_StoId(stoId);
        // 생성일자 기준으로 내림차순 정렬
        reviews.sort(Comparator.comparing(StoreReview::getCreatedAt).reversed());

        return reviews;
    }

    public void deleteReview(Long revId) {
        storeReviewRepository.deleteById(revId);
    }
    
    public boolean deleteReviewById(Long revId) {
        if (storeReviewRepository.existsById(revId)) {
        	storeReviewRepository.deleteById(revId); // 리뷰 삭제
            return true;
        }
        return false; // 리뷰가 존재하지 않음
    }
    
 // 추가된 메서드
    public void deleteReviewsByStoreId(Long stoId) {
        storeReviewRepository.deleteByStore_StoId(stoId); // 특정 가게의 모든 리뷰 삭제
    }
    
    public List<AllStore> getStoresWithSortedReviews() {
        return storeReviewRepository.findAllStoresWithSortedReviews();
    }

}