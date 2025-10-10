package com.erp.p03.controllers;

import java.util.List;

import com.erp.p03.controllers.dto.ProductoConCategoriaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.services.ProductoService;

@RestController
@RequestMapping("api/productos")
@CrossOrigin("*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductoEntity>> findAll() {
        List<ProductoEntity> productos = productoService.findAll();
        return ResponseEntity.ok(productos);
    }
    @GetMapping("/all-con-categoria")
    public ResponseEntity<List<ProductoConCategoriaDTO>> findAllConCategoria() {
        List<ProductoConCategoriaDTO> productos = productoService.obtenerProductosConCategoria();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoEntity> findById(@PathVariable Integer id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/save")
    public ResponseEntity<ProductoEntity> save(@RequestBody ProductoEntity producto) {
        try {
            ProductoEntity savedProducto = productoService.save(producto);
            return ResponseEntity.ok(savedProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoEntity> update(@PathVariable Integer id, @RequestBody ProductoEntity producto) {
        if (!productoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            producto.setIdProducto(id);
            ProductoEntity updatedProducto = productoService.save(producto);
            return ResponseEntity.ok(updatedProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!productoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            productoService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProductoEntity>> findActivos() {
        List<ProductoEntity> productos = productoService.findByEstado(true);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoEntity>> findByCategoria(@PathVariable Integer categoriaId) {
        List<ProductoEntity> productos = productoService.findByCategoriaId(categoriaId);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoEntity>> buscarPorNombre(@RequestParam String nombre) {
        List<ProductoEntity> productos = productoService.findByNombreContaining(nombre);
        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{id}/agregar-stock")
    public ResponseEntity<ProductoEntity> agregarStock(@PathVariable Integer id, @RequestParam int cantidad) {
        try {
            ProductoEntity productoActualizado = productoService.agregarStock(id, cantidad);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/quitar-stock")
    public ResponseEntity<ProductoEntity> quitarStock(@PathVariable Integer id, @RequestParam int cantidad) {
        try {
            ProductoEntity productoActualizado = productoService.quitarStock(id, cantidad);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint para obtener productos con stock bajo (<= 5)
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoEntity>> getProductosStockBajo() {
        List<ProductoEntity> productosStockBajo = productoService.findProductosStockBajo();
        return ResponseEntity.ok(productosStockBajo);
    }

    // Ordenar productos de A a Z
    @GetMapping("/orden/nombre")
    public ResponseEntity<List<ProductoEntity>> ordenarPorNombreAZ() {
        List<ProductoEntity> productos = productoService.findAllOrderByNombreAsc();
        return ResponseEntity.ok(productos);
    }

    // Ordenar productos por peso descendente
    @GetMapping("/orden/peso-desc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorPesoDesc() {
        List<ProductoEntity> productos = productoService.findAllOrderByPesoDesc();
        return ResponseEntity.ok(productos);
    }

    // Ordenar productos por stock descendente
    @GetMapping("/orden/stock-desc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorStockDesc() {
        List<ProductoEntity> productos = productoService.findAllOrderByStockDesc();
        return ResponseEntity.ok(productos);
    }
}
