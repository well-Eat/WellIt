package com.wellit.project.order;

import com.wellit.project.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;


    @PostMapping("/create")
    public String  createOrder(@ModelAttribute OrderForm orderForm, Principal principal){

        PurchaseOrder savedPo = orderService.addOrder(orderForm, principal.getName());

        return "redirect:/order/po/"+savedPo.getOrderId();
    }

    @GetMapping("/po/{orderId}")
    public String getPoForm(Model model, @PathVariable String orderId, Principal principal){

        //todo : po의 member와 로그인한 member정보가 일치해아만 접근 가능하도록
        //todo : orderstatus != payment_wait -> 주문 정보 페이지로 이동

        PurchaseOrder po = orderService.getOnePO(orderId);

        PoForm poForm = orderService.getPoForm(orderId);

        List<OrderItem> orderItemList = orderService.getOrderItemList(orderId);


        model.addAttribute("orderItemList", orderItemList);
        model.addAttribute("poForm", poForm);
        model.addAttribute("member", po.getMember());


        return "/order/order_po";
    }

/*    @PostMapping("/po/{orderId}")
    public String */
}
