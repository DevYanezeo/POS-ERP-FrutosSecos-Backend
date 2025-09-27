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
}
