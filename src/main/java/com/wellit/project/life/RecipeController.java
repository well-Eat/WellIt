package com.wellit.project.life;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.shop.ProdCnt;
import com.wellit.project.shop.Product;
import com.wellit.project.shop.ShopService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/life")
@Log4j2
@RequiredArgsConstructor
public class RecipeController {

	private final RecipeService recipeService;

	@Autowired
	private final FavoriteRecipeService favoriteRecipeService;

	@Autowired
	private final MemberService memberService;
	
	@Autowired
	private final ShopService shopService;

	private String UPLOAD_DIR = "C:\\Users\\GREEN\\git\\WellIt\\src\\main\\resources\\static\\imgs\\life\\recipe\\";

	@GetMapping("/wellit")
	public String getWellit() {
		return "life/wellit";
	}

	@GetMapping("/recipe/list")
	public String getRecipeList(@RequestParam(value = "servings", required = false) String servings,
			@RequestParam(value = "cookTime", required = false) String cookTime,
			@RequestParam(value = "difficulty", required = false) String difficulty,
			@RequestParam(value = "tags", required = false) String tags,
			@RequestParam(value = "userId", required = false) String userId, // userId 파라미터 추가
			Model model) {

		List<Recipe> recipeList = recipeService.getAllRecipes(); // 모든 레시피를 가져오는 서비스 메서드

		// 필터링 로직
		if (servings != null && !servings.equals("all")) {
			Integer servingsValue = Integer.parseInt(servings); // String을 Integer로 변환
			recipeList = recipeList.stream().filter(recipe -> recipe.getServings().equals(servingsValue)) // 비교
					.collect(Collectors.toList());
		}

		// 소요 시간 필터링
		if (cookTime != null && !cookTime.equals("all")) {
			recipeList = recipeList.stream().filter(recipe -> filterByCookTime(recipe.getCookTime(), cookTime))
					.collect(Collectors.toList());
		}

		// 난이도 필터링
		if (difficulty != null && !difficulty.equals("all")) {
			recipeList = recipeList.stream().filter(recipe -> recipe.getDifficulty().equals(difficulty))
					.collect(Collectors.toList());
		}

		// 태그 필터링
		if (tags != null && !tags.isEmpty()) {
			// 입력된 태그를 '# ' 기준으로 나누고, 공백 제거
			List<String> tagList = Arrays.stream(tags.split(" ")).map(String::trim).filter(tag -> !tag.isEmpty()) // 빈
																													// 태그
																													// 제거
					.collect(Collectors.toList());

			recipeList = recipeList.stream()
					.filter(recipe -> tagList.stream().anyMatch(tag -> recipe.getRecpTags().contains(tag)))
					.collect(Collectors.toList());
		}

		// userId가 있을 경우, 해당 사용자 작성 레시피만 필터링
		if (userId != null) {
			recipeList = recipeList.stream().filter(recipe -> recipe.getWriter().equals(userId))
					.collect(Collectors.toList());
		}

		model.addAttribute("recipes", recipeList); // 모델에 필터링된 레시피 리스트 추가
		return "life/recp_list"; // 레시피 목록을 보여줄 뷰 이름
	}

	private boolean filterByCookTime(int cookTimeValue, String cookTime) {
		switch (cookTime) {
		case "0-5":
			return cookTimeValue < 5;
		case "5-10":
			return cookTimeValue >= 5 && cookTimeValue < 10;
		case "10-20":
			return cookTimeValue >= 10 && cookTimeValue < 20;
		case "20-30":
			return cookTimeValue >= 20 && cookTimeValue < 30;
		case "30+":
			return cookTimeValue >= 30;
		default:
			return false;
		}
	}

	/* 레시피 등록 폼 OPEN */
	@GetMapping("/recipe/create")
	public String createRecipe(Model model, HttpSession session) {
	    // 세션에서 UserId를 확인
	    if (session.getAttribute("UserId") == null) {
	        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
	        return "redirect:/member/login_trial";
	    }
	    
	    model.addAttribute("recipeForm", new RecipeForm());
	    return "/life/recp_create";
	}

