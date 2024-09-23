package com.wellit.project.life;

import com.wellit.project.DataNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecpIngredientRepository recpIngredientRepository;
    private final RecpMainImgRepository recpMainImgRepository;
    private final CookOrderCardRepository cookOrderCardRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/imgs/life/recipe";

    //레시피 저장
    public Recipe createRecipe(Recipe recipe){
        return recipeRepository.save(recipe);
    }

    //레시피 : 재료 저장
    public void saveIngredient(RecpIngredient ingredient){
        recpIngredientRepository.save(ingredient);
    }

    //레시피 : 요리순서카드 저장
    public void saveCookOrderCard(CookOrderCard cookOrderCard){
        cookOrderCardRepository.save(cookOrderCard);
    }

    //레시피 : 메인 이미지 저장
    public void saveMainImg(RecpMainImg recpMainImg){
        recpMainImgRepository.save(recpMainImg);
    }

    //조회 : 레시피 조회
    public Recipe getOneRecipe(Long recpId){
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recpId);
        Recipe recipe = null;
        try{
            recipe = optionalRecipe.get();
        } catch (DataNotFoundException e){
            log.error(e.getMessage());
        }
        return recipe;
    }
    //조회 : 요리재료
    public List<RecpIngredient> getIngredientList(Recipe recipe){
        return recpIngredientRepository.findAllByRecipe(recipe);
    }
    //조회 : 메인이미지
    public List<RecpMainImg> getMainImgList(Recipe recipe){
        return recpMainImgRepository.findAllByRecipeOrderByMainImgIndexAsc(recipe);
    }
    //조회 : 레시피 순서 카드
    public List<CookOrderCard> getCookOrderCardList(Recipe recipe){
        return cookOrderCardRepository.findAllByRecipeOrderByCookOrderNumAsc(recipe);
    }

    public List<Recipe> getAllRecipes() {
        // 모든 레시피를 데이터베이스에서 가져와서 반환
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        // ID로 레시피를 찾아서 반환
        return recipeRepository.findById(id).orElse(null);
    }

    //레시피 정보를 업데이트하는 메서드
    public void updateRecipe(Recipe recipe) {
        // 레시피 정보를 데이터베이스에 저장
        recipeRepository.save(recipe);
    }
    
    //재료 리스트를 업데이트하는 메서드
    public void updateIngredients(Recipe recipe, List<RecpIngredient> ingredients) {
        // 기존 재료 리스트가 null인 경우 빈 리스트로 초기화
        List<RecpIngredient> existingIngredients = recipe.getRecpIngredientList();
        if (existingIngredients == null) {
            existingIngredients = new ArrayList<>();
        }

        // 기존 재료 삭제
        for (RecpIngredient ingredient : existingIngredients) {
            recpIngredientRepository.delete(ingredient);
        }

        // 새로운 재료 추가
        for (RecpIngredient ingredient : ingredients) {
            ingredient.setRecipe(recipe); // 레시피와 연결
            recpIngredientRepository.save(ingredient);
        }
    }

    //조리 순서 카드를 업데이트하는 메서드
    public void updateCookOrderCards(Recipe recipe, List<CookOrderCardForm> orderCardForms) {
        // 기존 조리 순서 삭제 (기존 데이터가 필요할 수 있음)
        for (CookOrderCard card : recipe.getCookOrderCardList()) {
            cookOrderCardRepository.delete(card);
        }

        // 새로운 조리 순서 추가
        for (CookOrderCardForm orderCardForm : orderCardForms) {
            CookOrderCard orderCard = new CookOrderCard();
            orderCard.setRecipe(recipe);
            orderCard.setCookOrderNum(orderCardForm.getCookOrderNum());
            orderCard.setCookOrderText(orderCardForm.getCookOrderText());
            
            // 이미지 처리: 업로드된 이미지가 있다면 저장
            MultipartFile imgFile = orderCardForm.getCookOrderImg();
            if (imgFile != null && !imgFile.isEmpty()) {
                // 이미지 파일 저장 로직 (위치와 방법에 따라 다름)
                String fileName = UUID.randomUUID().toString() + "_" + imgFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                try {
					Files.write(filePath, imgFile.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                orderCard.setCookOrderImg("/imgs/life/recipe/" + fileName); // 이미지 경로 설정
            }

            cookOrderCardRepository.save(orderCard);
        }
    }

    public void updateMainImages(Recipe recipe, List<MultipartFile> mainImgFiles) {
        // 기존 메인 이미지 삭제 (원하는 경우)
        for (RecpMainImg mainImg : recipe.getRecpmainImgList()) {
            recpMainImgRepository.delete(mainImg);
        }

        // 새로운 메인 이미지 추가
        int cnt = 0;
        for (MultipartFile imgFile : mainImgFiles) {
            if (!imgFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imgFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                try {
					Files.write(filePath, imgFile.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                RecpMainImg recpMainImg = new RecpMainImg();
                recpMainImg.setRecipe(recipe);
                recpMainImg.setImgSrc("/imgs/life/recipe/" + fileName);
                recpMainImg.setMain(cnt == 0); // 첫 번째 이미지를 메인으로 설정
                recpMainImg.setMainImgIndex(cnt);

                recpMainImgRepository.save(recpMainImg);
                cnt++;
            }
        }
    }

    public String saveImage(MultipartFile file, Recipe recipe) {
        // 이미지 저장 경로 설정
        String uploadDir = UPLOAD_DIR + "/" + recipe.getId(); // 레시피 ID에 따라 디렉토리 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리가 없으면 생성
        }

        // 파일 이름 설정 (중복 방지를 위해 UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            // 파일 저장
            File serverFile = new File(dir, fileName);
            file.transferTo(serverFile); // 실제 파일 저장

            // 저장된 파일의 URL 반환 (예: 웹 서버의 접근 가능한 경로)
            return "/imgs/life/recipe/" + recipe.getId() + "/" + fileName; // URL 형식으로 반환
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
        }
    }

    public void saveExistingImages(Recipe recipe, List<RecpMainImg> existingImages) {
        for (RecpMainImg img : existingImages) {
            img.setRecipe(recipe); // 레시피와의 관계 설정
            recpMainImgRepository.save(img); // JPA를 사용할 경우
        }
    }

    @Transactional // 트랜잭션을 활성화
    public void deleteExistingImagesByRecipeId(Long recipeId) {
        // 레시피 ID에 해당하는 이미지를 삭제
        recpMainImgRepository.deleteByRecipeId(recipeId);
    }

 // 이미지 파일 이름 조회 메서드
    public String getImgSrcById(Long id) {
        String imgSrc = recpMainImgRepository.findImgSrcById(id);
        System.out.println("Retrieved imgSrc: " + imgSrc + " for id: " + id); // 로그 추가
        return imgSrc;
    }

    @Transactional // 트랜잭션을 활성화
    public void deleteExistingCookOrderImgUrlsByRecipeId(Long recipeId) {
        // 레시피 ID에 연결된 모든 요리 카드를 삭제
        cookOrderCardRepository.deleteByRecipeId(recipeId);
    }

    public void saveCookOrderCards(List<CookOrderCard> cookOrderCards) {
        for (CookOrderCard cookOrderCard : cookOrderCards) {
            cookOrderCardRepository.save(cookOrderCard); // 각 요리 카드를 저장
        }
    }

    public List<RecpIngredient> getIngredientsByRecipeId(Long id) {
        return recpIngredientRepository.findByRecipeId(id); // 레시피 ID에 해당하는 재료 목록 조회
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id); // 레시피 삭제
    }








}
