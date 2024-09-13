package com.wellit.project.order;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.shop.Product;
import com.wellit.project.shop.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class OrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MemberService memberService;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final CartItemRepository cartItemRepository;
    private final DeliveryService deliveryService;

    /*주문 생성*/
    public PurchaseOrder addOrder(OrderForm orderForm, String memberId){

        PurchaseOrder po = new PurchaseOrder();
        po.setMember(memberService.getMember(memberId));

        List<OrderItem> orderItems = new ArrayList<>();

        List<OrderItemQuantity> orderItemQuantityList= orderForm.getOrderItemQuantityList();

        /* 금액 관련 필드 */
        Integer orgPrice=0;
        Integer discPrice=0;
        Integer deliveryFee = 0;

        //OrderItem-Product연결
        for (OrderItemQuantity orderItemQuantity : orderItemQuantityList) {
            if (!orderItemQuantity.isBooleanOrder()) continue;
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(productRepository.findById(orderItemQuantity.getProdId()).orElseThrow());
            orderItem.setQuantity(orderItemQuantity.getQuantity());
            orderItem.setSumOrgPrice(orderItemQuantity.getSumOrgPrice());
            orderItem.setSumDiscPrice(orderItemQuantity.getSumDiscPrice());
            orderItem.setPurchaseOrder(po);

            orgPrice += orderItemQuantity.getSumOrgPrice();
            discPrice += orderItemQuantity.getSumDiscPrice();

            orderItems.add(orderItem);
        }

        //금액 업데이트
        po.setOrderItems(orderItems);
        po.setOrgPrice(orgPrice);
        po.setDiscPrice(discPrice);

        // 50000원 이상 구매 시 배송비 무료
        if((orgPrice+discPrice) > 0 && (orgPrice+discPrice) <50000) deliveryFee = 3000;
        else deliveryFee = 0;

        po.setDeliveryFee(deliveryFee);
        po.setTotalPrice(orgPrice + discPrice + deliveryFee);

        po.setStatus(OrderStatus.PAYMENT_WAIT); // 주문 상태 업데이트
        po.setMilePay(0);
        po.setTotalPay(po.getTotalPrice());



        return purchaseOrderRepository.save(po);

    }

    /* 주문 검색 */
    public PurchaseOrder getOnePO(String orderId){
        log.info(orderId);
        return purchaseOrderRepository.findById(orderId).orElseThrow();
    }

    /*주문서 생성*/
    public PoForm getPoForm(String orderId){

        PurchaseOrder po = this.getOnePO(orderId);

        PoForm poForm = new PoForm();
        Member member = po.getMember();

        poForm.setOrderId(orderId);
        poForm.setAddr1(member.getRoadAddress());
        poForm.setAddr2(member.getAddressDetail());
        poForm.setDeliveryName(member.getMemberName());
        //poForm.setDeliveryPhone(member.getMemberPhone());

        String memberPhone = member.getMemberPhone();
        poForm.setDeliveryPhone(memberPhone);
        poForm.setPhone1(memberPhone.substring(0,3));
        if(memberPhone.length() == 11){
            poForm.setPhone2(memberPhone.substring(3,7));
            poForm.setPhone3(memberPhone.substring(7,11));
        } else {
            poForm.setPhone2(memberPhone.substring(3,6));
            poForm.setPhone3(memberPhone.substring(6,10));
        }


        /* 결제금액 필드 */
        poForm.setOrgPrice(po.getOrgPrice());
        poForm.setDiscPrice(po.getDiscPrice());
        poForm.setFinalPrice(poForm.getOrgPrice() + poForm.getDiscPrice());
        poForm.setDeliveryFee(po.getDeliveryFee());
        poForm.setTotalPrice(poForm.getFinalPrice() + poForm.getDeliveryFee());
        poForm.setMilePay(0);
        poForm.setTotalPay(poForm.getTotalPrice() +poForm.getMilePay());




        return poForm;
    }

    /*OrderItem 리스트 리턴*/
    public List<OrderItem> getOrderItemList(String orderId){
        return this.getOnePO(orderId).getOrderItems();
    }


    /*결제성공 후 주문서 변경내용 업데이트, 배송정보 설정*/
    @Transactional
    public boolean updatePurchaseOrderInfo(String orderId, PoForm poForm, Principal principal){
        Payment payment = paymentService.getPayment(orderId);

        if (payment == null) {
            throw new IllegalStateException("결제 정보가 없습니다. Order ID: " + orderId);
        }

        if (!payment.isSuccess()) {
            return false;
        }


        PurchaseOrder po = getOnePO(orderId);

        //po상태 업데이트
        po.setStatus(OrderStatus.PRODUCT_PREPARE);



        // 배송 정보 설정
        Delivery delivery = new Delivery();

        log.info(poForm.getAddr1());
        log.info(poForm.getAddr2());

        delivery.setAddr1(poForm.getAddr1());
        delivery.setAddr2(poForm.getAddr2());
        delivery.setDeliveryMsg(poForm.getDeliveryMsg());
        delivery.setDeliveryStatus(DeliveryStatus.PENDING); //집화 대기 상태로 업데이트
        delivery.setDeliveryName(poForm.getDeliveryName());
        delivery.setDeliveryPhone(poForm.getPhone1()+poForm.getPhone2()+poForm.getPhone3());
        delivery.setPurchaseOrder(po);
        po.setDelivery(delivery);
        log.info("딜리버리 저장완료");

        // 금액 업데이트
        po.setMilePay(poForm.getMilePay());
        po.setTotalPay(poForm.getTotalPay());
        memberService.updateMileage(principal.getName(), poForm.getMilePay());
        log.info("금액 업뎃 저장완료");


        //카트 비우는 로직
        Cart cart = po.getMember().getCart();
        cartItemRepository.deleteByCart(cart);
        log.info("카트 비우기 완료");

        purchaseOrderRepository.save(po);


        return true;

    }



    // mypage : 주문내역 리스트
    public List<PoHistoryForm> getPoHistoryList(String memberId){

        List<PurchaseOrder> poList = purchaseOrderRepository.findAllByMember_MemberIdAndStatusNot(memberId, OrderStatus.PAYMENT_WAIT);

        List<PoHistoryForm> poHistoryList = poList.stream().map(this::poHistoryConvertToDTO)
                                                  .collect(Collectors.toList());

        return poHistoryList;
    }

    // mypage : 주문 내역 DTO로 변환하는 메서드
    private PoHistoryForm poHistoryConvertToDTO(PurchaseOrder po){
        PoHistoryForm dto = new PoHistoryForm();

        //PurchaseOrder 기본정보 추가
        dto.setOrderId(po.getOrderId());
        dto.setOrderStatus(po.getStatus());
        dto.setTotalPrice(po.getTotalPrice());


        //Payment 정보
        Payment payment = paymentService.getPayment(po.getOrderId());
        dto.setPaidAt(payment.getCreatedAt());
        dto.setPaymentStatus(payment.getPaymentStatus());

        //Delivery 정보
        Delivery delivery = deliveryService.getDelivery(po.getOrderId());
        dto.setDeliveryStatus(delivery.getDeliveryStatus());
//        dto.setAddr1(delivery.getAddr1());
//        dto.setAddr2(delivery.getAddr2());
//        dto.setDeliveryName(delivery.getDeliveryName());
//        dto.setDeliveryPhone(delivery.getDeliveryPhone());
//        dto.setDeliveryMsg(delivery.getDeliveryMsg());
//        if(delivery.getInvoiceNum() !=null){
//            dto.setInvoiceNum(delivery.getInvoiceNum());
//        } else dto.setInvoiceNum("99999"); //송장번호 입력 전인 경우 '99999' 전달




        //ItemList 추가 (PO별 구매 아이템)

        /*List<OrderItemDTO> orderItemDTOList = po.getOrderItems().stream()
                                                .map(orderItem -> {
                                                    OrderItemDTO itemDTO = new OrderItemDTO();
                                                    Product product = orderItem.getProduct();

                                                    itemDTO.setProdId(product.getProdId());
                                                    itemDTO.setProdName(product.getProdName());
                                                    itemDTO.setProdThumb(product.getProdMainImg());
                                                    itemDTO.setProdOrgPrice(product.getProdOrgPrice());
                                                    itemDTO.setProdFinalPrice(product.getProdFinalPrice());

                                                    itemDTO.setQuantity(orderItem.getQuantity());
                                                    itemDTO.setSumFinalPrice(orderItem.getSumOrgPrice()+orderItem.getSumDiscPrice());

                                                    return itemDTO;
                                                }).collect(Collectors.toList());*/
        dto.setOrderItems(this.getOrderItemDtoList(po));

        // totalPrice = SumFinalPrice 합계 계산
        /*Integer totalPrice = orderItemDTOList.stream()
                                             .mapToInt(OrderItemDTO::getSumFinalPrice) // 각 항목의 sumFinalPrice를 int로 변환
                                             .sum();

        dto.setTotalPrice(totalPrice);*/

        return dto;

    }





    // 주문 상품 detail 조회
    public PoDetailForm getOnePoDetail(String orderId){
        PurchaseOrder po = purchaseOrderRepository.findById(orderId).get();
        return this.poDetailConvertToDTO(po);
    }

    // 주문 내역 1건 DTO로 변환
    private PoDetailForm poDetailConvertToDTO(PurchaseOrder po){
        PoDetailForm dto = new PoDetailForm();

        //PurchaseOrder 기본정보 추가
        dto.setOrderId(po.getOrderId());
        dto.setOrderStatus(po.getStatus());

        //PurchaseOrder 금액 필드
        dto.setOrgPrice(po.getOrgPrice() );
        dto.setDiscPrice(po.getDiscPrice() );
        dto.setFinalPrice(po.getOrgPrice() + po.getDiscPrice());
        dto.setDeliveryFee(po.getDeliveryFee() );
        dto.setTotalPrice(po.getTotalPrice() );
        dto.setMilePay(po.getMilePay() );
        dto.setTotalPay(po.getTotalPay() );



        //Payment 정보
        Payment payment = paymentService.getPayment(po.getOrderId());
        dto.setPaidAt(payment.getCreatedAt());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPgProvider(payment.getPgProvider());

        //Delivery 정보
        Delivery delivery = deliveryService.getDelivery(po.getOrderId());
        dto.setDeliveryStatus(delivery.getDeliveryStatus());
        dto.setAddr1(delivery.getAddr1());
        dto.setAddr2(delivery.getAddr2());
        dto.setDeliveryName(delivery.getDeliveryName());
        dto.setDeliveryPhone( this.formatPhoneNumber( delivery.getDeliveryPhone()));
        dto.setDeliveryMsg(delivery.getDeliveryMsg());
        if(delivery.getInvoiceNum() !=null){
            dto.setInvoiceNum(delivery.getInvoiceNum());
        } else dto.setInvoiceNum("99999"); //송장번호 입력 전인 경우 '99999' 전달




        //ItemList 추가 (PO별 구매 아이템)
        List<OrderItemDTO> orderItemDTOList = this.getOrderItemDtoList(po);
        dto.setOrderItems(orderItemDTOList);

        // totalPrice = SumFinalPrice 합계 계산
/*        Integer totalPrice = orderItemDTOList.stream()
                                             .mapToInt(OrderItemDTO::getSumFinalPrice) // 각 항목의 sumFinalPrice를 int로 변환
                                             .sum();

        dto.setTotalPrice(totalPrice);*/

        return dto;

    }

    // 구매 아이템 조회 (PO별 구매 아이템 리스트 반환)
    private List<OrderItemDTO> getOrderItemDtoList(PurchaseOrder po){
        List<OrderItemDTO> orderItemDTOList = po.getOrderItems().stream()
                                                .map(orderItem -> {
                                                    OrderItemDTO itemDTO = new OrderItemDTO();
                                                    Product product = orderItem.getProduct();

                                                    itemDTO.setProdId(product.getProdId());
                                                    itemDTO.setProdName(product.getProdName());
                                                    itemDTO.setProdThumb(product.getProdMainImg());
                                                    itemDTO.setProdOrgPrice(product.getProdOrgPrice());
                                                    itemDTO.setProdFinalPrice(product.getProdFinalPrice());

                                                    itemDTO.setQuantity(orderItem.getQuantity());
                                                    itemDTO.setSumFinalPrice(orderItem.getSumOrgPrice()+orderItem.getSumDiscPrice());

                                                    return itemDTO;
                                                }).collect(Collectors.toList());
        return orderItemDTOList;
    }





















    //전화번호 포맷팅
    public String formatPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("[^0-9]", ""); // 숫자만 남기기
        if (phoneNumber.length() == 11) {
            return phoneNumber.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3"); // 11자리 형식
        } else if (phoneNumber.length() == 10) {
            return phoneNumber.replaceAll("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3"); // 10자리 형식
        } else {
            return phoneNumber; // 형식이 맞지 않으면 그대로 출력
        }
    }

}
