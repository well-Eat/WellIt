package com.wellit.project.life;

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


    /* 레시피 등록 폼 OPEN */
    @GetMapping("/recipe/create")
    public String createRecipe(Model model){

        model.addAttribute("recipeForm", new RecipeForm());
        return "/life/recp_create";
    }

    /* 새 레시피 등록 */
    @PostMapping("/recipe/create")
    public String postRecipe(@Valid @ModelAttribute RecipeForm recipeForm,
                             BindingResult bindingResult, Model model) throws IOException {

        if (bindingResult.hasErrors()) {

            // 필드 에러를 출력
            bindingResult.getFieldErrors().forEach(error -> {
                log.info("Field Error: Field = {}, Rejected Value = {}, Message = {}",
                         error.getField(), error.getRejectedValue(), error.getDefaultMessage());
            });

            // 글로벌 에러를 출력 (객체 수준의 에러)
            bindingResult.getGlobalErrors().forEach(error -> {
                log.info("Global Error: Object = {}, Message = {}",
                         error.getObjectName(), error.getDefaultMessage());
            });


            return "/life/recp_create";  // 유효성 검사 실패 시 다시 폼으로 이동
        }


        //Recipe 저장
        Recipe recipe = new Recipe();

        recipe.setWriter("testWriter");
        recipe.setCookTime(recipeForm.getCookTime());
        recipe.setServings(recipeForm.getServings());
        recipe.setRecpName(recipeForm.getRecpName());
        recipe.setRecpIntroduce(recipeForm.getRecpIntroduce());
        recipe.setRecpTags(recipeForm.getRecpTags());
        recipe.setDifficulty(recipeForm.getDifficulty());
/*        recipe.setBookmark(0);
        recipe.setHeart(0);
        recipe.setView(0);*/

        Recipe savedRecipe = recipeService.createRecipe(recipe);

        //RecpIngredient 저장
        List<RecpIngredient> recpIngredientList =recipeForm.getRecpIngredientList();

        for(RecpIngredient ingredient: recpIngredientList){
            ingredient.setRecipe(savedRecipe);
            recipeService.saveIngredient(ingredient);
        }

        //CookOrderCard  저장
        List<CookOrderCardForm> orderCardFormList = recipeForm.getCookOrderCardList();
        for(CookOrderCardForm cardForm: orderCardFormList){
            MultipartFile multiImg = cardForm.getCookOrderImg();
            String fileName = UUID.randomUUID().toString()+"_"+ multiImg.getOriginalFilename();

            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, multiImg.getBytes());

            CookOrderCard card = new CookOrderCard();

            card.setRecipe(savedRecipe);
            card.setCookOrderImg("/imgs/life/recipe/"+fileName);
            card.setCookOrderNum(cardForm.getCookOrderNum());
            card.setCookOrderText(cardForm.getCookOrderText());

            recipeService.saveCookOrderCard(card);

        }


        //RecpMainImg 저장
        List<MultipartFile> mainImgMultiList = recipeForm.getRecpMainImgList();
        int cnt = 0;
        for(MultipartFile multiImg: mainImgMultiList){

            if(!multiImg.isEmpty()){
                String fileName = UUID.randomUUID().toString()+"_"+ multiImg.getOriginalFilename();

                Path filePath = Paths.get(UPLOAD_DIR, fileName);
                Files.write(filePath, multiImg.getBytes());

                RecpMainImg recpMainImg = new RecpMainImg();

                recpMainImg.setRecipe(savedRecipe);
                if(cnt == 0) recpMainImg.setMain(true);
                else recpMainImg.setMain(false);
                recpMainImg.setImgSrc("/imgs/life/recipe/"+fileName);
                recpMainImg.setMainImgIndex(cnt);

                recipeService.saveMainImg(recpMainImg);

                cnt++;
            }

        }



        return "redirect:/life/recipe/create";
    }


    @GetMapping("/{recpId}")
    public String getRecpDetail(@PathVariable(name = "recpId") Long recpId, Model model){
        Recipe recipe = recipeService.getOneRecipe(recpId);

        //태그 문자열을 리스트로 변환
        String strTags = recipe.getRecpTags();
        strTags.replaceAll("\\s","");
        List<String> tagList = Arrays.stream(strTags.split(",")).toList();


        //재료 리스트
        List<RecpIngredient> recpIngredientList = recipeService.getIngredientList(recipe);

        List<RecpMainImg> recpMainImgList = recipeService.getMainImgList(recipe);


        model.addAttribute("recipe", recipe);
        model.addAttribute("tagList", tagList);
        model.addAttribute("recpIngredientList", recpIngredientList);
        model.addAttribute("recpMainImgList", recpMainImgList);

        return "/life/recp_detail";
    }











}

