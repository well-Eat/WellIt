package com.study.project.store;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/load")
public class StoreController {

    @Autowired
    private StoreService storeService;

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
}