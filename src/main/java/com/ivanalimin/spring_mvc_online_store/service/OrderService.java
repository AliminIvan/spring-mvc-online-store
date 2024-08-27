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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrderService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(CustomerRepository customerRepository, OrderRepository orderRepository,
                        ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    }

    @Transactional
    public Order save(OrderDTO orderDTO) {
        Order order = new Order();
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));
        order.setCustomer(customer);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.IN_PROGRESS);
        order.setShippingAddress(orderDTO.getShippingAddress());
        Set<Product> products = findAndValidateProducts(orderDTO.getProductIds());
        order.setProducts(products);
        order.setTotalPrice(calculateTotalPrice(products));
        return orderRepository.save(order);
    }

    @Transactional
    public Order update(UUID orderId, OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found"));
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));
        order.setCustomer(customer);
        Set<Product> products = findAndValidateProducts(orderDTO.getProductIds());
        order.setProducts(products);
        order.setTotalPrice(calculateTotalPrice(products));
        order.setShippingAddress(orderDTO.getShippingAddress());
        return orderRepository.save(order);
    }

    @Transactional
    public void delete(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new NotFoundException("Order with id " + orderId + " not found");
        }
        orderRepository.deleteById(orderId);
    }

    private Set<Product> findAndValidateProducts(List<UUID> productIds) {
        if (productIds != null && !productIds.isEmpty()) {
            List<Product> products = productRepository.findAllById(productIds);
            if (products.size() != productIds.size()) {
                throw new IllegalArgumentException("One or more product IDs are invalid");
            }
            return new HashSet<>(products);
        }
        return Collections.emptySet();
    }

    private BigDecimal calculateTotalPrice(Set<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
