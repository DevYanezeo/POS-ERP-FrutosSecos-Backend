package com.erp.p03.controllers;

import java.math.BigDecimal;
import java.util.List;

import com.erp.p03.controllers.dto.ParcialDTO;
import com.erp.p03.controllers.dto.ProductoConCategoriaDTO;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.services.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/productos")
@CrossOrigin("*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // ====================== CRUD BÁSICO ======================

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

    // update parcial (patch) para modificar solo el estado
    @PutMapping("/{id}/parcial")
    public ResponseEntity<ProductoEntity> update(@PathVariable Integer id, @RequestBody ParcialDTO productoUpdated) {
        if (!productoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            ProductoEntity updatedProducto = productoService.parcialSave(id, productoUpdated);
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

    // ====================== FILTROS ======================

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

    // 🔹 Filtrar productos por rango de precios (CLP)
    @GetMapping("/precio")
    public ResponseEntity<List<ProductoEntity>> findByPrecioBetween(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        if (min.compareTo(max) >= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        List<ProductoEntity> productos = productoService.findByPrecioBetween(min, max);
        return ResponseEntity.ok(productos);
    }

    // ====================== STOCK ======================

    // Productos con stock bajo (<= X)
    @GetMapping("/stock-bajo/{minStock}")
    public ResponseEntity<List<ProductoEntity>> getProductosStockBajo(@PathVariable Integer minStock) {
        List<ProductoEntity> productosStockBajo = productoService.findProductosStockBajo(minStock);
        return ResponseEntity.ok(productosStockBajo);
    }

    // Agregar stock a un lote
    @PutMapping("/{productoId}/lotes/{loteId}/agregar-stock")
    public ResponseEntity<ProductoEntity> agregarStock(
            @PathVariable("productoId") Integer productoId,
            @PathVariable("loteId") Integer loteId,
            @RequestParam int cantidad) {
        try {
            ProductoEntity productoActualizado = productoService.agregarStock(productoId, loteId, cantidad);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Quitar stock de un lote
    @PutMapping("/{productoId}/lotes/{loteId}/quitar-stock")
    public ResponseEntity<ProductoEntity> quitarStock(
            @PathVariable("productoId") Integer productoId,
            @PathVariable("loteId") Integer loteId,
            @RequestParam int cantidad) {
        try {
            ProductoEntity productoActualizado = productoService.quitarStock(productoId, loteId, cantidad);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ====================== ORDENAMIENTOS ======================

    // Nombre A → Z
    @GetMapping("/orden/nombre")
    public ResponseEntity<List<ProductoEntity>> ordenarPorNombreAZ() {
        List<ProductoEntity> productos = productoService.findAllOrderByNombreAsc();
        return ResponseEntity.ok(productos);
    }

    // Nombre Z → A
    @GetMapping("/orden/nombre-desc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorNombreZA() {
        List<ProductoEntity> productos = productoService.findAllOrderByNombreDesc();
        return ResponseEntity.ok(productos);
    }

    // Stock ascendente
    @GetMapping("/orden/stock-asc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorStockAsc() {
        List<ProductoEntity> productos = productoService.findAllOrderByStockAsc();
        return ResponseEntity.ok(productos);
    }

    // Stock descendente
    @GetMapping("/orden/stock-desc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorStockDesc() {
        List<ProductoEntity> productos = productoService.findAllOrderByStockDesc();
        return ResponseEntity.ok(productos);
    }

    // Precio ascendente
    @GetMapping("/orden/precio-asc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorPrecioAsc() {
        List<ProductoEntity> productos = productoService.findAllOrderByPrecioAsc();
        return ResponseEntity.ok(productos);
    }

    // Precio descendente
    @GetMapping("/orden/precio-desc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorPrecioDesc() {
        List<ProductoEntity> productos = productoService.findAllOrderByPrecioDesc();
        return ResponseEntity.ok(productos);
    }

}
