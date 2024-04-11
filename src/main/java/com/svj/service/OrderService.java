package com.svj.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svj.dto.OrderRequestDTO;
import com.svj.dto.OrderResponseDTO;
import com.svj.dto.PaymentDTO;
import com.svj.dto.UserDTO;
import com.svj.entity.Order;
import com.svj.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static com.svj.utils.AppUtils.mapDTOToEntity;

@Service
@RefreshScope
public class OrderService {

    public static final String ORDER_SERVICE = "orderService";
    private OrderRepository repository;
    @Lazy
    private RestTemplate restTemplate;
//    private KafkaTemplate<String, Object> kafkaTemplate;

//    @Value("${order.producer.topic.name}")
//    private String topicName;

    @Value("${microservices.endpoints.payment-service.fetchPaymentById}")
    private String fetchPaymentUri;
    @Value("${microservices.endpoints.user-service.fetchUserById}")
    private String fetchUserUri;

    @Value("${test.input}")
    private String testInput;

    private StreamBridge streamBridge;

    private ObjectMapper objectMapper;

    public OrderService(OrderRepository repository, RestTemplate restTemplate, ObjectMapper objectMapper, StreamBridge streamBridge){
        this.repository= repository;
        this.restTemplate= restTemplate;
//        this.kafkaTemplate= kafkaTemplate;
        this.objectMapper= objectMapper;
        this.streamBridge= streamBridge;
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
//            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(savedOrder));
            streamBridge.send("orderBinding-out-0", objectMapper.writeValueAsString(savedOrder));
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // user log.error
        }
        return "Your order with order ID ( "+ orderReq.getOrderId()+" ) has been place. We will notify once it is confirmed.";
    }

    // get order
    @CircuitBreaker(name = ORDER_SERVICE, fallbackMethod = "getOrderDetails")
    public OrderResponseDTO getOrder(String orderId){
        System.out.println("*************** Test Value is "+testInput);
        System.out.println("FetchPaymentUri: "+fetchPaymentUri+" && FetchUserUri: "+fetchUserUri);
        // order details- own DB
        Order order= repository.findByOrderId(orderId);
        // payment and user details - from resp services
//        String SERVICE_PAYMENTS_URL = "http://PAYMENT-SERVICE/payments/";
//        String SERVICE_USERS_URL = "http://USER-SERVICE/users/";
        PaymentDTO paymentDTO = restTemplate.getForObject(fetchPaymentUri + orderId, PaymentDTO.class);
        UserDTO userDTO= restTemplate.getForObject(fetchUserUri+order.getUserId(), UserDTO.class);
        return OrderResponseDTO.builder()
                .order(order)
                .paymentResponse(paymentDTO)
                .userInfo(userDTO)
                .build();
    }

    public OrderResponseDTO getOrderDetails(Exception ex){
        // you can call a DB/ invoke external api
        return new OrderResponseDTO("FAILED", null, null, null);
    }

}
