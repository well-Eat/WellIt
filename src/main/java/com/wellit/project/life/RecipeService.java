package com.wellit.project.life;

import com.wellit.project.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecpIngredientRepository recpIngredientRepository;
    private final RecpMainImgRepository recpMainImgRepository;
    private final CookOrderCardRepository cookOrderCardRepository;


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













}
