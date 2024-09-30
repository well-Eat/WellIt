package com.wellit.project.store;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/load")
public class StoreController {

    @Autowired
    private StoreService storeService;
    
    @Autowired
    private StoreReviewService storeReviewService;
    
    @Autowired
    private AllStoreService allStoreService;
    
    @Autowired
    private FavoriteStoreService favoriteStoreService;
    
    @Autowired
    private MemberService memberService;

    @GetMapping("/map")
	public String getMap() {
		return "load/map";
	}
    
    @GetMapping("/localmap")
	public String getlocalMap() {
		return "load/localmap";
	}
    
    @GetMapping("/favoriteStore")
    public String getFavoriteStore() {
    	return "load/favoriteStore";
    }
    
    @GetMapping("/store/create")
    public String createStore() {
    	return "manager/storeForm";
    }
    
    @GetMapping("/store/update")
    public String updateStore(@RequestParam("id") Long stoId, Model model) {
        // stoId에 해당하는 가게 정보를 가져오는 로직 추가
        AllStore store = allStoreService.getStoreById(stoId);
        model.addAttribute("store", store);
        return "manager/storeUpdateForm";
    }
    
    @PostMapping("/recommend")
    public String recommend(@RequestParam("vegetarian") String stoVegetarianType, 
                            @RequestParam("sido") String stoRegionProvince, 
                            @RequestParam("sigungu") String stoRegionCity, 
                            Model model, HttpSession session) {
        
        try {
        	String memberId = (String) session.getAttribute("UserId"); // 세션에서 사용자 ID 가져오기

        	List<AllStore> findStores = allStoreService.findStoresByVegetarianAndLocation(stoVegetarianType, stoRegionProvince, stoRegionCity);
            model.addAttribute("findStores", findStores);
            List<AllStore> stores = allStoreService.getAllStores(); // 데이터 가져오기
            model.addAttribute("stores", stores); // 모델에 데이터 추가
            if (memberId != null) {
                List<FavoriteStore> favoriteStores = favoriteStoreService.getFavoriteStoresByMember(memberId); // 즐겨찾기 목록 가져오기
                model.addAttribute("favoriteStores", favoriteStores); // 모델에 추가
            }
            return "load/place"; // 추천 결과를 보여줄 뷰 이름
        } catch (Exception e) {
            // 에러 처리 로직
            model.addAttribute("errorMessage", "데이터를 불러오는 중 오류가 발생했습니다.");
            return "load/place"; // 에러 페이지로 리다이렉트
        }
        
    }
    
    @GetMapping("/place")
    public String getPlaces(Model model, HttpSession session) {
    	String memberId = (String) session.getAttribute("UserId"); // 세션에서 사용자 ID 가져오기

        List<AllStore> stores = allStoreService.getAllStores(); // 데이터 가져오기
        model.addAttribute("stores", stores); // 모델에 데이터 추가
        List<AllStore> randomStores = allStoreService.getRandomStores(stores); // 서비스 메서드 호출
        model.addAttribute("randomStores", randomStores); // 모델에 추가
        
        if (memberId != null) {
            List<FavoriteStore> favoriteStores = favoriteStoreService.getFavoriteStoresByMember(memberId); // 즐겨찾기 목록 가져오기
            model.addAttribute("favoriteStores", favoriteStores); // 모델에 추가
        }
        
        return "load/place"; // place.html로 이동 (슬래시 제거)
    }

    @PostMapping("/place")
    public String addStore(AllStore store) {
    	allStoreService.saveStore(store);
        return "redirect:/load"; // 저장 후 다시 목록으로 리다이렉트 (경로 수정)
    }
    
    @GetMapping("/place/{stoId}")
    public String getStoreDetail(@PathVariable("stoId") Long stoId, Model model) {
        System.out.println("Requested store ID: " + stoId); // 로그 추가
        // 조회수 증가
        AllStore store = allStoreService.incrementViewCount(stoId);
        
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
        store.setRecommendationCount(Double.parseDouble(formattedAverageRating)); // 평균 평점을 stoRecommendationCount에 세팅

        // 영업 시간 체크
        String operatingHours = store.getStoOperatingHours(); // "07:00 - 19:00"
        String[] hours = operatingHours.split(" - ");
        
        // 시작 시간과 종료 시간 파싱
        LocalTime openingTime = LocalTime.parse(hours[0], DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime closingTime = LocalTime.parse(hours[1], DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime now = LocalTime.now();
        
        // 영업 여부 판단
        boolean isOpen = (now.isAfter(openingTime) && now.isBefore(closingTime));
        store.setOpen(isOpen); // 영업 여부를 Store 객체에 세팅
        // 모델에 스토어, 필터링된 리뷰 추가
        model.addAttribute("store", store);
        model.addAttribute("filteredReviews", filteredReviews);
        model.addAttribute("filteredReviewsCount", filteredReviews.size());

        allStoreService.updateStore(store);
        
        return "load/place_detail"; // 디테일 페이지로 이동
    }
    
 // admin:상품 리스트 이동
    @GetMapping("/admin/store/list")
    public String getAdminProductList(Model model) {
        String memberId = memberService.getMemberId();
        // 현재 로그인한 사용자가 admin인지 확인
        if (memberId == null || !"admin".equals(memberId)) {
            return "/order/admin_po_list";  // 상품 리스트 페이지로 리다이렉트
        }

        return "/load/admin_storeList";
    }

    
}