package com.svj.dto;

import com.svj.entity.Order;
import com.svj.validation.PaymentValidation;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRequestDTO {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String category;
    @Positive
    private double price;
    private LocalDate purchaseDate;
    private String orderId;
    @NotEmpty
    private int userId;
    @PaymentValidation
    private String paymentMode;
}
