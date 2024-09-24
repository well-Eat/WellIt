package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Console;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/load")
public class AllStoreController {

	@Autowired
	private AllStoreService allStoreService;

	@GetMapping("/store")
	public List<AllStore> getAllStores() {
		return allStoreService.getAllStores();
	}
	
	@GetMapping("/stores")
	public List<AllStore> getAllStores2() {
		return allStoreService.getAllStores();
	}

	@GetMapping("/getUserId")
	public ResponseEntity<Map<String, String>> getUserId(HttpServletRequest request) {
		String userId = (String) request.getSession().getAttribute("UserId");
		Map<String, String> response = new HashMap<>();
		response.put("userId", userId != null ? userId : "로그인되지 않았습니다.");
		return ResponseEntity.ok(response);
	}

	@PostMapping(value = "/store/update", consumes = "multipart/form-data")
	public ResponseEntity<String> updateStore(
	        @RequestParam("stoId") Long stoId,
	        @RequestParam("stoName") String stoName,
	        @RequestParam("stoTitle") String stoTitle,
	        @RequestParam("stoContent") String stoContent,
	        @RequestParam("stoCategory") String stoCategory,
	        @RequestParam("stoRegionProvince") String stoRegionProvince,
	        @RequestParam("stoRegionCity") String stoRegionCity,
	        @RequestParam("stoContact") String stoContact,
	        @RequestParam("stoAddress") String stoAddress,
	        @RequestParam("stoImage") MultipartFile stoImage,
	        @RequestParam("stoOperatingHours") String stoOperatingHours,
	        @RequestParam("stoClosedDays") String stoClosedDays,
	        @RequestParam("stoRecommendedMenu") String stoRecommendedMenu,
	        @RequestParam("stoParkingInfo") String stoParkingInfo,
	        @RequestParam("stoLatitude") Double stoLatitude,
	        @RequestParam("stoLongitude") Double stoLongitude,
	        @RequestParam("kakaoStoreId") String kakaoStoreId,
	        @RequestParam("stoVegetarianType") String stoVegetarianType) {

	    // AllStore 객체 생성
	    AllStore store = new AllStore();
	    store.setStoId(stoId);
	    store.setStoName(stoName);
	    store.setStoTitle(stoTitle);
	    store.setStoContent(stoContent);
	    store.setStoCategory(stoCategory);
	    store.setStoRegionProvince(stoRegionProvince);
	    store.setStoRegionCity(stoRegionCity);
	    store.setStoContact(stoContact);
	    store.setStoAddress(stoAddress);
	    
	    // 이미지 파일 처리
	    if (!stoImage.isEmpty()) {
	        String imageUrl = allStoreService.saveImage(stoImage); // 이미지 저장 후 URL 반환
	        store.setStoImage(imageUrl);
	    }

	    store.setStoOperatingHours(stoOperatingHours);
	    store.setStoClosedDays(stoClosedDays);
	    store.setStoRecommendedMenu(stoRecommendedMenu);
	    store.setStoParkingInfo(stoParkingInfo);
	    store.setStoLatitude(stoLatitude);
	    store.setStoLongitude(stoLongitude);
	    store.setKakaoStoreId(kakaoStoreId);
	    store.setStoVegetarianType(stoVegetarianType);

	    boolean isUpdated = allStoreService.updateStore(store);
	    if (isUpdated) {
	        return ResponseEntity.ok("가게가 성공적으로 수정되었습니다.");
	    } else {
	        return ResponseEntity.status(400).body("가게 수정에 실패했습니다.");
	    }
	}

	// 삭제
	@DeleteMapping("/store/delete/{stoId}")
	public ResponseEntity<Void> deleteStore(@PathVariable("stoId") Long stoId) {
		allStoreService.deleteStoreById(stoId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/store/save")
	public ResponseEntity<AllStore> createStore(@RequestBody AllStore storeData) {

		// 데이터 저장 로직
		AllStore savedStore = allStoreService.saveStore(storeData.getStoName(), storeData.getStoTitle(),
				storeData.getStoContent(), storeData.getStoCategory(), storeData.getStoRegionProvince(),
				storeData.getStoRegionCity(), storeData.getStoContact(), storeData.getStoAddress(),
				storeData.getStoImage(), storeData.getStoOperatingHours(), storeData.getStoClosedDays(),
				storeData.getStoRecommendedMenu(), storeData.getStoParkingInfo(), storeData.getKakaoStoreId(),
				storeData.getStoLatitude(), storeData.getStoLongitude(), storeData.getStoVegetarianType());
		return ResponseEntity.status(HttpStatus.CREATED).body(savedStore);
	}

	@PostMapping("/store/create")
	public ResponseEntity<AllStore> createStore(@RequestParam("stoName") String stoName,
			@RequestParam("stoTitle") String stoTitle, @RequestParam("stoContent") String stoContent,
			@RequestParam("stoCategory") String stoCategory,
			@RequestParam("stoRegionProvince") String stoRegionProvince,
			@RequestParam("stoRegionCity") String stoRegionCity, @RequestParam("stoContact") String stoContact,
			@RequestParam("stoAddress") String stoAddress, @RequestParam("stoImage") MultipartFile stoImage, // 이미지 파일
			@RequestParam("stoOperatingHours") String stoOperatingHours,
			@RequestParam("stoClosedDays") String stoClosedDays,
			@RequestParam("stoRecommendedMenu") String stoRecommendedMenu,
			@RequestParam("stoParkingInfo") String stoParkingInfo, @RequestParam("stoLatitude") Double stoLatitude,
			@RequestParam("stoLongitude") Double stoLongitude,
	        @RequestParam("kakaoStoreId") String kakaoStoreId, // 카카오 스토어 ID 추가
			@RequestParam("stoVegetarianType") String stoVegetarianType) {
		// 이미지 저장 로직 구현
		String imageUrl = allStoreService.saveImage(stoImage); // 이미지 저장 후 URL 반환

		// 데이터 저장 로직
		AllStore savedStore = allStoreService.createStore(stoName, stoTitle, stoContent, stoCategory, stoRegionProvince,
				stoRegionCity, stoContact, stoAddress, imageUrl, // 저장된 이미지 URL 사용
				stoOperatingHours, stoClosedDays, stoRecommendedMenu, stoParkingInfo, stoLatitude, stoLongitude,
				kakaoStoreId, stoVegetarianType);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedStore);
	}

	@GetMapping("/store/save/{uniqueId}")
	public ResponseEntity<Boolean> checkIfExists(@PathVariable("uniqueId") String uniqueId) {
		try {
			boolean exists = allStoreService.existsByKakaoStoreId(uniqueId);
			return ResponseEntity.ok(exists);
		} catch (Exception e) {
			// 로그에 에러 메시지 출력
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
		}
	}

}