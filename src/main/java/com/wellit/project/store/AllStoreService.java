package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AllStoreService {

    @Autowired
    private AllStoreRepository allStoreRepository;
    
    @Autowired
    private StoreReviewRepository storeReviewRepository;

    public List<AllStore> getAllStores() {
        return allStoreRepository.findAll();
    }

    public List<AllStore> getStoresWithSortedReviews() {
        return allStoreRepository.findAllStoresWithSortedReviews();
    }
    
    public AllStore getStoreById(Long stoId) {
    	return allStoreRepository.findById(stoId).orElse(null); // ID로 스토어 찾기
    }

    public AllStore saveStore(String placeName, String title, String content, String category, String city, String district, 
                               String phoneNumber, String address, String image, String operatingHours, String closedDays, String recommendedMenu, String parkingInfo, String uniqueId, 
                               Double lat, Double lng, String type, List<StoreReview> storeReviews) {
    	if (allStoreRepository.existsByKakaoStoreId(uniqueId)) {
            throw new RuntimeException("이 카카오 아이디는 이미 존재합니다.");
        }
        AllStore storeData = new AllStore();

        storeData.setStoName(placeName);
        storeData.setStoTitle(title); // 제목을 추가할 수 있습니다.
        storeData.setStoContent(content); // 내용 추가 (필요하다면)
        storeData.setStoCategory(category);
        storeData.setStoRegionProvince(city); // 도시 정보 추가
        storeData.setStoRegionCity(district); // 구 정보 추가
        storeData.setViewCount(0); // 기본값 설정
        storeData.setRecommendationCount(0); // 기본값 설정
        storeData.setStoContact(phoneNumber);
        storeData.setStoAddress(address);
        storeData.setStoImage(image); // 이미지 URL 추가 (필요하다면)
        storeData.setStoOperatingHours(operatingHours); // 운영시간 (필요하다면)
        storeData.setStoClosedDays(closedDays); // 휴무일 (필요하다면)
        storeData.setStoRecommendedMenu(recommendedMenu); // 추천 메뉴 (필요하다면)
        storeData.setStoParkingInfo(parkingInfo); // 주차 정보 (필요하다면)
        storeData.setStoLatitude(lat);
        storeData.setStoLongitude(lng);
        storeData.setStoVegetarianType(type);
        storeData.setCreatedAt(LocalDateTime.now()); // 생성 날짜
        storeData.setUpdatedAt(LocalDateTime.now()); // 수정 날짜
        storeData.setOpen(true); // 'Y' 대신 boolean 값 사용
        storeData.setKakaoStoreId(uniqueId); // 카카오맵 가게 고유 ID (필요하다면)
        storeData.setStoreReviews(storeReviews);
        return allStoreRepository.save(storeData); // 저장 메소드 호출
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

	public List<AllStore> findStoresByVegetarianAndLocation(String stoVegetarianType, String stoRegionProvince, String stoRegionCity) {
		return allStoreRepository.findByStoVegetarianTypeAndStoRegionProvinceAndStoRegionCity(stoVegetarianType, stoRegionProvince, stoRegionCity);
	}

	public boolean updateStore(AllStore store) {
	    // 가게 정보를 데이터베이스에서 찾아서 수정합니다.
	    AllStore existingStore = allStoreRepository.findById(store.getStoId()).orElse(null);

	    if (existingStore != null) {
	    	existingStore.setStoId(store.getStoId());
	        existingStore.setStoName(store.getStoName());
	        existingStore.setStoTitle(store.getStoTitle());
	        existingStore.setStoContent(store.getStoContent());
	        existingStore.setStoCategory(store.getStoCategory());
	        existingStore.setStoRegionProvince(store.getStoRegionProvince());
	        existingStore.setStoRegionCity(store.getStoRegionCity());
	        existingStore.setStoContact(store.getStoContact());
	        existingStore.setStoAddress(store.getStoAddress());
	        existingStore.setStoImage(store.getStoImage());
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
}