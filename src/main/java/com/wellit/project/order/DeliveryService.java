package com.wellit.project.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public Delivery getDelivery(String orderId){
        return deliveryRepository.findDeliveryByPurchaseOrder_OrderId(orderId);
    }

}
