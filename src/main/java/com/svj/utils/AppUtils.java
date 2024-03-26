package com.svj.utils;

import com.svj.dto.OrderRequestDTO;
import com.svj.entity.Order;

public class AppUtils {
    public static Order mapDTOToEntity(OrderRequestDTO requestDTO){
        Order order = Order.builder()
        		.id(requestDTO.getId())
        		.name(requestDTO.getName())
        		.category(requestDTO.getCategory())
        		.price(requestDTO.getPrice())
        		.purchaseDate(requestDTO.getPurchaseDate())
        		.orderId(requestDTO.getOrderId())
        		.userId(requestDTO.getUserId())
        		.paymentMode(requestDTO.getPaymentMode())
        		.build();
        return order;
    }
}
