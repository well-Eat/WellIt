package com.wellit.project.shop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdInfoRepository extends JpaRepository<ProdInfo, Long> {
    public void deleteAllByProduct(Product product);
    public List<ProdInfo> findAllByProduct(Product product);
}
