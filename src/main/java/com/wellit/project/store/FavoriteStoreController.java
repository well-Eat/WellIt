package com.wellit.project.store;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/store/favorite")
public class FavoriteStoreController {

	@Autowired
	private FavoriteStoreService favoriteStoreService;

	@Autowired FavoriteStoreRepository favoriteStoreRepository;
	
	@Autowired
	private MemberService memberService;

	@Autowired
	private AllStoreService allStoreService;

	@PostMapping("/add")
	public ResponseEntity<String> addFavorite(@RequestParam("memberId") String memberId, @RequestParam("stoId") Long stoId) {
	    try {
	        Member member = memberService.findByMemberId(memberId);
	        AllStore store = allStoreService.findByStoId(stoId);
	        
	        if (member == null) {
	            throw new RuntimeException("로그인해주세요");
	        }else if(store == null) {
	        	throw new RuntimeException("가게 정보를 찾을 수 없습니다.");
	        }
	        
	        favoriteStoreService.addFavoriteStore(member, store);
	        return ResponseEntity.ok("찜하기에 성공했습니다!");
	    } catch (RuntimeException ex) {
	        return ResponseEntity.badRequest().body(ex.getMessage()); // 예외 메시지 반환
	    }
	}

	@DeleteMapping("/remove")
	public void removeFavorite(@RequestParam("memberId") String memberId, @RequestParam("stoId") Long stoId) {
		Member member = memberService.findByMemberId(memberId);
		AllStore store = allStoreService.findByStoId(stoId);
		if (member != null && store != null) {
			favoriteStoreService.removeFavoriteStore(member, store);
		} else {
			throw new RuntimeException("멤버 또는 스토어를 찾을 수 없습니다.");
		}
	}

	@GetMapping("/list")
	public List<AllStore> getFavoriteStores(@RequestParam("memberId") String memberId) {
        return favoriteStoreService.getFavoriteStores(memberId);
	}
}