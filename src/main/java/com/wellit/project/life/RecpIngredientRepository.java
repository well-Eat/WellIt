package com.wellit.project.life;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecpIngredientRepository extends JpaRepository<RecpIngredient, Long> {
    List<RecpIngredient> findAllByRecipe(Recipe recipe);

	List<RecpIngredient> findByRecipeId(Long id);
}
