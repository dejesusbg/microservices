package org.example.product.repository;

import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class ProductRepositoryIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(null, "Integration Test Product", 300.0);
        productRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveProduct() {
        Product savedProduct = productRepository.save(sampleProduct);
        Optional<Product> retrievedProduct = productRepository.findById(savedProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals("Integration Test Product", retrievedProduct.get().getName());
    }

    @Test
    void shouldDeleteProduct() {
        Product savedProduct = productRepository.save(sampleProduct);
        productRepository.deleteById(savedProduct.getId());
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }
}
