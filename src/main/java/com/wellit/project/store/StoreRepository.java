package com.wellit.project.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {
	@Query("SELECT s FROM Store s LEFT JOIN FETCH s.storeReviews sr ORDER BY sr.createdAt DESC")
    List<Store> findAllStoresWithSortedReviews();
}