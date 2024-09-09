package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/load/store")
public class AllStoreController {
	

    @Autowired
    private AllStoreService allStoreService;

    @GetMapping
    public List<AllStore> getAllStores() {
        return allStoreService.getAllStores();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AllStore> getStoreById(@PathVariable Long id) {
        return allStoreService.getStoreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
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
            storeData.getStoLongitude()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(savedStore);
    }
    @GetMapping("/save/")
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

    @PutMapping("/{id}")
    public ResponseEntity<AllStore> updateStore(@PathVariable Long id, @RequestBody AllStore storeDetails) {
        AllStore updatedStore = allStoreService.updateStore(id, storeDetails);
        return updatedStore != null ? ResponseEntity.ok(updatedStore) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Long id) {
        allStoreService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }
}