package com.wellit.project.order;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cart/data")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;
    private final MemberService memberService;



    /* 카트에 상품 추가 */
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody CartItemRequest cartItemRequest, Principal principal){

        cartService.addToCart(cartItemRequest, getMember(principal));

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cart updated successfully");

        return ResponseEntity.ok(response);
    }















    /* Member 인스턴스 반환 */
    private Member getMember(Principal principal){
        return memberService.getMember(principal.getName());
    }


}
