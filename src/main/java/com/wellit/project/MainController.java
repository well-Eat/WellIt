package com.wellit.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import com.wellit.project.life.Recipe;
import com.wellit.project.life.RecipeService;
import com.wellit.project.store.AllStore;
import com.wellit.project.store.AllStoreService;

@Controller
public class MainController {
	
	@Autowired
    private AllStoreService storeService;
	
	@Autowired
	private RecipeService recipeService;

	@GetMapping("/")
	public String index(Model model) {
	    List<AllStore> topStores = storeService.getTop4Stores(); // 상위 4개 가게 목록 가져오기
	    model.addAttribute("stores", topStores); // 모델에 가게 목록 추가

	    List<Recipe> recipes = recipeService.getAllRecipes(); // 모든 레시피 가져오기
	    List<Recipe> reverseRecipes = new ArrayList<>(recipes); // 원본 리스트를 복사하여 새 리스트 생성
	    Collections.reverse(reverseRecipes); // 새 리스트를 역순으로 변경

	    model.addAttribute("recipes", recipes); // 원본 리스트를 모델에 추가
	    model.addAttribute("reverseRecipes", reverseRecipes); // 역순 리스트를 모델에 추가

	    return "index"; // 인덱스 페이지로 이동
	}

	// 로그인 오류 창 후 로그인 페이지로 리다이렉트
	@GetMapping("/login-alert")
	public String loginAlert() {
		return "/error/login_alert";
	}



}
