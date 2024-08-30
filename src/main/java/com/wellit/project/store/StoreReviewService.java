package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StoreReviewService {

    private final StoreReviewRepository storeReviewRepository;

    @Autowired
    public StoreReviewService(StoreReviewRepository storeReviewRepository) {
        this.storeReviewRepository = storeReviewRepository;
    }

    public List<StoreReview> getAllReviews() {
        return storeReviewRepository.findAll();
    }

    public StoreReview getReviewById(Integer revId) {
        return storeReviewRepository.findById(revId).orElse(null);
    }

    public StoreReview createReview(StoreReview storeReview) {
        storeReview.setCreatedAt(LocalDateTime.now());
        return storeReviewRepository.save(storeReview);
    }

    public StoreReview updateReview(Integer revId, StoreReview updatedReview) {
        StoreReview existingReview = storeReviewRepository.findById(revId).orElse(null);
        if (existingReview != null) {
            existingReview.setRevImg(updatedReview.getRevImg());
            existingReview.setRevText(updatedReview.getRevText());
            existingReview.setRevRating(updatedReview.getRevRating());
            existingReview.setUpdatedAt(LocalDateTime.now());
            existingReview.setWriter(updatedReview.getWriter());
            return storeReviewRepository.save(existingReview);
        }
        return null;
    }

    public void deleteReview(Integer revId) {
        storeReviewRepository.deleteById(revId);
    }
    
    public List<StoreReview> getAllReviewsByPlaceId(Integer placeId) {
        // 장소 ID에 따라 리뷰를 가져오는 로직
        return storeReviewRepository.findByStoreId(placeId);
    }
    
    
}
