package com.ivanalimin.spring_mvc_online_store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanalimin.spring_mvc_online_store.dto.OrderDTO;
import com.ivanalimin.spring_mvc_online_store.model.Order;
import com.ivanalimin.spring_mvc_online_store.service.OrderService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = OrderController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    static final String REST_URL = "/rest/orders";

    private final OrderService service;

    private final ObjectMapper objectMapper;

    public OrderController(OrderService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable UUID id) {
        try {
            Order order = service.findById(id);
            String jsonResponse = objectMapper.writeValueAsString(order);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody String orderJson) {
        try {
            OrderDTO orderDTO = objectMapper.readValue(orderJson, OrderDTO.class);
            Order createdOrder = service.save(orderDTO);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdOrder.getOrderId())
                    .toUri();
            return ResponseEntity.created(location).body(createdOrder);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
