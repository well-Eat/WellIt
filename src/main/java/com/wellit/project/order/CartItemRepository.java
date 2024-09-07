package com.wellit.project.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    public List<CartItem> findCartItemsByCart(Cart cart);
}
