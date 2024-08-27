package com.ivanalimin.spring_mvc_online_store.service;

import com.ivanalimin.spring_mvc_online_store.dto.OrderDTO;
import com.ivanalimin.spring_mvc_online_store.exception_handling.NotFoundException;
import com.ivanalimin.spring_mvc_online_store.model.Customer;
import com.ivanalimin.spring_mvc_online_store.model.Order;
import com.ivanalimin.spring_mvc_online_store.model.OrderStatus;
import com.ivanalimin.spring_mvc_online_store.model.Product;
import com.ivanalimin.spring_mvc_online_store.repository.CustomerRepository;
import com.ivanalimin.spring_mvc_online_store.repository.OrderRepository;
import com.ivanalimin.spring_mvc_online_store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@Transactional
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_find_all_orders() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.findAll();
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void test_find_order_by_id() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.findById(orderId);
        assertNotNull(result);
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void test_find_order_by_id_not_found() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> orderService.findById(orderId));
        assertEquals("Order with id " + orderId + " not found", thrown.getMessage());
    }

    @Test
    void test_save_order_success() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerId(UUID.randomUUID());
        orderDTO.setShippingAddress("123 Test St");
        orderDTO.setProductIds(List.of(UUID.randomUUID()));

        Customer customer = new Customer();
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);
        Set<Product> products = new HashSet<>(Collections.singletonList(product));

        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(anyList())).thenReturn(new ArrayList<>(products));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order savedOrder = orderService.save(orderDTO);
        assertNotNull(savedOrder);
        assertEquals("123 Test St", savedOrder.getShippingAddress());
        assertEquals(BigDecimal.TEN, savedOrder.getTotalPrice());
    }

    @Test
    void test_update_order_success() {
        UUID orderId = UUID.randomUUID();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setCustomerId(UUID.randomUUID());
        orderDTO.setShippingAddress("456 Test Ave");
        orderDTO.setProductIds(List.of(UUID.randomUUID()));

        Customer customer = new Customer();
        Product product = new Product();
        product.setPrice(BigDecimal.TEN);
        Set<Product> products = new HashSet<>(Collections.singletonList(product));

        Order existingOrder = new Order();
        existingOrder.setOrderDate(LocalDate.now());
        existingOrder.setOrderStatus(OrderStatus.IN_PROGRESS);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(anyList())).thenReturn(new ArrayList<>(products));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updatedOrder = orderService.update(orderId, orderDTO);
        assertNotNull(updatedOrder);
        assertEquals("456 Test Ave", updatedOrder.getShippingAddress());
        assertEquals(BigDecimal.TEN, updatedOrder.getTotalPrice());
    }

    @Test
    void test_delete_order_success() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsById(orderId)).thenReturn(true);

        orderService.delete(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void test_delete_order_not_found() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsById(orderId)).thenReturn(false);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> orderService.delete(orderId));
        assertEquals("Order with id " + orderId + " not found", thrown.getMessage());
    }
}