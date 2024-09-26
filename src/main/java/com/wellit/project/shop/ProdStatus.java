package com.wellit.project.shop;

public enum ProdStatus {
    AVAILABLE("판매중"),
    UNAVAILABLE("판매일시중단"),
    OUT_OF_STOCK("일시품절"),
    DISCONTINUED("단종");

    private final String render;

    // Enum 생성자
    ProdStatus(String render) {
        this.render = render;
    }

    // 한글 상태명 반환 메서드
    public String renderStatus() {
        return render;
    }
}
