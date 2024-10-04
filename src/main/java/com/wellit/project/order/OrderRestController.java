package com.wellit.project.order;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    // admin페이지 주문 리스트 받아오기
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "page", defaultValue = "1") int page) {

        // 주문 목록 가져오기 (검색, 필터링 및 페이지네이션 적용)
        Page<PurchaseOrder> ordersPage = orderService.findOrders(search, status, startDate, endDate, page);

        // 반환할 데이터 구성
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> ordersList = new ArrayList<>();

        for (PurchaseOrder order : ordersPage.getContent()) {
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderId", order.getOrderId());
            orderData.put("memberName", order.getMember().getMemberName());
            orderData.put("memberId", order.getMember().getMemberId());
            orderData.put("status", order.getStatus().renderStatus());
            orderData.put("createdAt", order.getCreatedAt().toString());
            orderData.put("totalPay", order.getTotalPay());
            ordersList.add(orderData);
        }

        response.put("orders", ordersList);
        response.put("totalPages", ordersPage.getTotalPages());
        response.put("currentPage", page);

        return ResponseEntity.ok(response);
    }

    // 주문 처리용 상세 정보 1건 API
    @GetMapping("/{orderId}")
    public ResponseEntity<PoProcessForm> getOrderDetails(@PathVariable(value = "orderId") String orderId) {
        PoProcessForm poProcessForm = orderService.getOnePoProcess(orderId);

        return ResponseEntity.ok(poProcessForm);
    }




    // 출고 처리 API
    @PostMapping("/{orderId}/ship")
    public ResponseEntity<String> processShipment(@PathVariable(value = "orderId") String orderId, @RequestBody Map<String, String> body) {
        String invoiceNum = body.get("invoiceNum");
        orderService.shipOrder(orderId, invoiceNum);

        return ResponseEntity.ok("출고 처리 완료");
    }

    // 배송완료 처리 API
    @PostMapping("/{orderId}/deliveryComplete")
    public ResponseEntity<String> processDeliveryComplete(@PathVariable(value = "orderId") String orderId, @RequestBody Map<String, String> body) {
        String invoiceNum = body.get("invoiceNum");
        orderService.deliveryComplete(orderId, invoiceNum);

        return ResponseEntity.ok("배송 완료 처리");
    }



    // 주문 취소 신청
    @PostMapping("/cancelRequest")
    public ResponseEntity<String> waitingCancelRequest(@RequestBody Map<String, String> body){
        String orderId = body.get("orderId");
        String cancelReason = body.get("cancelReason");

        orderService.waitingCancelRequest(orderId, cancelReason);

        return ResponseEntity.ok("승인대기");
    }




}
