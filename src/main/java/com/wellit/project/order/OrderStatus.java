package com.wellit.project.order;

public enum OrderStatus {
    PAYMENT_WAIT("결제대기"),
    PRODUCT_PREPARE("상품준비중"),
    DELIVERING("배송중"),
    COMPLETED("배송완료"),
    CANCELLED("주문취소"),
    WAITING_CANCEL("취소승인대기중");

    private final String render;

    // Enum 생성자
    OrderStatus(String render) {
        this.render = render;
    }

    // 한글 이름 반환 메서드
    public String renderStatus() {
        return render;
    }
}
