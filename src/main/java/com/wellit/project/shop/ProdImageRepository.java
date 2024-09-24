package com.wellit.project.shop;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ProdImageRepository extends JpaRepository<ProdImage, Long> {

    public void deleteByImagePath(String imagePath);
    ProdImage findByImagePath(String imagePath);
}