	/* 새 레시피 등록 */
	@PostMapping("/recipe/create")
	public String postRecipe(@Valid @ModelAttribute RecipeForm recipeForm, BindingResult bindingResult, Model model,
			HttpSession session) throws IOException {
		if (bindingResult.hasErrors()) {

			// 필드 에러를 출력
			bindingResult.getFieldErrors().forEach(error -> {
				log.info("Field Error: Field = {}, Rejected Value = {}, Message = {}", error.getField(),
						error.getRejectedValue(), error.getDefaultMessage());
			});

			// 글로벌 에러를 출력 (객체 수준의 에러)
			bindingResult.getGlobalErrors().forEach(error -> {
				log.info("Global Error: Object = {}, Message = {}", error.getObjectName(), error.getDefaultMessage());
			});

			return "/life/recp_create"; // 유효성 검사 실패 시 다시 폼으로 이동
		}

		// 로그인한 사용자 ID 가져오기
		String userId = (String) session.getAttribute("UserId"); // 세션에서 사용자 ID 가져오기

		// Recipe 저장
		Recipe recipe = new Recipe();
		recipe.setWriter(userId); // writer에 사용자 ID 설정
		recipe.setCookTime(recipeForm.getCookTime());
		recipe.setServings(recipeForm.getServings());
		recipe.setRecpName(recipeForm.getRecpName());
		recipe.setRecpIntroduce(recipeForm.getRecpIntroduce());
		recipe.setRecpTags(recipeForm.getRecpTags());
		recipe.setDifficulty(recipeForm.getDifficulty());
		/*
		 * recipe.setBookmark(0); recipe.setHeart(0); recipe.setView(0);
		 */

		Recipe savedRecipe = recipeService.createRecipe(recipe);

		// RecpIngredient 저장
		List<RecpIngredient> recpIngredientList = recipeForm.getRecpIngredientList();

		for (RecpIngredient ingredient : recpIngredientList) {
			ingredient.setRecipe(savedRecipe);
			recipeService.saveIngredient(ingredient);
		}

		// CookOrderCard 저장
		List<CookOrderCardForm> orderCardFormList = recipeForm.getCookOrderCardList();
		for (CookOrderCardForm cardForm : orderCardFormList) {
			MultipartFile multiImg = cardForm.getCookOrderImg();
			String fileName = UUID.randomUUID().toString() + "_" + multiImg.getOriginalFilename();

			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			Files.write(filePath, multiImg.getBytes());

			CookOrderCard card = new CookOrderCard();

			card.setRecipe(savedRecipe);
			card.setCookOrderImg("/imgs/life/recipe/" + fileName);
			card.setCookOrderNum(cardForm.getCookOrderNum());
			card.setCookOrderText(cardForm.getCookOrderText());

			recipeService.saveCookOrderCard(card);

		}

		// RecpMainImg 저장
		List<MultipartFile> mainImgMultiList = recipeForm.getRecpMainImgList();
		int cnt = 0;
		for (MultipartFile multiImg : mainImgMultiList) {

			if (!multiImg.isEmpty()) {
				String fileName = UUID.randomUUID().toString() + "_" + multiImg.getOriginalFilename();

				Path filePath = Paths.get(UPLOAD_DIR, fileName);
				Files.write(filePath, multiImg.getBytes());

				RecpMainImg recpMainImg = new RecpMainImg();

				recpMainImg.setRecipe(savedRecipe);
				if (cnt == 0)
					recpMainImg.setMain(true);
				else
					recpMainImg.setMain(false);
				recpMainImg.setImgSrc("/imgs/life/recipe/" + fileName);
				recpMainImg.setMainImgIndex(cnt);

				recipeService.saveMainImg(recpMainImg);

				cnt++;
			}

		}

		return "redirect:/life/recipe/list";
	}

