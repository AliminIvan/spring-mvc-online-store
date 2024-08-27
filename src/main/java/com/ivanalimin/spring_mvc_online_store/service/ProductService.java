package com.ivanalimin.spring_mvc_online_store.service;

import com.ivanalimin.spring_mvc_online_store.exception_handling.NotFoundException;
import com.ivanalimin.spring_mvc_online_store.model.Product;
import com.ivanalimin.spring_mvc_online_store.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product with id: " + id + " not found"));
    }

    @Transactional
    public Product save(Product product) {
        return repository.save(product);
    }

    @Transactional
    public Product update(UUID id, Product product) {
        return repository.findById(id)
                .map(productToUpdate -> {
                    productToUpdate.setName(product.getName());
                    productToUpdate.setDescription(product.getDescription());
                    productToUpdate.setPrice(product.getPrice());
                    productToUpdate.setQuantityInStock(product.getQuantityInStock());
                    return repository.save(productToUpdate);
                })
                .orElseThrow(() -> new NotFoundException("Product with id: " + id + " not found"));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Product with id: " + id + " not found");
        }
        repository.deleteById(id);
    }
}
