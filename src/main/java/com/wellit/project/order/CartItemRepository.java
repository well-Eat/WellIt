package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    public List<CartItem> findCartItemsByCart(Cart cart);

    public int countCartItemsByCart(Cart cart);

    public int deleteCartItemByProduct_ProdIdAndCart(Long prodId, Cart cart);
}
