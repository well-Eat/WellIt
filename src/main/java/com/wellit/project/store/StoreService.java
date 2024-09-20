package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }
    
    public Store getStoreById(Long stoId) {
    	
        return storeRepository.findById(stoId).orElse(null); // ID로 스토어 찾기
    }
    
    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

  
    
    public Store incrementViewCount(Long stoId) {
        Store store = getStoreById(stoId);
        if (store != null) {
            store.setStoViewCount(store.getStoViewCount() + 1); // 조회수 증가
            storeRepository.save(store); // 변경사항 저장
        }
        return store;
    }


}