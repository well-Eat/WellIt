package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wellit.project.shop.Product;
import com.wellit.project.shop.ProductAdminDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AllStoreService {

	@Autowired
	private AllStoreRepository allStoreRepository;

	@Autowired
	private StoreReviewRepository storeReviewRepository;

	public List<AllStore> getAllStores() {
		return allStoreRepository.findAll();
	}

	// 가게 정보를 4개만 가져오는 메서드 추가
	public List<AllStore> getTop4Stores() {
		return allStoreRepository.findAll().stream().limit(4) // 상위 4개만 가져오기
				.toList(); // 리스트로 변환
	}

	public List<AllStore> getStoresWithSortedReviews() {
		return allStoreRepository.findAllStoresWithSortedReviews();
	}

	public AllStore getStoreById(Long stoId) {
		return allStoreRepository.findById(stoId).orElse(null); // ID로 스토어 찾기
	}

	public AllStore saveStore(String placeName, String title, String content, String category, String city,
			String district, String phoneNumber, String address, String image, String operatingHours, String closedDays,
			String recommendedMenu, String parkingInfo, String uniqueId, Double lat, Double lng, String type) {
		if (allStoreRepository.existsByKakaoStoreId(uniqueId)) {
			throw new RuntimeException("이 카카오 아이디는 이미 존재합니다.");
		}
		AllStore storeData = new AllStore();

		storeData.setStoName(placeName);
		storeData.setStoTitle(title);
		storeData.setStoContent(content);
		storeData.setStoCategory(category);
		storeData.setStoRegionProvince(city);
		storeData.setStoRegionCity(district);
		storeData.setViewCount(0);
		storeData.setRecommendationCount(0);
		storeData.setStoContact(phoneNumber);
		storeData.setStoAddress(address);
		storeData.setStoImage(image);
		storeData.setStoOperatingHours(operatingHours);
		storeData.setStoClosedDays(closedDays);
		storeData.setStoRecommendedMenu(recommendedMenu);
		storeData.setStoParkingInfo(parkingInfo);
		storeData.setStoLatitude(lat);
		storeData.setStoLongitude(lng);
		storeData.setStoVegetarianType(type);
		storeData.setCreatedAt(LocalDateTime.now());
		storeData.setUpdatedAt(LocalDateTime.now());
		storeData.setOpen(true);
		storeData.setKakaoStoreId(uniqueId);

		return allStoreRepository.save(storeData);
	}

	public boolean existsByKakaoStoreId(String kakaoStoreId) {
		return allStoreRepository.existsByKakaoStoreId(kakaoStoreId);
	}

	@Transactional
	public void deleteStoreById(Long stoId) {
		storeReviewRepository.deleteByStore_StoId(stoId); // 필요한 메서드 추가
		allStoreRepository.deleteById(stoId);
	}

	public AllStore saveStore(AllStore store) {
		return allStoreRepository.save(store);
	}

	public AllStore incrementViewCount(Long stoId) {
		AllStore store = getStoreById(stoId);
		if (store != null) {
			store.setViewCount(store.getViewCount() + 1); // 조회수 증가
			allStoreRepository.save(store); // 변경사항 저장
		}
		return store;
	}

	public List<AllStore> findStoresByVegetarianAndLocation(String stoVegetarianType, String stoRegionProvince,
			String stoRegionCity) {
		return allStoreRepository.findByStoVegetarianTypeAndStoRegionProvinceAndStoRegionCity(stoVegetarianType,
				stoRegionProvince, stoRegionCity);
	}

	public boolean updateStore(AllStore store) {
	    // 가게 정보를 데이터베이스에서 찾아서 수정합니다.
	    AllStore existingStore = allStoreRepository.findById(store.getStoId()).orElse(null);

	    if (existingStore != null) {
	        existingStore.setStoName(store.getStoName());
	        existingStore.setStoTitle(store.getStoTitle());
	        existingStore.setStoContent(store.getStoContent());
	        existingStore.setStoCategory(store.getStoCategory());
	        existingStore.setStoRegionProvince(store.getStoRegionProvince());
	        existingStore.setStoRegionCity(store.getStoRegionCity());
	        existingStore.setStoContact(store.getStoContact());
	        existingStore.setStoAddress(store.getStoAddress());

	        // 이미지 업데이트
	        if (store.getStoImage() != null) {
	            existingStore.setStoImage(store.getStoImage());
	        }

	        existingStore.setStoOperatingHours(store.getStoOperatingHours());
	        existingStore.setStoClosedDays(store.getStoClosedDays());
	        existingStore.setStoRecommendedMenu(store.getStoRecommendedMenu());
	        existingStore.setStoParkingInfo(store.getStoParkingInfo());
	        existingStore.setStoLatitude(store.getStoLatitude());
	        existingStore.setStoLongitude(store.getStoLongitude());
	        existingStore.setStoVegetarianType(store.getStoVegetarianType());
	        existingStore.setKakaoStoreId(store.getKakaoStoreId());

	        allStoreRepository.save(existingStore);
	        return true;
	    }
	    return false;
	}

	// stoId로 스토어 조회
	public AllStore findByStoId(Long stoId) {
		Optional<AllStore> storeOpt = allStoreRepository.findById(stoId);
		return storeOpt.orElse(null); // 스토어가 존재하지 않으면 null 반환
	}

	public String saveImage(MultipartFile stoImage) {
		String directory = "C:/Users/GREEN/git/WellIt/src/main/resources/static/imgs/map"; // 실제 경로로 변경
		String fileName = System.currentTimeMillis() + "_" + stoImage.getOriginalFilename();
		Path filePath = Paths.get(directory, fileName);

		try {
			Files.createDirectories(filePath.getParent());
			stoImage.transferTo(filePath);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return "/imgs/map/" + fileName;
	}

	public AllStore createStore(String stoName, String stoTitle, String stoContent, String stoCategory,
			String stoRegionProvince, String stoRegionCity, String stoContact, String stoAddress, String imageUrl,
			String stoOperatingHours, String stoClosedDays, String stoRecommendedMenu, String stoParkingInfo,
			Double stoLatitude, Double stoLongitude, String kakaoStoreId, String stoVegetarianType) {
// 가게 정보를 저장하는 로직 구현 (예: 데이터베이스에 저장)
		AllStore store = new AllStore();
		store.setStoName(stoName);
		store.setStoTitle(stoTitle);
		store.setStoContent(stoContent);
		store.setStoCategory(stoCategory);
		store.setStoRegionProvince(stoRegionProvince);
		store.setStoRegionCity(stoRegionCity);
		store.setStoContact(stoContact);
		store.setStoAddress(stoAddress);
		store.setStoImage(imageUrl);
		store.setStoOperatingHours(stoOperatingHours);
		store.setStoClosedDays(stoClosedDays);
		store.setStoRecommendedMenu(stoRecommendedMenu);
		store.setStoParkingInfo(stoParkingInfo);
		store.setStoLatitude(stoLatitude);
		store.setStoLongitude(stoLongitude);
	    store.setKakaoStoreId(kakaoStoreId); // 카카오 스토어 ID 설정
		store.setStoVegetarianType(stoVegetarianType);
		allStoreRepository.save(store);

// 데이터베이스에 저장하는 로직 추가 (예: repository.save(store))
// return saved store
		return store; // 저장된 가게 객체 반환
	}
	
	
	//랜덤으로 가게 가져오기
	public List<AllStore> getRandomStores(List<AllStore> allStores) {
        Collections.shuffle(allStores); // 리스트를 랜덤하게 섞음
        return allStores.stream().limit(20).collect(Collectors.toList()); // 상위 20개 선택
    }

	public Page<AllStore> findStores(String stoName, String stoCategory, String stoVegetarianType, int page, int pageSize) {
	    Pageable pageable = PageRequest.of(page - 1, pageSize); // 페이지 요청 생성

        // 검색 조건에 따라 가게 정보를 조회
        if (stoName != null && !stoName.isEmpty()) {
            return allStoreRepository.findByStoNameContainingAndStoCategoryContainingAndStoVegetarianType(stoName, stoCategory, stoVegetarianType, pageable);
        } else {
            return allStoreRepository.findByStoCategoryContainingAndStoVegetarianType(stoCategory, stoVegetarianType, pageable);
        }
    }

	public Page<AllStore> findAllStores(int page, int pageSize) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page - 1, pageSize); // 페이지는 0부터 시작하므로 -1

        // 모든 가게를 페이지네이션하여 반환
        return allStoreRepository.findAll(pageable);
    }
	
	
}