package com.wellit.project.life;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecpMainImgRepository extends JpaRepository<RecpMainImg, Long> {
    List<RecpMainImg> findAllByRecipeOrderByMainImgIndexAsc(Recipe recipe);
}
