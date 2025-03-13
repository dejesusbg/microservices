package org.example.producto.service;

import org.example.producto.entity.Producto;
import org.example.producto.repository.ProductoRepository;
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
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    private Producto sampleProducto;

    @BeforeEach
    void setUp() {
        sampleProducto = new Producto(null, "Integration Test Product", 200.0);
        productoRepository.deleteAll();
    }

    @Test
    void shouldCreateAndFetchProduct() {
        Producto savedProduct = productoService.createProduct(sampleProducto);
        assertNotNull(savedProduct.getId());

        Optional<Producto> retrievedProduct = productoService.getProductById(savedProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals("Integration Test Product", retrievedProduct.get().getName());
    }

    @Test
    void shouldRetrieveAllProducts() {
        productoService.createProduct(sampleProducto);
        List<Producto> products = productoService.getAllProducts();
        assertFalse(products.isEmpty());
    }

    @Test
    void shouldUpdateProduct() {
        Producto savedProduct = productoService.createProduct(sampleProducto);
        Producto updatedDetails = new Producto(savedProduct.getId(), "Updated Product", 250.0);
        Producto updatedProduct = productoService.updateProduct(savedProduct.getId(), updatedDetails);

        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(250.0, updatedProduct.getPrice());
    }

    @Test
    void shouldDeleteProduct() {
        Producto savedProduct = productoService.createProduct(sampleProducto);
        productoService.deleteProduct(savedProduct.getId());

        Optional<Producto> deletedProduct = productoService.getProductById(savedProduct.getId());
        assertFalse(deletedProduct.isPresent());
    }
}
