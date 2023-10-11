package com.svj.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svj.dto.OrderResponseDTO;
import com.svj.dto.PaymentDTO;
import com.svj.dto.UserDTO;
import com.svj.entity.Order;
import com.svj.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Service
public class OrderService {

    private OrderRepository repository;
    private RestTemplate restTemplate;
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${order.producer.topic.name}")
    private String topicName;

    private ObjectMapper objectMapper;

    public OrderService(OrderRepository repository, KafkaTemplate kafkaTemplate, RestTemplate restTemplate, ObjectMapper objectMapper){
        this.repository= repository;
        this.restTemplate= restTemplate;
        this.kafkaTemplate= kafkaTemplate;
        this.objectMapper= objectMapper;
    }

    // place an order
    public String placeOrder(Order order){
        // save in order-service db
        order.setPurchaseDate(LocalDate.now());
//        order.setPurchaseDate(new Date());
        order.setOrderId(UUID.randomUUID().toString().split("-")[0]);
        repository.save(order);
        // send it to payment service using kafka
        try {
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(order));
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // user log.error
        }
        return "Your order with order ID ( "+ order.getOrderId()+" ) has been place. We will notify once it is confirmed.";
    }

    // get order
    public OrderResponseDTO getOrder(String orderId){
        // order details- own DB
        Order order= repository.findByOrderId(orderId);
        // payment and user details - from resp services
        String SERVICE_PAYMENTS_URL = "http://localhost:9292/payments/";
        String SERVICE_USERS_URL = "http://localhost:9393/users/";
        PaymentDTO paymentDTO = restTemplate.getForObject(SERVICE_PAYMENTS_URL + orderId, PaymentDTO.class);
        UserDTO userDTO= restTemplate.getForObject(SERVICE_USERS_URL+order.getUserId(), UserDTO.class);
        return OrderResponseDTO.builder()
                .order(order)
                .paymentResponse(paymentDTO)
                .userInfo(userDTO)
                .build();
    }

}
