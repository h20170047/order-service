package com.svj.entity;

import com.svj.validation.PaymentValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Entity
@Table(name = "ORDERS_TBL")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String category;
    private double price;
    private LocalDate purchaseDate;
    private String orderId;
    private int userId;
    private String paymentMode;
}
