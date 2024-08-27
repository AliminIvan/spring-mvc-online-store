package com.ivanalimin.spring_mvc_online_store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanalimin.spring_mvc_online_store.dto.OrderDTO;
import com.ivanalimin.spring_mvc_online_store.exception_handling.NotFoundException;
import com.ivanalimin.spring_mvc_online_store.model.Order;
import com.ivanalimin.spring_mvc_online_store.model.OrderStatus;
import com.ivanalimin.spring_mvc_online_store.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService service;

    @Test
    void test_get_order_by_id_success() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);

        when(service.findById(orderId)).thenReturn(order);

        mockMvc.perform(get("/rest/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(order)));
    }

    @Test
    void test_get_order_by_id_not_found() throws Exception {
        UUID orderId = UUID.randomUUID();

        when(service.findById(orderId)).thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(get("/rest/orders/{id}", orderId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Order not found")));
    }

    @Test
    void test_create_order_success() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerId(UUID.randomUUID());
        orderDTO.setShippingAddress("123 Test St");
        orderDTO.setProductIds(List.of(UUID.randomUUID()));

        Order createdOrder = new Order();
        createdOrder.setOrderId(orderId);
        createdOrder.setOrderDate(LocalDate.now());
        createdOrder.setOrderStatus(OrderStatus.IN_PROGRESS);

        when(service.save(any(OrderDTO.class))).thenReturn(createdOrder);

        mockMvc.perform(post("/rest/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/rest/orders/" + orderId))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.orderDate").value(createdOrder.getOrderDate().toString()))
                .andExpect(jsonPath("$.orderStatus").value(createdOrder.getOrderStatus().name()));
    }

    @Test
    void test_create_order_bad_request() throws Exception {
        String invalidOrderJson = "{ \"customerId\": \"invalid-uuid\", \"shippingAddress\": \"\", \"productIds\": [] }";

        mockMvc.perform(post("/rest/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrderJson))
                .andExpect(status().isBadRequest());
    }
}