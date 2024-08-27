package com.ivanalimin.spring_mvc_online_store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanalimin.spring_mvc_online_store.exception_handling.NotFoundException;
import com.ivanalimin.spring_mvc_online_store.model.Product;
import com.ivanalimin.spring_mvc_online_store.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;

    @InjectMocks
    private ProductController controller;

    @Test
    void test_get_all_products_success() throws Exception {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(19.99));

        List<Product> products = Collections.singletonList(product);

        when(service.findAll()).thenReturn(products);

        mockMvc.perform(get("/rest/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(products)));
    }

    @Test
    void test_get_product_by_id_success() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(19.99));

        when(service.findById(productId)).thenReturn(product);

        mockMvc.perform(get("/rest/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(product.getProductId().toString()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice().doubleValue()));
    }

    @Test
    void test_get_product_by_id_not_found() throws Exception {
        UUID productId = UUID.randomUUID();

        when(service.findById(productId)).thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(get("/rest/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Product not found")));
    }

    @Test
    void test_create_product_success() throws Exception {
        Product product = new Product();
        product.setProductId(UUID.randomUUID());
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(19.99));

        when(service.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/rest/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(product.getProductId().toString()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice().doubleValue()));
    }

    @Test
    void test_update_product_success() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        product.setProductId(productId);
        product.setName("Updated Product");
        product.setPrice(BigDecimal.valueOf(29.99));

        when(service.update(eq(productId), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/rest/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(product.getProductId().toString()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.price").value(product.getPrice().doubleValue()));
    }

    @Test
    void test_delete_product_success() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(delete("/rest/products/{id}", productId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(productId);
    }
}