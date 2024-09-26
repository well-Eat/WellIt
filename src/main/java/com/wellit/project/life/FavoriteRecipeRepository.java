package com.wellit.project.life;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wellit.project.member.Member;

public interface FavoriteRecipeRepository extends JpaRepository<FavoriteRecipe, Long> {

    boolean existsByMemberAndRecipeId(Member member, Long recipeId);
    // 추가적인 쿼리 메서드가 필요하다면 여기에 정의

    void deleteByMemberAndRecipeId(Member member, Long recipeId);

	List<FavoriteRecipe> findByMember(Member member);
}
