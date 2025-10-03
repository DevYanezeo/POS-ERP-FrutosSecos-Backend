package com.erp.p03.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<ProductoEntity> findAll() {
        return productoRepository.findAll();
    }

    public Optional<ProductoEntity> findById(Integer id) {
        return productoRepository.findById(id);
    }

    public ProductoEntity save(ProductoEntity producto) {
        return productoRepository.save(producto);
    }

    public void deleteById(Integer id) {
        productoRepository.deleteById(id);
    }

    public boolean existsById(Integer id) {
        return productoRepository.existsById(id);
    }

    public List<ProductoEntity> findByEstado(Boolean estado) {
        return productoRepository.findByEstado(estado);
    }

    public List<ProductoEntity> findByCategoriaId(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<ProductoEntity> findByNombreContaining(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Agrega stock al producto con el id dado y retorna el producto actualizado
    public ProductoEntity agregarStock(int productoId, int cantidad) {
        // Busca el producto por id, lanza error tipo Excepción si no existe
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setStock(producto.getStock() + cantidad);
        return productoRepository.save(producto);
    }

    // Quita stock al producto con el id dado y retorna el producto actualizado
    public ProductoEntity quitarStock(int productoId, int cantidad) {
        // Busca el producto por id, lanza error tipo Excepción si no existe
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        int nuevaCantidad = producto.getStock() - cantidad;
        // Si el stock resultante es negativo, lanza excepción
        if (nuevaCantidad < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        producto.setStock(nuevaCantidad);
        return productoRepository.save(producto);
    }

    // Devuelve la lista de productos cuyo stock es menor o igual a 5
    public List<ProductoEntity> findProductosStockBajo() {
        // Busca productos con stock <= 5
        return productoRepository.findAll().stream()
                .filter(producto -> producto.getStock() <= 5)
                .toList();
    }

}
