package com.wellit.project.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
	List<StoreReview> findByStore_StoId(Long stoId);

	@Query("SELECT s FROM AllStore s JOIN FETCH s.storeReviews r ORDER BY r.createdAt DESC")
	List<AllStore> findAllStoresWithSortedReviews();

	void deleteByStore_StoId(Long stoId);

}