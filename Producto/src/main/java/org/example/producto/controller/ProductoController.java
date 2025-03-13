package org.example.producto.controller;

import lombok.RequiredArgsConstructor;
import org.example.producto.entity.Producto;
import org.example.producto.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public List<Producto> getAllProducts() {
        return productoService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductById(@PathVariable String id) {
        Optional<Producto> product = productoService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Producto createProduct(@RequestBody Producto product) {
        return productoService.createProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProduct(@PathVariable String id, @RequestBody Producto productDetails) {
        try {
            Producto updatedProduct = productoService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productoService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
