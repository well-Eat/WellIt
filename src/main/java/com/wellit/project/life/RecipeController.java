package com.wellit.project.life;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/life")
@Log4j2
@RequiredArgsConstructor
public class RecipeController {

	private final RecipeService recipeService;

	private static final String UPLOAD_DIR = "src/main/resources/static/imgs/life/recipe";

	@GetMapping("/wellit")
	public String getWellit() {
		return "life/wellit";
	}

	@GetMapping("/recipe/list")
	public String getRecipeList(Model model) {
		List<Recipe> recipeList = recipeService.getAllRecipes(); // 모든 레시피를 가져오는 서비스 메서드
		model.addAttribute("recipes", recipeList); // 모델에 레시피 리스트 추가
		return "life/recp_list"; // 레시피 목록을 보여줄 뷰 이름
	}

	/* 레시피 등록 폼 OPEN */
	@GetMapping("/recipe/create")
	public String createRecipe(Model model) {
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
	public String getRecipeDetail(@RequestParam("id") Long id, Model model) {
		Recipe recipe = recipeService.getRecipeById(id); // ID로 레시피를 가져오는 서비스 메서드
		if (recipe == null) {
			// 레시피가 없을 경우 처리 (예: 에러 페이지로 리다이렉트)
			return "error/recipe_not_found"; // 에러 페이지로 리턴
		}
		model.addAttribute("recipe", recipe); // 모델에 레시피 추가
		return "life/recipe_detail"; // 상세 정보를 보여줄 뷰 이름
	}

	@PostMapping("/recipe/edit/{id}")
	public String updateRecipe(@PathVariable("id") Long id,
	                           @Valid @ModelAttribute RecipeForm recipeForm,
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

	 // 기존 재료 가져오기
	    List<RecpIngredient> existingIngredients = recipeService.getIngredientsByRecipeId(recipe.getId());
	    List<RecpIngredient> recpIngredientList = recipeForm.getRecpIngredientList();
	    if (recpIngredientList != null && !recpIngredientList.isEmpty()) {
	        // 수정된 재료가 있을 경우 업데이트
	        recipeService.updateIngredients(recipe, recpIngredientList);
	    } else {
	        // 수정된 재료가 없을 경우 기존 재료를 사용
	        recipeService.updateIngredients(recipe, existingIngredients);
	    }
	    
	 // CookOrderCard 저장
	    List<CookOrderCardForm> orderCardFormList = recipeForm.getCookOrderCardList();
	    List<String> existingCookOrderImgUrls = recipeForm.getExistingCookOrderImgUrls(); // 기존 요리 이미지 URL 가져오기

	    // 기존 음식 이미지 삭제 로직
	    recipeService.deleteExistingCookOrderImgUrlsByRecipeId(recipe.getId()); // DB에서 기존 요리 이미지 삭제
	    
	    // 기존 이미지 URL 가져오기
	    List<String> existingImgUrls = recipeForm.getExistingImgIds(); // imgSrc를 직접 가져옴
	    
	    // 기존 메인 이미지 삭제 로직
	    recipeService.deleteExistingImagesByRecipeId(recipe.getId()); // DB에서 기존 이미지 삭제

	    // 메인 이미지 처리
	    List<MultipartFile> mainImgMultiList = recipeForm.getRecpMainImgList();
	    List<RecpMainImg> existingImages = new ArrayList<>(); // 리스트 초기화

	    // 새로 업로드된 메인 이미지가 있을 경우 처리
	    if (mainImgMultiList != null && !mainImgMultiList.isEmpty()) {
	        for (MultipartFile file : mainImgMultiList) {
	            String imageUrl = recipeService.saveImage(file, recipe); 
	            RecpMainImg newImage = new RecpMainImg();
	            newImage.setImgSrc(imageUrl); 
	            existingImages.add(newImage); 
	        }
	    } else {
	        // 기존 메인 이미지 URL을 사용하여 처리
	        for (String imgUrl : existingImgUrls) {
	            if (imgUrl != null && !imgUrl.isEmpty()) {
	                RecpMainImg existingImage = new RecpMainImg();
	                existingImage.setImgSrc(imgUrl); // URL 설정
	                existingImages.add(existingImage); // 기존 이미지 리스트에 추가
	            } else {
	                System.out.println("No imgSrc found for imgUrl: " + imgUrl); // 로그 추가
	            }
	        }
	    }

	    // 모든 메인 이미지를 저장하는 로직
	    recipeService.saveExistingImages(recipe, existingImages); 
	    
	 // 요리 이미지 처리
	    List<CookOrderCardForm> cookOrderCardForms = recipeForm.getCookOrderCardList();
	    List<CookOrderCard> cookExistingImages = new ArrayList<>(); // 리스트 초기화

	    // 요리 카드 처리
	    if (cookOrderCardForms != null && !cookOrderCardForms.isEmpty()) {
	        for (CookOrderCardForm orderCardForm : cookOrderCardForms) {
	            CookOrderCard cookOrderCard = new CookOrderCard(); // 새로운 요리 카드 객체 생성
	            
	            // 새로 업로드된 이미지가 있을 경우 처리
	            if (orderCardForm.getCookOrderImg() != null && !orderCardForm.getCookOrderImg().isEmpty()) {
	                String imageUrl = recipeService.saveImage(orderCardForm.getCookOrderImg(), recipe);
	                cookOrderCard.setCookOrderImg(imageUrl); // 새 이미지 URL 설정
	            } else {
	                // 기존 이미지 URL을 사용하여 처리
	                String existingImgUrl = orderCardForm.getExistingCookOrderImg(); // 기존 이미지 URL 가져오기
	                if (existingImgUrl != null && !existingImgUrl.isEmpty()) {
	                    cookOrderCard.setCookOrderImg(existingImgUrl); // 기존 이미지 URL 설정
	                } else {
	                    System.out.println("No existing imgSrc found for order card."); // 로그 추가
	                }
	            }
	            
	            // 요리 카드 세부 정보 설정
	            cookOrderCard.setCookOrderNum(orderCardForm.getCookOrderNum());
	            cookOrderCard.setCookOrderText(orderCardForm.getCookOrderText());
	            cookOrderCard.setRecipe(recipe); // 레시피 연결
	            
	            cookExistingImages.add(cookOrderCard); // 요리 카드 리스트에 추가
	        }
	    }

	    // 요리 카드 저장 로직
	    recipeService.saveCookOrderCards(cookExistingImages);
	    
	
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

		// 조리 순서 카드 리스트 매핑
		List<CookOrderCard> orderCards = recipe.getCookOrderCardList();
		List<CookOrderCardForm> orderCardForms = new ArrayList<>();
		for (CookOrderCard orderCard : orderCards) {
			CookOrderCardForm orderCardForm = new CookOrderCardForm();
			orderCardForm.setCookOrderNum(orderCard.getCookOrderNum());
			orderCardForm.setCookOrderText(orderCard.getCookOrderText());
			// 이미지 URL을 표시하기 위해서 기존 이미지 URL을 사용
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
