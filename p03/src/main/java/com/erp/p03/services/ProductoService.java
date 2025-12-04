package com.erp.p03.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.erp.p03.controllers.dto.LoteDTO;
import com.erp.p03.controllers.dto.ParcialDTO;
import com.erp.p03.controllers.dto.ProductoConCategoriaDTO;
import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // ======================== CRUD BÁSICO =========================
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

    // ======================== FILTROS =========================

    // Filtrar productos activos o inactivos
    public List<ProductoEntity> findByEstado(Boolean estado) {
        return productoRepository.findByEstado(estado);
    }

    // Filtrar productos por categoría
    public List<ProductoEntity> findByCategoriaId(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    // Buscar por nombre parcial (case-insensitive)
    public List<ProductoEntity> findByNombreContaining(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Filtrar productos por precio entre un mínimo y un máximo
    public List<ProductoEntity> findByPrecioBetween(BigDecimal min, BigDecimal max) {
        return productoRepository.findByPrecioBetween(min, max);
    }

    // ======================== ORDENAMIENTOS =========================

    // Ordenar productos por nombre (A → Z)
    public List<ProductoEntity> findAllOrderByNombreAsc() {
        return productoRepository.findAllByOrderByNombreAsc();
    }

    // Ordenar productos por nombre (Z → A)
    public List<ProductoEntity> findAllOrderByNombreDesc() {
        return productoRepository.findAllByOrderByNombreDesc();
    }

    // Ordenar productos por stock ascendente
    public List<ProductoEntity> findAllOrderByStockAsc() {
        return productoRepository.findAllByOrderByStockAsc();
    }

    // Ordenar productos por stock descendente
    public List<ProductoEntity> findAllOrderByStockDesc() {
        return productoRepository.findAllByOrderByStockDesc();
    }

    // Ordenar productos por precio ascendente
    public List<ProductoEntity> findAllOrderByPrecioAsc() {
        return productoRepository.findAllByOrderByPrecioAsc();
    }

    // Ordenar productos por precio descendente
    public List<ProductoEntity> findAllOrderByPrecioDesc() {
        return productoRepository.findAllByOrderByPrecioDesc();
    }

    // ======================== LOTES Y STOCK =========================

    // Calcula el stock total a partir de los lotes activos
    private int stockDesdeLotes(ProductoEntity producto) {
        if (producto == null || producto.getLotes() == null) return 0;
        return producto.getLotes().stream()
                .filter(Objects::nonNull)
                .filter(l -> Boolean.TRUE.equals(l.getEstado()) && l.getCantidad() != null)
                .mapToInt(LoteEntity::getCantidad)
                .sum();
    }

    // Agregar stock a un producto en un lote específico
    @Transactional
    public ProductoEntity agregarStock(int productoId, int loteId, int cantidad) {
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        List<LoteEntity> lotes = Optional.ofNullable(producto.getLotes())
                .orElseThrow(() -> new RuntimeException("Producto no tiene lotes"));

        boolean encontrado = false;
        for (LoteEntity l : lotes) {
            if (Objects.equals(l.getIdLote(), loteId)) {
                int cantidadActual = Optional.ofNullable(l.getCantidad()).orElse(0);
                l.setCantidad(cantidadActual + cantidad);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) throw new RuntimeException("Lote no encontrado");

        producto.setLotes(lotes);
        int totalStock = stockDesdeLotes(producto);
        producto.setStock(totalStock);

        return productoRepository.save(producto);
    }

    // Quitar stock a un producto en un lote específico
    @Transactional
    public ProductoEntity quitarStock(int productoId, int loteId, int cantidad) {
        ProductoEntity producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        List<LoteEntity> lotes = Optional.ofNullable(producto.getLotes())
                .orElseThrow(() -> new RuntimeException("Producto no tiene lotes"));

        boolean encontrado = false;
        for (LoteEntity l : lotes) {
            if (Objects.equals(l.getIdLote(), loteId)) {
                int cantidadActual = Optional.ofNullable(l.getCantidad()).orElse(0);
                l.setCantidad(cantidadActual - cantidad);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) throw new RuntimeException("Lote no encontrado");

        producto.setLotes(lotes);
        int totalStock = stockDesdeLotes(producto);
        producto.setStock(totalStock);

        return productoRepository.save(producto);
    }

    // ======================== REPORTES / VISTAS =========================

    // Devuelve productos cuyo stock sea menor o igual a 5 (alerta de stock bajo)
    public List<ProductoEntity> findProductosStockBajo() {
        return productoRepository.findAll().stream()
                .filter(producto -> {
                    int stock = Optional.ofNullable(producto.getStock())
                            .orElse(stockDesdeLotes(producto));
                    return stock <= 5;
                })
                .toList();
    }

    // Devuelve productos junto con su categoría y lotes en un DTO
    public List<ProductoConCategoriaDTO> obtenerProductosConCategoria() {
        List<ProductoEntity> productos = productoRepository.findAllWithLotes();
        List<CategoriaEntity> categorias = categoriaRepository.findAll();

        return productos.stream().map(producto -> {
            ProductoConCategoriaDTO dto = new ProductoConCategoriaDTO();
            dto.setIdProducto(producto.getIdProducto());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setImagen(producto.getImagen());
            dto.setPrecio(producto.getPrecio());

            // Calcular stock total desde campo o lotes
            int stockCalc = Optional.ofNullable(producto.getStock())
                    .orElse(stockDesdeLotes(producto));
            dto.setStock(stockCalc);

            dto.setUnidad(producto.getUnidad());
            dto.setEstado(producto.getEstado());

            // Buscar la fecha de vencimiento más próxima entre lotes activos
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

            // Buscar nombre de la categoría
            if (producto.getCategoriaId() != null) {
                categorias.stream()
                        .filter(categoria -> Objects.equals(categoria.getIdCategoria(), producto.getCategoriaId()))
                        .findFirst()
                        .ifPresent(categoria -> dto.setNombreCategoria(categoria.getNombre()));
            } else {
                dto.setNombreCategoria(null);
            }

            // Mapear los lotes a DTO
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


    // update parcial
    @Transactional
    public ProductoEntity parcialSave(Integer id, ParcialDTO dto) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (dto.getNombre() != null) producto.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) producto.setDescripcion(dto.getDescripcion());
        if (dto.getUnidad() != null) producto.setUnidad(dto.getUnidad());
        if (dto.getEstado() != null) producto.setEstado(dto.getEstado());
        if (dto.getPrecio() != null) producto.setPrecio(dto.getPrecio());

        return productoRepository.save(producto);
    }
}
