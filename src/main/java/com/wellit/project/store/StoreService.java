package com.wellit.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}