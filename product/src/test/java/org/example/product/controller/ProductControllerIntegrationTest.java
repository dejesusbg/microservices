package org.example.product.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    private RestTemplate restTemplate;
    private String baseUrl;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port + "/api/products";
        sampleProduct = new Product(null, "Controller Test Product", 400.0);
        productRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveProduct() {
        ResponseEntity<Product> response = restTemplate.postForEntity(baseUrl, sampleProduct, Product.class);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        ResponseEntity<Product> retrieved = restTemplate.getForEntity(baseUrl + "/" + response.getBody().getId(), Product.class);
        assertEquals("Controller Test Product", retrieved.getBody().getName());
    }

    @Test
    void shouldRetrieveAllProducts() {
        restTemplate.postForEntity(baseUrl, sampleProduct, Product.class);
        ResponseEntity<Product[]> response = restTemplate.getForEntity(baseUrl, Product[].class);
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void shouldUpdateProduct() {
        ResponseEntity<Product> response = restTemplate.postForEntity(baseUrl, sampleProduct, Product.class);
        Product updatedProduct = new Product(response.getBody().getId(), "Updated Controller Product", 450.0);
        restTemplate.put(baseUrl + "/" + response.getBody().getId(), updatedProduct);

        ResponseEntity<Product> retrieved = restTemplate.getForEntity(baseUrl + "/" + response.getBody().getId(), Product.class);
        assertEquals("Updated Controller Product", retrieved.getBody().getName());
    }

    @Test
    void shouldDeleteProduct() {
        ResponseEntity<Product> response = restTemplate.postForEntity(baseUrl, sampleProduct, Product.class);
        restTemplate.delete(baseUrl + "/" + response.getBody().getId());

        ResponseEntity<Product> retrieved = restTemplate.getForEntity(baseUrl + "/" + response.getBody().getId(), Product.class);
        assertNull(retrieved.getBody());
    }
}
