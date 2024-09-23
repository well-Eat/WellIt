package com.wellit.project.shop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdInfoRepository extends JpaRepository<ProdInfo, Long> {
    public void deleteAllByProduct(Product product);
}
