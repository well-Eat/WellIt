package com.wellit.project.store;

import com.wellit.project.member.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteStoreService {

    @Autowired
    private FavoriteStoreRepository favoriteStoreRepository;
    
    @Autowired
    private AllStoreRepository allStoreRepository;

    public void addFavoriteStore(Member member, AllStore store) {
        // 해당 사용자의 찜 목록에서 스토어 ID가 이미 존재하는지 확인
        List<FavoriteStore> existingFavorites = favoriteStoreRepository.findByMember_MemberId(member.getMemberId());
        
        boolean alreadyExists = existingFavorites.stream()
            .anyMatch(favorite -> favorite.getStore().getStoId().equals(store.getStoId()));
        
        if (alreadyExists) {
            throw new RuntimeException("이미 찜한 스토어입니다."); // 중복 찜 시 예외 처리
        }

        // 찜 추가 로직
        FavoriteStore favoriteStore = new FavoriteStore();
        favoriteStore.setMember(member);
        favoriteStore.setStore(store);
        favoriteStoreRepository.save(favoriteStore); // 찜 저장
    }

    public void removeFavoriteStore(Member member, AllStore store) {
        FavoriteStore favoriteStore = favoriteStoreRepository.findByMemberAndStore(member, store);
        if (favoriteStore != null) {
            favoriteStoreRepository.delete(favoriteStore);
        }
    }

    public List<FavoriteStore> getFavoriteStoresByMember(Member member) {
        return favoriteStoreRepository.findAllByMember(member);
    }
    
    public List<AllStore> getFavoriteStores(String memberId) {
        List<FavoriteStore> favorites = favoriteStoreRepository.findByMember_MemberId(memberId);
        List<AllStore> allStores = new ArrayList<>();

        for (FavoriteStore favorite : favorites) {
            Long storeId = favorite.getStore().getStoId();  // storeId 가져오기
            Optional<AllStore> optionalStore = allStoreRepository.findById(storeId);  // AllStore 정보 가져오기
            
            optionalStore.ifPresent(allStores::add); // Optional에서 값이 존재하면 allStores에 추가
        }

        return allStores;
    }

	public List<FavoriteStore> getFavoriteStoresByMember(String memberId) {
		
        return favoriteStoreRepository.findByMember_MemberId(memberId); // ID로 즐겨찾기 목록 조회
	}
}