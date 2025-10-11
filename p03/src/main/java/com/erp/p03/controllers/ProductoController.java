package com.erp.p03.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.erp.p03.controllers.dto.ProductoConCategoriaDTO;
import com.erp.p03.services.FileStorageService;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MultipartFile;

import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.services.ProductoService;

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

    // Endpoint para agregar stock de un lote específico de un producto
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
    // Endpoint para quitar stock de un lote específico de un producto
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
        return ResponseEntity.ok(productos); //quickfix
    }
    // Filtrar productos por peso entre min y max de gramos, ingresados desde la vista
    @GetMapping("/peso")
    public ResponseEntity<List<ProductoEntity>> findByPesoBetween(@RequestParam int min, @RequestParam int max) {
        if (min >= max) {
            // Validación: no permitir mínimo mayor o igual al máximo
            return ResponseEntity.badRequest().body(null);
        }
        List<ProductoEntity> productos = productoService.findByPesoBetween(min, max);
        return ResponseEntity.ok(productos);
    }

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
                    String oldFileName = producto.getImagen().substring(producto.getImagen().lastIndexOf("/") + 1);
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
