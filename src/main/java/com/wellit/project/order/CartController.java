package com.wellit.project.order;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

        model.addAttribute("cartItemList", cartItemList);

        return "/order/order_cart";
    }











    /* 인증된 사용자의 Member 인스턴스 반환 */
    private Member getMember(Principal principal){
        return memberService.getMember(principal.getName());
    }


}
