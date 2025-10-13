package com.erp.p03.controllers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.erp.p03.controllers.dto.ProductoConCategoriaDTO;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.services.FileStorageService;
import com.erp.p03.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/productos")
@CrossOrigin("*")
public class ProductoController {

    private final ProductoService productoService;
    private final FileStorageService fileStorageService;

    public ProductoController(ProductoService productoService, FileStorageService fileStorageService) {
        this.productoService = productoService;
        this.fileStorageService = fileStorageService;
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

    // Filtrar productos por peso entre min y max (gramos)
    @GetMapping("/peso")
    public ResponseEntity<List<ProductoEntity>> findByPesoBetween(
            @RequestParam int min, @RequestParam int max) {
        if (min >= max) {
            return ResponseEntity.badRequest().body(null);
        }
        List<ProductoEntity> productos = productoService.findByPesoBetween(min, max);
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

    // Productos con stock bajo (<= 5)
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoEntity>> getProductosStockBajo() {
        List<ProductoEntity> productosStockBajo = productoService.findProductosStockBajo();
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

    // Peso ascendente
    @GetMapping("/orden/peso-asc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorPesoAsc() {
        List<ProductoEntity> productos = productoService.findAllOrderByPesoAsc();
        return ResponseEntity.ok(productos);
    }

    // Peso descendente
    @GetMapping("/orden/peso-desc")
    public ResponseEntity<List<ProductoEntity>> ordenarPorPesoDesc() {
        List<ProductoEntity> productos = productoService.findAllOrderByPesoDesc();
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

    // ====================== IMÁGENES ======================

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String nombreArchivo = fileStorageService.guardarImagen(file);
            String imageUrl = "/uploads/productos/" + nombreArchivo;

            Map<String, String> response = new HashMap<>();
            response.put("fileName", nombreArchivo);
            response.put("imageUrl", imageUrl);
            response.put("message", "Imagen subida exitosamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/imagen")
    public ResponseEntity<?> updateProductoImagen(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {
        try {
            ProductoEntity producto = productoService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
                try {
                    String oldFileName = producto.getImagen()
                            .substring(producto.getImagen().lastIndexOf("/") + 1);
                    fileStorageService.eliminarImagen(oldFileName);
                } catch (Exception e) {
                    System.err.println("No se pudo eliminar la imagen anterior: " + e.getMessage());
                }
            }

            String nombreArchivo = fileStorageService.guardarImagen(file);
            String imageUrl = "/uploads/productos/" + nombreArchivo;

            producto.setImagen(imageUrl);
            ProductoEntity updatedProducto = productoService.save(producto);

            return ResponseEntity.ok(updatedProducto);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
