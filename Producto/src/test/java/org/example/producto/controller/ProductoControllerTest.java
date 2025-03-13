package org.example.producto.controller;

import org.example.producto.entity.Producto;
import org.example.producto.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;
    private Producto sampleProducto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        sampleProducto = new Producto("1", "Test Product", 100.0);
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        when(productoService.getAllProducts()).thenReturn(List.of(sampleProducto));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void shouldGetProductById() throws Exception {
        when(productoService.getProductById("1")).thenReturn(Optional.of(sampleProducto));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        when(productoService.createProduct(any(Producto.class))).thenReturn(sampleProducto);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Product\", \"price\": 100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        when(productoService.updateProduct(eq("1"), any(Producto.class))).thenReturn(sampleProducto);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Product\", \"price\": 150.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        doNothing().when(productoService).deleteProduct("1");

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
