package com.svj.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svj.dto.OrderRequestDTO;
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
import java.util.UUID;

import static com.svj.utils.AppUtils.mapDTOToEntity;

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
    public String placeOrder(OrderRequestDTO orderReq){
        Order order= mapDTOToEntity(orderReq);
        // save in order-service db
        orderReq.setPurchaseDate(LocalDate.now());
        orderReq.setOrderId(UUID.randomUUID().toString().split("-")[0]);
        Order mappedOrder = mapDTOToEntity(orderReq);
        Order savedOrder = repository.save(mappedOrder);
        // send it to payment service using kafka
        try {
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(savedOrder));
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // user log.error
        }
        return "Your order with order ID ( "+ orderReq.getOrderId()+" ) has been place. We will notify once it is confirmed.";
    }

    // get order
    public OrderResponseDTO getOrder(String orderId){
        // order details- own DB
        Order order= repository.findByOrderId(orderId);
        // payment and user details - from resp services
        String SERVICE_PAYMENTS_URL = "http://PAYMENT-SERVICE/payments/";
        String SERVICE_USERS_URL = "http://USER-SERVICE/users/";
        PaymentDTO paymentDTO = restTemplate.getForObject(SERVICE_PAYMENTS_URL + orderId, PaymentDTO.class);
        UserDTO userDTO= restTemplate.getForObject(SERVICE_USERS_URL+order.getUserId(), UserDTO.class);
        return OrderResponseDTO.builder()
                .order(order)
                .paymentResponse(paymentDTO)
                .userInfo(userDTO)
                .build();
    }

}
