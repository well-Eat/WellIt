package com.wellit.project.order;

import com.wellit.project.member.MemberRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
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
    public String  createOrder( @ModelAttribute OrderForm orderForm, Principal principal, Model model){


            PurchaseOrder savedPo = orderService.addOrder(orderForm, principal.getName());
            String oi = savedPo.getOrderId();
//            return "redirect:/";

        return "redirect:/order/po/"+oi;
    }

    @GetMapping("/po/{orderId}")
    public String getPoForm(Model model, @PathVariable("orderId") String orderId, Principal principal){

        //todo : po의 member와 로그인한 member정보가 일치해아만 접근 가능하도록
        //todo : orderstatus != payment_wait -> 주문 정보 페이지로 이동

        PurchaseOrder po = orderService.getOnePO(orderId);
        log.info(po.getOrderId());

        PoForm poForm = orderService.getPoForm(orderId);

        List<OrderItem> orderItemList = orderService.getOrderItemList(orderId);


        model.addAttribute("orderItemList", orderItemList);
        model.addAttribute("poForm", poForm);
        model.addAttribute("member", po.getMember());


        return "/order/order_po";
    }


    // 결제 성공 후 프로세스
    @PostMapping("/po/{orderId}")
    public String afterPaymentSuccess(@PathVariable("orderId")  String orderId, @ModelAttribute PoForm poForm, Principal principal){

        //po 내용 업데이트
        boolean success = orderService.updatePurchaseOrderInfo(orderId, poForm, principal);

        if(success) {  //성공 시 주문 화면 출력
            return "redirect:/order/po/detail/"+orderId+"?success";
        } else {  // 실패 시
            // todo : 결제 실패 화면
            return "redirect:/";
        }
    }

    // mypage : 주문 내용 상세 (주문 결과 상세 확인)
    @GetMapping("/po/detail/{orderId}")
    public String getOrderDetailPage(Model model, @PathVariable("orderId") String orderId, Principal principal, @RequestParam(value = "success", required = false) String success){

        if (success != null) {
            model.addAttribute("orderSuccess", true);
        } else {
            model.addAttribute("orderSuccess", false);
        }

        PurchaseOrder po = orderService.getOnePO(orderId);
        log.info(po.getOrderId());
        PoDetailForm poDetail =orderService.getOnePoDetail(orderId);

        List<OrderItem> orderItemList = orderService.getOrderItemList(orderId);


        model.addAttribute("orderItemList", orderItemList);
        model.addAttribute("poDetail", poDetail);
        model.addAttribute("member", po.getMember());

        return "/order/order_poDetail";
    }



    /********** admin : 주문처리 로직 ****************/
    /********** admin : 주문처리 로직 ****************/
    /********** admin : 주문처리 로직 ****************/
    // admin : 주문 리스트 확인 페이지
    @GetMapping("/admin/po/list")
    public String getRecievePoList(){
        return "/order/admin_po_list";
    }

    // admin : 개별 주문 처리
    @GetMapping("/admin/po/{orderId}")
    public String viewPoDetail(@PathVariable(name = "orderId") String orderId){
        return "/order/admin_poDetail";
    }




}
