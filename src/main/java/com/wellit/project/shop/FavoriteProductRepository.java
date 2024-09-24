package com.wellit.project.shop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    public boolean existsByProduct_ProdIdAndMember_MemberId(Long prodId, String memberId);

    public void deleteFavoriteProductByProduct_ProdIdAndMember_MemberId(Long prodId, String memberId);

    public List<FavoriteProduct> findAllByMember_MemberIdOrderByCreatedAtDesc(String memberId);

    public Integer countByProduct_ProdId(Long prodId);

}