	@GetMapping("/recipe/detail")
	public String getRecipeDetail(@RequestParam("id") Long id, Model model, HttpSession session) {
	    Recipe recipe = recipeService.getRecipeById(id); // ID로 레시피를 가져오는 서비스 메서드
	    if (recipe == null) {
	        return "error/recipe_not_found"; // 레시피가 없을 경우 처리
	    }

	    // 조회수 증가
	    recipe.setViewCount(recipe.getViewCount() == null ? 1 : recipe.getViewCount() + 1);
	    recipeService.updateRecipe(recipe); // 변경된 레시피 저장

	    // 세션에서 사용자 가져오기
	    String userId = (String) session.getAttribute("UserId");
	    Member member = memberService.getMember(userId); // 사용자 객체 가져오기

	    // 사용자의 좋아요 목록에 현재 레시피가 있는지 확인
	    boolean isFavorite = favoriteRecipeService.isFavoriteRecipe(member, recipe.getId());
	    model.addAttribute("isFavorite", isFavorite); // 좋아요 여부 추가

	    // 조리 카드 리스트 정렬
	    List<CookOrderCard> orderCards = recipe.getCookOrderCardList();
	    orderCards.sort(Comparator.comparingInt(CookOrderCard::getCookOrderNum)); // 순서 번호로 정렬
	    
	    List<Product> productList = shopService.findAll(); // 모든 상품 리스트 가져오기

	    // 리뷰 수를 가져오는 부분
	    List<ProdCnt> prodCnts = shopService.getProdCntList(productList);
	    Map<Long, Integer> revCntMap = prodCnts.stream()
	                                           .collect(Collectors.toMap(ProdCnt::getProdId, ProdCnt::getRevCnt));

	    // 모델에 추가
	    model.addAttribute("recipe", recipe); // 모델에 레시피 추가
	    model.addAttribute("productList", productList);
	    model.addAttribute("revCntMap", revCntMap); // 리뷰 수 추가

	    return "life/recipe_detail"; // 상세 정보를 보여줄 뷰 이름
	}

	@PostMapping("/recipe/edit/{id}")
	public String updateRecipe(@PathVariable("id") Long id, @Valid @ModelAttribute RecipeForm recipeForm,
	        BindingResult bindingResult, Model model, HttpSession session) {

	    // 유효성 검사 및 에러 처리
	    if (bindingResult.hasErrors()) {
	        return "/life/recp_edit";
	    }

	    // 로그인한 사용자 ID 가져오기
	    String userId = (String) session.getAttribute("UserId");

	    // 기존 레시피 가져오기
	    Recipe recipe = recipeService.getRecipeById(id);
	    if (recipe == null) {
	        return "error/recipe_not_found";
	    }

	    // 수정된 데이터 설정
	    recipe.setRecpName(recipeForm.getRecpName());
	    recipe.setRecpIntroduce(recipeForm.getRecpIntroduce());
	    recipe.setServings(recipeForm.getServings());
	    recipe.setCookTime(recipeForm.getCookTime());
	    recipe.setRecpTags(recipeForm.getRecpTags());
	    recipe.setDifficulty(recipeForm.getDifficulty());
	    recipe.setWriter(userId);

	    // 레시피 저장
	    recipeService.updateRecipe(recipe);
	    
	    // 재료 리스트 업데이트
	    List<RecpIngredient> newIngredients = recipeForm.getRecpIngredientList();
	    recipeService.updateIngredients(recipe, newIngredients); // 서비스 메서드를 호출하여 재료 업데이트

	    // 메인 이미지 처리
	    List<MultipartFile> mainImgMultiList = recipeForm.getRecpMainImgList();
	    List<RecpMainImg> existingImages = new ArrayList<>(); // 리스트 초기화   

	    // 기존 이미지 삭제
	    recipeService.deleteExistingImagesByRecipeId(recipe.getId()); // DB에서 기존 이미지 삭제

	    // 새로운 이미지 처리
	    if (mainImgMultiList != null && !mainImgMultiList.isEmpty()) {
	        for (MultipartFile file : mainImgMultiList) {
	            if (!file.isEmpty()) {
	                String imageUrl = recipeService.saveImage(file, id); // 이미지 저장
	                if (imageUrl != null) {
	                    RecpMainImg newImage = new RecpMainImg();
	                    newImage.setImgSrc(imageUrl);
	                    existingImages.add(newImage);
	                }
	            }
	        }
	    }

	    // 기존 이미지 URL을 사용하여 처리
	    if (existingImages.isEmpty()) {
	        List<String> existingImgUrls = recipeForm.getExistingImgIds(); // imgSrc를 직접 가져옴
	        for (String imgUrl : existingImgUrls) {
	            if (imgUrl != null && !imgUrl.isEmpty()) {
	                RecpMainImg existingImage = new RecpMainImg();
	                existingImage.setImgSrc(imgUrl);
	                existingImages.add(existingImage);
	            }
	        }
	    }

	    // 모든 메인 이미지를 저장하는 로직
	    recipeService.saveExistingImages(recipe, existingImages);

	    // 요리 카드 저장 로직 (이 부분은 기존 코드와 동일하게 유지)

	    return "redirect:/life/recipe/detail?id=" + recipe.getId();
	}


