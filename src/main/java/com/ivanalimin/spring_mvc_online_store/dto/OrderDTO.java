package com.ivanalimin.spring_mvc_online_store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class OrderDTO {

    @NotNull(message = "Customer ID cannot be null")
    private UUID customerId;

    @NotBlank(message = "Shipping address cannot be blank")
    private String shippingAddress;

    @NotEmpty(message = "Cart is empty")
    private List<UUID> productIds;
}
