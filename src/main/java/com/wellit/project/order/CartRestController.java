package com.wellit.project.order;

import com.wellit.project.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/cart/data")
@RequiredArgsConstructor
@Log4j2
public class CartRestController {

    private final CartService cartService;
    private final MemberService memberService;



    /* 카트에 상품 추가 */
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(@RequestBody CartItemRequest cartItemRequest){
        //로그인 상태가 아닌 경우
        if(memberService.getMemberId() == null){
            Map<String, String> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "로그인 후 이용가능합니다.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String memberId = memberService.getMemberId();

        cartService.addToCart(cartItemRequest, memberService.getMember(memberId));

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cart updated successfully");

        return ResponseEntity.ok(response);
    }

    /*카트 아이템 삭제*/
    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<Map<String , String >> removeAtCart(@RequestBody String strProdId){

        String memberId = memberService.getMemberId();

        if(memberId == null){
            Map<String, String> response = new HashMap<>();
            response.put("status", "fail");
            response.put("message", "로그인 후 이용가능합니다.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }


        Long prodId = Long.parseLong(strProdId.replace("{\"prodId\":","").replace("}",""));
        cartService.removeCartItem(prodId, memberService.getMember(memberId).getCart());

        Map<String, String > response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Cart updated successfully");

        return ResponseEntity.ok(response);
    }




    @GetMapping("/item-count")
    public ResponseEntity<Map<String, Object>> getCartItemCount() {
        Map<String, Object> response = new HashMap<>();
        String memberId = memberService.getMemberId();

        if (memberId != null) {
            int cartItemCount = cartService.getCartItemCountByUser(memberId);
            response.put("cartItemCount", cartItemCount);
        } else {
            response.put("cartItemCount", 0);  // 비로그인 상태일 때 0 반환
        }
        return ResponseEntity.ok(response);
    }


















}
