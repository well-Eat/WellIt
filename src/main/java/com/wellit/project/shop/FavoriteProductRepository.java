package com.wellit.project.shop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    public boolean existsByProduct_ProdIdAndMember_MemberId(Long prodId, String memberId);

    public void deleteFavoriteProductByProduct_ProdIdAndMember_MemberId(Long prodId, String memberId);

}
