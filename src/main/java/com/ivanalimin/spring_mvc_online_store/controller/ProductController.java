package com.ivanalimin.spring_mvc_online_store.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanalimin.spring_mvc_online_store.model.Product;
import com.ivanalimin.spring_mvc_online_store.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = ProductController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    static final String REST_URL = "/rest/products";

    private final ProductService service;

    private final ObjectMapper objectMapper;

    public ProductController(ProductService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<String> getAll() {
        List<Product> products = service.findAll();
        try {
            String jsonResponse = objectMapper.writeValueAsString(products);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable UUID id) {
        Product product = service.findById(id);
        try {
            String jsonResponse = objectMapper.writeValueAsString(product);
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody String productJson) {
        try {
            Product product = objectMapper.readValue(productJson, Product.class);
            Product createdProduct = service.save(product);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdProduct.getProductId())
                    .toUri();
            return ResponseEntity.created(location).body(createdProduct);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable UUID id, @RequestBody String productJson) {
        try {
            Product product = objectMapper.readValue(productJson, Product.class);
            Product productToUpdate = service.update(id, product);
            return ResponseEntity.ok(productToUpdate);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
