package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.Console;
import java.util.List;

@RestController
@RequestMapping("/load")
public class AllStoreController {
	

    @Autowired
    private AllStoreService allStoreService;

    @GetMapping("/store")
    public List<AllStore> getAllStores() {
        return allStoreService.getAllStores();
    }
    
    @PostMapping("/store/update")
    public ResponseEntity<String> updateStore(@RequestBody AllStore store) {
        boolean isUpdated = allStoreService.updateStore(store);
        if (isUpdated) {
            return ResponseEntity.ok("가게가 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.status(400).body("가게 수정에 실패했습니다.");
        }
    }
    
    //삭제
    @DeleteMapping("/store/delete/{stoId}")
    public ResponseEntity<Void> deleteStore(@PathVariable("stoId") Long stoId) {
        allStoreService.deleteStoreById(stoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/store/save")
    public ResponseEntity<AllStore> createStore(@RequestBody AllStore storeData) {

        // 데이터 저장 로직
        AllStore savedStore = allStoreService.saveStore(
            storeData.getStoName(),
            storeData.getStoTitle(),
            storeData.getStoContent(),
            storeData.getStoCategory(),
            storeData.getStoRegionProvince(),
            storeData.getStoRegionCity(),
            storeData.getStoContact(),
            storeData.getStoAddress(),
            storeData.getStoImage(),
            storeData.getStoOperatingHours(),
            storeData.getStoClosedDays(),
            storeData.getStoRecommendedMenu(),
            storeData.getStoParkingInfo(),
            storeData.getKakaoStoreId(),
            storeData.getStoLatitude(),
            storeData.getStoLongitude(),
            storeData.getStoVegetarianType(),
            storeData.getStoreReviews()
        );
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