package com.wellit.project.order;

import com.wellit.project.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

import java.util.List;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;

    @GetMapping("/list")
    public String getCartPage(Model model){


        //로그인 상태가 아닌 경우
        if(memberService.getMemberId()==null){
            return "redirect:/login?request";
        }

        List<CartItem> cartItemList = cartService.getCartItemList(memberService.getMemberId());


        String memberAddress = cartService.getMemberAddress(memberService.getMemberId());

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


        // 모델에 데이터 추가
        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("memAddr", memberAddress);
        model.addAttribute("orderForm", orderForm);

        return "/order/order_cart";
    }














}
