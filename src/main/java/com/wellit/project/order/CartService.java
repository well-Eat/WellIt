package com.wellit.project.order;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberRepository;
import com.wellit.project.member.MemberService;
import com.wellit.project.shop.Product;
import com.wellit.project.shop.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;


    /* 장바구니 상품 추가 */
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


    /*장바구니 아이템 삭제*/
    public void removeCartItem(Long prodId, Cart cart){
        cartItemRepository.deleteCartItemByProduct_ProdIdAndCart(prodId, cart);
    }




    /*카트 아이템 수 반환*/
    public int getCartItemCountByUser(String memberId) {
        // 사용자에 대한 장바구니를 조회
        Optional<Cart> optionalCart = cartRepository.findByMember_MemberId(memberId);

        // 카트가 없으면 0을 반환
        if (optionalCart.isEmpty()) {
            return 0;
        }

        // 카트가 있으면 해당 카트의 아이템 개수 반환
        Cart cart = optionalCart.get();
        return cartItemRepository.countCartItemsByCart(cart);
    }



    /* 카트페이지로 카트 품목리스트 반환 */
    public List<CartItem> getCartItemList(String memberId){

        Cart cart = cartRepository.findByMember_MemberId(memberId).orElseThrow();

        return cartItemRepository.findCartItemsByCart(cart);
    }

    public String getMemberAddress(String memberId) {
        Member member = memberRepository.findByMemberId(memberId);

        return member.getMemberAddress();
    }


}





































