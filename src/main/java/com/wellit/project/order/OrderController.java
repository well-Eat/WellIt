package com.wellit.project.order;

import com.wellit.project.member.MemberService;
import com.wellit.project.shop.Product;
import com.wellit.project.shop.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;
    private final ProductRepository productRepository;
    private final MemberService memberService;


    //카트 -> 주문서 생성
    @PostMapping("/create")
    public String  createOrder(@Valid @ModelAttribute OrderForm orderForm, BindingResult bindingResult, Model model){

        // 재고 수량 확인 (백엔드에서 재고 체크)
        for (OrderItemQuantity orderItemQuantity : orderForm.getOrderItemQuantityList()) {
            Product product = productRepository.findById(orderItemQuantity.getProdId()).orElseThrow();
            if (orderItemQuantity.getQuantity() > product.getProdStock()) {
                bindingResult.rejectValue("orderItemQuantityList[" + orderForm.getOrderItemQuantityList().indexOf(orderItemQuantity) + "].quantity",
                                          "error.orderItemQuantity", "재고 수량이 부족합니다.");
                return "redirect:/cart/list?error_stock"; // 재고 수량이 부족할 경우
            }
        }


            PurchaseOrder savedPo = orderService.addOrder(orderForm, memberService.getMemberId());
            String oi = savedPo.getOrderId();

        return "redirect:/order/po/"+oi;
    }
        //todo : orderstatus != payment_wait -> 주문 정보 페이지로 이동



    //카트 -> 주문서 생성 -> 생성된 주문서 뷰페이지
    @GetMapping("/po/{orderId}")
    public String getPoForm(Model model, @PathVariable("orderId") String orderId){

        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        String memberId = memberService.getMemberId();

        if(memberId==null){
            return "redirect:/login?request";
        }

        //해당 주문서 가져오기
        PurchaseOrder po = orderService.getOnePO(orderId);
        log.info(po.getOrderId());

        // 로그인한 사용자의 정보와 주문서의 회원 정보가 일치하는지 확인
        if (!po.getMember().getMemberId().equals(memberId)) {
            // 사용자가 일치하지 않으면 403 Forbidden 오류를 던짐
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }


        PoForm poForm = orderService.getPoForm(orderId);

        List<OrderItem> orderItemList = orderService.getOrderItemList(orderId);


        model.addAttribute("orderItemList", orderItemList);
        model.addAttribute("poForm", poForm);
        model.addAttribute("member", po.getMember());


        return "/order/order_po";
    }


    // 결제 성공 후 프로세스
    @PostMapping("/po/{orderId}")
    public String afterPaymentSuccess(@PathVariable("orderId")  String orderId, @ModelAttribute PoForm poForm){

        //po 내용 업데이트
        boolean success = orderService.updatePurchaseOrderInfo(orderId, poForm);

        if(success) {  //성공 시 주문 화면 출력
            return "redirect:/order/po/detail/"+orderId+"?success";
        } else {  // 실패 시
            // todo : 결제 실패 화면
            return "redirect:/";
        }
    }

    // 결제 취소 성공 후 DB 업데이트 및 화면 리다이렉트
    @PostMapping("/admin/cancel/{orderId}")
    public String afterCancelPaymentSuccess(@PathVariable("orderId") String orderId,
                                            @RequestParam("impUid") String impUid,
                                            @RequestParam("reason") String reason) {
        try {
            // DB에서 해당 주문 정보를 찾고, 취소 상태로 업데이트
            boolean isUpdated = orderService.updateOrderStatusToCanceled(orderId, impUid, reason);

            if (isUpdated) {
                // 취소 성공 시 주문 상세 화면으로 리다이렉트
                return "redirect:/order/admin/po/" + orderId;
            } else {
                // DB 업데이트 실패 시 처리
                return "redirect:/order/admin/po/" + orderId+ "?cancelError";
            }

        } catch (Exception e) {
            // 예외 발생 시 처리
            return "redirect:/order/admin/po/" + orderId + "?cancelError";
        }
    }




    // mypage : 주문 내용 상세 (주문 결과 상세 확인)
    @GetMapping("/po/detail/{orderId}")
    public String getOrderDetailPage(Model model, @PathVariable("orderId") String orderId, @RequestParam(value = "success", required = false) String success){

        if (success != null) { //결제 직후 출력 페이지
            model.addAttribute("orderSuccess", true);
        } else { //history 접근 시 페이지
            model.addAttribute("orderSuccess", false);
        }

        PurchaseOrder po = orderService.getOnePO(orderId);
        PoDetailForm poDetail =orderService.getOnePoDetail(orderId);

        List<OrderItemProcessDTO> orderItemList = orderService.getOrderItemProcessDtoList(po);

        model.addAttribute("orderItemList", orderItemList);
        model.addAttribute("poDetail", poDetail);
        model.addAttribute("cancelBtn", orderService.isCancelBtn(success, po.getStatus()));

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
    public String viewPoDetail(@PathVariable(name = "orderId") String orderId, @RequestParam(required = false, name = "reason") String reason, Model model){

        if(reason != null){
            model.addAttribute("cancelReason", reason);
        }
        return "/order/admin_poDetail";
    }

    // admin : 결제 취소 요청 대기리스트 접속
    @GetMapping("/admin/cancel/request/list")
    public String viewCancelRequestList(Model model){

        List<CancelRequestForm> cancelRequestList = orderService.getCancelRequestList();

        model.addAttribute("cancelRequestList", cancelRequestList);

        return "/order/admin_cancelRequestList";
    }






}
