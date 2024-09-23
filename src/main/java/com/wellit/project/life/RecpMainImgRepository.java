package com.wellit.project.life;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecpMainImgRepository extends JpaRepository<RecpMainImg, Long> {
    List<RecpMainImg> findAllByRecipeOrderByMainImgIndexAsc(Recipe recipe);

    @Modifying // 데이터 변경 메서드에 추가
    @Query("DELETE FROM RecpMainImg img WHERE img.recipe.id = :recipeId")
    void deleteByRecipeId(@Param("recipeId") Long recipeId);

    @Query("SELECT img.imgSrc FROM RecpMainImg img WHERE img.id = :id")
    String findImgSrcById(@Param("id") Long id); // imgSrc만 반환

}
