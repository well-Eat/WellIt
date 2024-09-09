package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AllStoreService {

    @Autowired
    private AllStoreRepository allStoreRepository;

    public List<AllStore> getAllStores() {
        return allStoreRepository.findAll();
    }

    public Optional<AllStore> getStoreById(Long id) {
        return allStoreRepository.findById(id);
    }

    public AllStore saveStore(String placeName, String title, String content, String category, String city, String district, 
                               String phoneNumber, String address, String image, String operatingHours, String closedDays, String recommendedMenu, String parkingInfo, String uniqueId, 
                               Double lat, Double lng) {
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
        storeData.setCreatedAt(LocalDateTime.now()); // 생성 날짜
        storeData.setUpdatedAt(LocalDateTime.now()); // 수정 날짜
        storeData.setOpen(true); // 'Y' 대신 boolean 값 사용
        storeData.setKakaoStoreId(uniqueId); // 카카오맵 가게 고유 ID (필요하다면)

        return allStoreRepository.save(storeData); // 저장 메소드 호출
    }
    public boolean existsByKakaoStoreId(String kakaoStoreId) {
        return allStoreRepository.existsByKakaoStoreId(kakaoStoreId);
    }

    public void deleteStore(Long id) {
        allStoreRepository.deleteById(id);
    }

    public AllStore updateStore(Long id, AllStore storeDetails) {
        Optional<AllStore> optionalStore = allStoreRepository.findById(id);
        if (optionalStore.isPresent()) {
            AllStore store = optionalStore.get();
            // 필요한 필드 업데이트
            store.setStoName(storeDetails.getStoName());
            store.setStoTitle(storeDetails.getStoTitle());
            // ... 나머지 필드 업데이트
            return allStoreRepository.save(store);
        }
        return null;
    }
}