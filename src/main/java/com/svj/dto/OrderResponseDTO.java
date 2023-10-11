package com.svj.dto;

import com.svj.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponseDTO {
    private Order order;
    private PaymentDTO paymentResponse;
    private UserDTO userInfo;
}
