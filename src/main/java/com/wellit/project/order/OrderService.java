package com.wellit.project.order;

import com.wellit.project.member.Member;
import com.wellit.project.member.MemberService;
import com.wellit.project.shop.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class OrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final MemberService memberService;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;
    private final CartItemRepository cartItemRepository;

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

        try{
            poForm.getAddr2();
        } catch (Exception e){
            poForm.setAddr2("");
        }

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
        log.info("금액 업뎃 저장완료");


        //카트 비우는 로직
        Cart cart = po.getMember().getCart();
        cartItemRepository.deleteByCart(cart);
        log.info("카트 비우기 완료");

        purchaseOrderRepository.save(po);


        return true;

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