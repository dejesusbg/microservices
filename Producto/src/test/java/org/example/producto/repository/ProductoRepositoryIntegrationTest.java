package org.example.producto.repository;

import org.example.producto.entity.Producto;
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
    private ProductoRepository productoRepository;

    private Producto sampleProducto;

    @BeforeEach
    void setUp() {
        sampleProducto = new Producto(null, "Integration Test Product", 300.0);
        productoRepository.deleteAll();
    }

    @Test
    void shouldSaveAndRetrieveProduct() {
        Producto savedProduct = productoRepository.save(sampleProducto);
        Optional<Producto> retrievedProduct = productoRepository.findById(savedProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals("Integration Test Product", retrievedProduct.get().getName());
    }

    @Test
    void shouldDeleteProduct() {
        Producto savedProduct = productoRepository.save(sampleProducto);
        productoRepository.deleteById(savedProduct.getId());
        Optional<Producto> deletedProduct = productoRepository.findById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }
}
