package com.wellit.project.store;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AllStoreRepository extends JpaRepository<AllStore, Long> {
	boolean existsByKakaoStoreId(String kakaoStoreId);
	
	@Query("SELECT s FROM AllStore s JOIN FETCH s.storeReviews r ORDER BY r.createdAt DESC")
	List<AllStore> findAllStoresWithSortedReviews();

	List<AllStore> findByStoVegetarianTypeContainingAndStoRegionProvinceAndStoRegionCity(
            String stoVegetarianType, String stoRegionProvince, String stoRegionCity);
	
    @Query("SELECT s FROM AllStore s WHERE s.stoName LIKE CONCAT('%', :stoName, '%') AND s.stoCategory LIKE CONCAT('%', :stoCategory, '%') AND s.stoVegetarianType = :stoVegetarianType")
    Page<AllStore> findByStoNameContainingAndStoCategoryContainingAndStoVegetarianType(
        @Param("stoName") String stoName, 
        @Param("stoCategory") String stoCategory, 
        @Param("stoVegetarianType") String stoVegetarianType, 
        org.springframework.data.domain.Pageable pageable
    );

    @Query("SELECT s FROM AllStore s WHERE s.stoCategory LIKE CONCAT('%', :stoCategory, '%') AND s.stoVegetarianType = :stoVegetarianType")
    Page<AllStore> findByStoCategoryContainingAndStoVegetarianType(
        @Param("stoCategory") String stoCategory, 
        @Param("stoVegetarianType") String stoVegetarianType, 
        org.springframework.data.domain.Pageable pageable
    );
}