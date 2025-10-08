package com.erp.p03.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.erp.p03.controllers.dto.LoteDTO;
import com.erp.p03.controllers.dto.ProductoConCategoriaDTO;
import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
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

    // Helper: calcula el stock total a partir de los lotes activos
    private int stockDesdeLotes(ProductoEntity producto) {
        if (producto == null || producto.getLotes() == null) return 0;
        return producto.getLotes().stream()
                .filter(Objects::nonNull)
                .filter(l -> Boolean.TRUE.equals(l.getEstado()) && l.getCantidad() != null)
                .mapToInt(LoteEntity::getCantidad)
                .sum();
    }

    // Agrega stock al producto con el id dado y retorna el producto actualizado
    public ProductoEntity agregarStock(int productoId, int cantidad) {
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        int actual = Optional.ofNullable(producto.getStock()).orElse(stockDesdeLotes(producto));
        producto.setStock(actual + cantidad);
        return productoRepository.save(producto);
    }

    // Quita stock al producto con el id dado y retorna el producto actualizado
    public ProductoEntity quitarStock(int productoId, int cantidad) {
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        int actual = Optional.ofNullable(producto.getStock()).orElse(stockDesdeLotes(producto));
        int nuevaCantidad = actual - cantidad;
        if (nuevaCantidad < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        producto.setStock(nuevaCantidad);
        return productoRepository.save(producto);
    }

    // Devuelve la lista de productos cuyo stock es menor o igual a 5
    public List<ProductoEntity> findProductosStockBajo() {
        return productoRepository.findAll().stream()
                .filter(producto -> {
                    int stock = Optional.ofNullable(producto.getStock()).orElse(stockDesdeLotes(producto));
                    return stock <= 5;
                })
                .toList();
    }

    // Devuelve la lista de productos con el nombre de la categoría a la que pertenece
    public List<ProductoConCategoriaDTO> obtenerProductosConCategoria() {
        // usar findAllWithLotes para traer lotes en la misma consulta y evitar LazyInitialization
        List<ProductoEntity> productos = productoRepository.findAllWithLotes();
        List<CategoriaEntity> categorias = categoriaRepository.findAll();
        return productos.stream().map(producto -> {
            ProductoConCategoriaDTO dto = new ProductoConCategoriaDTO();
            dto.setIdProducto(producto.getIdProducto());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setImagen(producto.getImagen());
            dto.setPrecio(producto.getPrecio());
            // Calculamos stock: preferimos el campo stock si no es nulo, sino sumamos lotes activos
            int stockCalc = Optional.ofNullable(producto.getStock()).orElse(stockDesdeLotes(producto));
            dto.setStock(stockCalc);
            dto.setUnidad(producto.getUnidad());
            dto.setEstado(producto.getEstado());
            // Fecha de vencimiento mínima entre lotes activos
            Optional<LocalDate> fechaVenc = Optional.empty();
            if (producto.getLotes() != null) {
                fechaVenc = producto.getLotes().stream()
                        .filter(Objects::nonNull)
                        .filter(l -> l.getFechaVencimiento() != null && Boolean.TRUE.equals(l.getEstado()))
                        .map(LoteEntity::getFechaVencimiento)
                        .min(Comparator.naturalOrder());
            }
            dto.setFechaVencimiento(fechaVenc.orElse(null));
            dto.setCodigo(producto.getCodigo());
            dto.setCategoriaId(producto.getCategoriaId());
            // Validar que categoriaId no sea null antes de buscar
            if (producto.getCategoriaId() != null) {
                categorias.stream()
                        .filter(categoria -> Objects.equals(categoria.getIdCategoria(), producto.getCategoriaId()))
                        .findFirst()
                        .ifPresent(categoria -> dto.setNombreCategoria(categoria.getNombre()));
            } else {
                dto.setNombreCategoria(null);
            }

            // Mapear lotes a LoteDTO
            if (producto.getLotes() != null) {
                List<LoteDTO> lotes = producto.getLotes().stream()
                        .filter(Objects::nonNull)
                        .map(l -> new LoteDTO(
                                l.getIdLote(),
                                l.getCantidad(),
                                l.getFechaVencimiento(),
                                l.getFechaIngreso(),
                                l.getCodigoLote(),
                                l.getEstado()
                        ))
                        .collect(Collectors.toList());
                dto.setLotes(lotes);
            } else {
                dto.setLotes(null);
            }

            return dto;
        }).toList();
    }

}
