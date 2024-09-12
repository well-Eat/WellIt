package com.wellit.project.order;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String getCartPage(Model model, Principal principal){

        List<CartItem> cartItemList = cartService.getCartItemList(principal.getName());


        String memberAddress = cartService.getMemberAddress(principal.getName());

        OrderForm orderForm = new OrderForm();

        // 각 CartItem에 해당하는 OrderItemQuantity 생성 및 추가
        List<OrderItemQuantity> orderItemQuantityList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            OrderItemQuantity orderItemQuantity = new OrderItemQuantity();
            orderItemQuantity.setProdId(cartItem.getProduct().getProdId());
            orderItemQuantity.setQuantity(cartItem.getQuantity());
            orderItemQuantity.setBooleanOrder(true);
            orderForm.getOrderItemQuantityList().add(orderItemQuantity);
        }

        // OrderForm에 사용자 주소 설정
        orderForm.setAddr1(memberAddress);

        // 모델에 데이터 추가
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("memAddr", memberAddress);
        model.addAttribute("orderForm", orderForm);

        return "/order/order_cart";
    }











    /* 인증된 사용자의 Member 인스턴스 반환 */
    private Member getMember(Principal principal){
        return memberService.getMember(principal.getName());
    }


}