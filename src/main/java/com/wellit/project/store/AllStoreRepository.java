package com.wellit.project.store;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AllStoreRepository extends JpaRepository<AllStore, Long> {
	boolean existsByKakaoStoreId(String kakaoStoreId);
	
	@Query("SELECT s FROM AllStore s JOIN FETCH s.storeReviews r ORDER BY r.createdAt DESC")
	List<AllStore> findAllStoresWithSortedReviews();

    List<AllStore> findByStoVegetarianTypeAndStoRegionProvinceAndStoRegionCity(String stoVegetarianType, String stoRegionProvince, String stoRegionCity);
}