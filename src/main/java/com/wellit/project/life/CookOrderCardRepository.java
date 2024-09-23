package com.wellit.project.life;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CookOrderCardRepository extends JpaRepository<CookOrderCard, Long> {
    List<CookOrderCard> findAllByRecipeOrderByCookOrderNumAsc(Recipe recipe);

	void deleteByRecipeId(Long recipeId);
}
