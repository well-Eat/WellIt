package com.wellit.project.order;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberRepository;
import com.wellit.project.member.MemberService;
import com.wellit.project.shop.Product;
import com.wellit.project.shop.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;


    public void addToCart(CartItemRequest cartItemRequest, Member member) {

        // 회원의 장바구니 조회 (없으면 새로 생성)
        //Cart cart = cartRepository.findByMember(member).orElseGet(() -> new Cart(member));

        Cart cart = member.getCart();
        if(cart == null){
            cart = new Cart(member);
            member.setCart(cart);
        }

        // 상품 조회
        Product product = productRepository.findById(cartItemRequest.getProdId())
                                           .orElseThrow(() -> new RuntimeException("해당 상품이 없습니다."));

        // 장바구니에 해당 상품이 이미 있는지 확인
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                                                  .filter(item -> item.getProduct().getProdId().equals(product.getProdId()))
                                                  .findFirst();

        // 상품이 이미 있다면 수량을 업데이트
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequest.getQuantity());
        } else {
            // 새로 추가
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
        }

        // 장바구니 저장
        cartRepository.save(cart);
    }


    /* 카트페이지로 카트 품목리스트 반환 */
    public List<CartItem> getCartItemList(String memberId){

        Cart cart = cartRepository.findByMember_MemberId(memberId).orElseThrow();

        return cartItemRepository.findCartItemsByCart(cart);
    }



}





































