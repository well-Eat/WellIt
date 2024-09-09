package com.wellit.project.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllStoreRepository extends JpaRepository<AllStore, Long> {
	boolean existsByKakaoStoreId(String kakaoStoreId);
}