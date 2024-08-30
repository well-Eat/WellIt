package com.wellit.project.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
	List<StoreReview> findByStore_StoId(Long stoId);
}