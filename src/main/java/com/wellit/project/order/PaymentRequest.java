package com.wellit.project.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String imp_uid;
    private String merchant_uid;
    private Integer paid_amount;
    private String buyer_email;
    private String buyer_name;
    private String status;
    private boolean success;
    private String pay_method;
    private String pg_provider;
}
