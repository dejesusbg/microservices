package org.example.producto.controller;

import org.example.producto.entity.Producto;
import org.example.producto.repository.ProductoRepository;
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
    private ProductoRepository productoRepository;

    private RestTemplate restTemplate;
    private String baseUrl;
    private Producto sampleProduct;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port + "/api/products";
        sampleProduct = new Producto(null, "Controller Test Product", 400.0);
        productoRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveProduct() {
        ResponseEntity<Producto> response = restTemplate.postForEntity(baseUrl, sampleProduct, Producto.class);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());

        ResponseEntity<Producto> retrieved = restTemplate.getForEntity(baseUrl + "/" + response.getBody().getId(), Producto.class);
        assertEquals("Controller Test Product", retrieved.getBody().getName());
    }

    @Test
    void shouldRetrieveAllProducts() {
        restTemplate.postForEntity(baseUrl, sampleProduct, Producto.class);
        ResponseEntity<Producto[]> response = restTemplate.getForEntity(baseUrl, Producto[].class);
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void shouldUpdateProduct() {
        ResponseEntity<Producto> response = restTemplate.postForEntity(baseUrl, sampleProduct, Producto.class);
        Producto updatedProduct = new Producto(response.getBody().getId(), "Updated Controller Product", 450.0);
        restTemplate.put(baseUrl + "/" + response.getBody().getId(), updatedProduct);

        ResponseEntity<Producto> retrieved = restTemplate.getForEntity(baseUrl + "/" + response.getBody().getId(), Producto.class);
        assertEquals("Updated Controller Product", retrieved.getBody().getName());
    }

    @Test
    void shouldDeleteProduct() {
        ResponseEntity<Producto> response = restTemplate.postForEntity(baseUrl, sampleProduct, Producto.class);
        restTemplate.delete(baseUrl + "/" + response.getBody().getId());

        ResponseEntity<Producto> retrieved = restTemplate.getForEntity(baseUrl + "/" + response.getBody().getId(), Producto.class);
        assertNull(retrieved.getBody());
    }
}
