package com.wellit.project.store;

import java.time.LocalDateTime;
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
        return storeReviewRepository.findByStore_StoId(stoId);
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
    

}