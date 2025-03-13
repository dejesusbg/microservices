package org.example.producto.service;

import org.example.producto.entity.Producto;
import org.example.producto.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> getAllProducts() {
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductById(String id) {
        return productoRepository.findById(id);
    }

    public Producto createProduct(Producto product) {
        return productoRepository.save(product);
    }

    public Producto updateProduct(String id, Producto productDetails) {
        return productoRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setPrice(productDetails.getPrice());
                    return productoRepository.save(product);
                }).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    public void deleteProduct(String id) {
        productoRepository.deleteById(id);
    }
}