	@GetMapping("/recipe/edit/{id}")
	public String editRecipe(@PathVariable("id") Long id, Model model) {
		Recipe recipe = recipeService.getRecipeById(id);
		if (recipe == null) {
			return "error/recipe_not_found"; // 레시피가 없을 경우 처리
		}

		RecipeForm recipeForm = new RecipeForm();
		recipeForm.setRecpName(recipe.getRecpName());
		recipeForm.setRecpIntroduce(recipe.getRecpIntroduce());
		recipeForm.setServings(recipe.getServings());
		recipeForm.setCookTime(recipe.getCookTime());
		recipeForm.setRecpTags(recipe.getRecpTags());
		recipeForm.setDifficulty(recipe.getDifficulty());

		// 재료 리스트 매핑
		List<RecpIngredient> ingredients = recipe.getRecpIngredientList();
		recipeForm.setRecpIngredientList(ingredients);

		// 조리 순서 카드 리스트 매핑 및 정렬
		List<CookOrderCard> orderCards = recipe.getCookOrderCardList();
		// 정렬: cookOrderNum을 기준으로 정렬
		orderCards.sort(Comparator.comparingInt(CookOrderCard::getCookOrderNum));
		List<CookOrderCardForm> orderCardForms = new ArrayList<>();
		for (CookOrderCard orderCard : orderCards) {
			CookOrderCardForm orderCardForm = new CookOrderCardForm();
			orderCardForm.setCookOrderNum(orderCard.getCookOrderNum());
			orderCardForm.setCookOrderText(orderCard.getCookOrderText());
			orderCardForms.add(orderCardForm);
		}
		recipeForm.setCookOrderCardList(orderCardForms);

		// 메인 이미지 리스트 매핑
		List<RecpMainImg> mainImages = recipe.getRecpmainImgList();
		List<String> existingMainImgUrls = new ArrayList<>();
		for (RecpMainImg mainImg : mainImages) {
			existingMainImgUrls.add(mainImg.getImgSrc()); // 기존 이미지 URL 추가
		}
		recipeForm.setExistingMainImgUrls(existingMainImgUrls); // 기존 이미지 URL 리스트 설정

		model.addAttribute("recipeForm", recipeForm);
		model.addAttribute("recipe", recipe);

		return "life/recp_update"; // 수정 폼을 보여줄 뷰 이름
	}

	@PostMapping("/recipe/delete/{id}")
	public String deleteRecipe(@PathVariable("id") Long id) {
		recipeService.deleteRecipe(id); // 레시피 삭제 서비스 호출
		return "redirect:/life/recipe/list"; // 삭제 후 목록 페이지로 리다이렉트
	}
}
