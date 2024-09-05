package com.wellit.project.store;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/load")
public class StoreController {

    @Autowired
    private StoreService storeService;
    
    @Autowired
    private StoreReviewService storeReviewService;

    @GetMapping("/map")
	public String getMap() {
		return "load/map";
	}
    
    @GetMapping("/place")
    public String getPlaces(Model model) {
        List<Store> stores = storeService.getAllStores(); // 데이터 가져오기
        model.addAttribute("stores", stores); // 모델에 데이터 추가
        return "load/place"; // place.html로 이동 (슬래시 제거)
    }

    @PostMapping("/place")
    public String addStore(Store store) {
        storeService.saveStore(store);
        return "redirect:/load"; // 저장 후 다시 목록으로 리다이렉트 (경로 수정)
    }
    
    @GetMapping("/place/{stoId}")
    public String getStoreDetail(@PathVariable("stoId") Long stoId, Model model) {
        System.out.println("Requested store ID: " + stoId); // 로그 추가
        // 조회수 증가
        Store store = storeService.incrementViewCount(stoId);
        
        if (store == null) {
            return "error/store_not_found"; // 에러 페이지 경로
        }
        
        // 리뷰 가져오기
        List<StoreReview> reviews = storeReviewService.getReviewsByStoreId(stoId);
        
        // 필터링된 리뷰 리스트 생성
        List<StoreReview> filteredReviews = reviews.stream()
            .filter(review -> review.getRevImg() != null)
            .sorted(Comparator.comparing(StoreReview::getCreatedAt).reversed())
            .collect(Collectors.toList());
        
        // 평균 평점 계산
        double totalRating = reviews.stream()
            .mapToDouble(StoreReview::getRevRating) // 각 리뷰의 평점을 가져옴
            .sum(); // 평점의 총합
        double averageRating = reviews.size() > 0 ? totalRating / reviews.size() : 0; // 평균 평점 계산

        // 소수점 한 자리로 포맷팅
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedAverageRating = df.format(averageRating); // 포맷팅된 평균 평점

        // 평균 평점을 Store 객체에 세팅 (소수점 한 자리로 변환)
        store.setStoRecommendationCount(Double.parseDouble(formattedAverageRating)); // 평균 평점을 stoRecommendationCount에 세팅

        // 모델에 스토어, 필터링된 리뷰 추가
        model.addAttribute("store", store);
        model.addAttribute("filteredReviews", filteredReviews);
        model.addAttribute("filteredReviewsCount", filteredReviews.size());

        return "load/place_detail"; // 디테일 페이지로 이동
    }
    
}