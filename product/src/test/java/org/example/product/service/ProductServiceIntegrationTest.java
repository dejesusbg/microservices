package org.example.product.service;

import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.example.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class ProductServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(null, "Integration Test Product", 200.0);
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateAndFetchProduct() {
        Product savedProduct = productService.createProduct(sampleProduct);
        assertNotNull(savedProduct.getId());

        Optional<Product> retrievedProduct = productService.getProductById(savedProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals("Integration Test Product", retrievedProduct.get().getName());
    }

    @Test
    void shouldRetrieveAllProducts() {
        productService.createProduct(sampleProduct);
        List<Product> products = productService.getAllProducts();
        assertFalse(products.isEmpty());
    }

    @Test
    void shouldUpdateProduct() {
        Product savedProduct = productService.createProduct(sampleProduct);
        Product updatedDetails = new Product(savedProduct.getId(), "Updated Product", 250.0);
        Product updatedProduct = productService.updateProduct(savedProduct.getId(), updatedDetails);

        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(250.0, updatedProduct.getPrice());
    }

    @Test
    void shouldDeleteProduct() {
        Product savedProduct = productService.createProduct(sampleProduct);
        productService.deleteProduct(savedProduct.getId());

        Optional<Product> deletedProduct = productService.getProductById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }
}
