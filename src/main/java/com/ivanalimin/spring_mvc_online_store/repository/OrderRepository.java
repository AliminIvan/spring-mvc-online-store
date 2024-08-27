package com.ivanalimin.spring_mvc_online_store.repository;

import com.ivanalimin.spring_mvc_online_store.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
