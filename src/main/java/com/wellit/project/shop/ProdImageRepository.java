package com.wellit.project.shop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdImageRepository extends JpaRepository<ProdImage, Long> {

    public List<ProdImage> findAllByProduct(Product product);
    public void deleteAllByProduct(Product product);
    public void deleteByImagePath(String imagePath);
}
