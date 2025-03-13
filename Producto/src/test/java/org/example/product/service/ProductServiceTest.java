package org.example.product.service;

import org.example.product.entity.Product;
import org.example.product.repository.ProductRepository;
import org.example.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product("1", "Test Product", 100.0);
    }

    @Test
    void shouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("Test Product", products.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnProductById() {
        when(productRepository.findById("1")).thenReturn(Optional.of(sampleProduct));

        Optional<Product> product = productService.getProductById("1");

        assertTrue(product.isPresent());
        assertEquals("Test Product", product.get().getName());
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void shouldCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product createdProduct = productService.createProduct(sampleProduct);

        assertNotNull(createdProduct);
        assertEquals("Test Product", createdProduct.getName());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void shouldUpdateProduct() {
        Product updatedDetails = new Product("1", "Updated Product", 150.0);
        when(productRepository.findById("1")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedDetails);

        Product updatedProduct = productService.updateProduct("1", updatedDetails);

        assertNotNull(updatedProduct);
        assertEquals("Updated Product", updatedProduct.getName());
        assertEquals(150.0, updatedProduct.getPrice());
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {
        doNothing().when(productRepository).deleteById("1");

        productService.deleteProduct("1");

        verify(productRepository, times(1)).deleteById("1");
    }
}
