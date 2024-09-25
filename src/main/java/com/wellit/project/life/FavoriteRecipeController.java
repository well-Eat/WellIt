package com.wellit.project.life;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService; // 사용자 서비스

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FavoriteRecipeController {

    @Autowired
    private FavoriteRecipeService favoriteRecipeService; // 찜하기 서비스
    @Autowired
    private MemberService memberService; // 사용자 서비스
    @Autowired
    private RecipeService recipeService; // 레시피 서비스

    @PostMapping("/favorite/add")
    public String addFavoriteRecipe(@RequestParam("userId") String userId, @RequestParam("recipeId") Long recipeId) {
        Member member = memberService.getMember(userId); // 사용자 가져오기
        Recipe recipe = recipeService.getRecipeById(recipeId); // 레시피 가져오기

        if (member == null || recipe == null) {
            return "fail"; // 사용자 또는 레시피가 없으면 실패
        }

        // 이미 찜한 레시피인지 확인
        if (favoriteRecipeService.isRecipeFavorited(member, recipeId)) {
            return "already_favorited"; // 이미 찜한 경우
        }

        FavoriteRecipe favoriteRecipe = new FavoriteRecipe();
        favoriteRecipe.setMember(member); // 사용자 설정
        favoriteRecipe.setRecipe(recipe); // 레시피 설정

        favoriteRecipeService.saveFavoriteRecipe(favoriteRecipe); // 찜하기 저장

        return "success"; // 성공
    }
    
    @PostMapping("/favorite/remove")
    public String removeFavoriteRecipe(@RequestParam("userId") String userId, @RequestParam("recipeId") Long recipeId) {
        // 사용자와 레시피로 찜 목록에서 제거하는 로직 구현
        favoriteRecipeService.removeFavoriteRecipe(userId, recipeId);
        return "success"; // 성공 응답
    }
    
    @GetMapping("/favorite/check")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@RequestParam("userId") String userId, @RequestParam("recipeId") Long recipeId) {
        Member member = memberService.getMember(userId);
        boolean isFavorite = favoriteRecipeService.isFavoriteRecipe(member, recipeId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        
        return ResponseEntity.ok(response);
    }
}
