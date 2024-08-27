package com.ivanalimin.spring_mvc_online_store.service;

import com.ivanalimin.spring_mvc_online_store.exception_handling.NotFoundException;
import com.ivanalimin.spring_mvc_online_store.model.Product;
import com.ivanalimin.spring_mvc_online_store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
public class ProductServiceTest {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_find_all_products() {
        List<Product> products = Arrays.asList(new Product(), new Product());
        when(repository.findAll()).thenReturn(products);

        List<Product> result = service.findAll();
        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void test_find_product_by_id() {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        when(repository.findById(productId)).thenReturn(Optional.of(product));

        Product result = service.findById(productId);
        assertNotNull(result);
        verify(repository, times(1)).findById(productId);
    }

    @Test
    void test_find_product_by_id_not_found() {
        UUID productId = UUID.randomUUID();
        when(repository.findById(productId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> service.findById(productId));
        assertEquals("Product with id: " + productId + " not found", thrown.getMessage());
    }

    @Test
    void test_save_product() {
        Product product = new Product();
        when(repository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = service.save(product);
        assertNotNull(savedProduct);
        verify(repository, times(1)).save(product);
    }

    @Test
    void test_update_product_success() {
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product();
        existingProduct.setName("Old Product Name");
        when(repository.findById(productId)).thenReturn(Optional.of(existingProduct));

        Product updatedProduct = new Product();
        updatedProduct.setName("New Product Name");

        when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = service.update(productId, updatedProduct);
        assertNotNull(result);
        assertEquals("New Product Name", result.getName());
    }

    @Test
    void test_update_product_not_found() {
        UUID productId = UUID.randomUUID();
        Product product = new Product();
        when(repository.findById(productId)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> service.update(productId, product));
        assertEquals("Product with id: " + productId + " not found", thrown.getMessage());
        verify(repository, times(1)).findById(productId);
    }

    @Test
    void test_delete_product_success() {
        UUID productId = UUID.randomUUID();
        when(repository.existsById(productId)).thenReturn(true);

        service.delete(productId);
        verify(repository, times(1)).deleteById(productId);
    }


    @Test
    void test_delete_product_not_found() {
        UUID productId = UUID.randomUUID();
        when(repository.existsById(productId)).thenReturn(false);

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> service.delete(productId));
        assertEquals("Product with id: " + productId + " not found", thrown.getMessage());
        verify(repository, times(1)).existsById(productId);
    }
}