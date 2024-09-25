package com.wellit.project.life;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FavoriteRecipeService {

    @Autowired
    private FavoriteRecipeRepository favoriteRecipeRepository;
    
    @Autowired
    private MemberService memberService;

    public void saveFavoriteRecipe(FavoriteRecipe favoriteRecipe) {
        favoriteRecipeRepository.save(favoriteRecipe); // JPA를 사용하여 저장
    }
    
    public boolean isFavoriteRecipe(Member member, Long recipeId) {
        // 해당 사용자와 레시피 ID로 좋아요 여부 확인
        return favoriteRecipeRepository.existsByMemberAndRecipeId(member, recipeId);
    }

    public void removeFavoriteRecipe(String userId, Long recipeId) {
        // 사용자 ID로 Member 객체 가져오기
        Member member = memberService.getMember(userId);
        if (member == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        // 해당 사용자와 레시피 ID에 해당하는 모든 찜 목록 삭제
        favoriteRecipeRepository.deleteByMemberAndRecipeId(member, recipeId);
    }
    
    public boolean isRecipeFavorited(Member member, Long recipeId) {
        return favoriteRecipeRepository.existsByMemberAndRecipeId(member, recipeId);
    }
}
