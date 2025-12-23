package com.erp.p03.services;

import com.erp.p03.controllers.dto.ProductoLoteVencimientoDTO;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.LoteRepository;
import com.erp.p03.repositories.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public LoteService(LoteRepository loteRepository, ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
    }

    // ✅ Crear nuevo lote
    @Transactional
    public LoteEntity crearLote(LoteEntity lote) {
        if (lote == null) throw new IllegalArgumentException("Lote nulo");
        if (lote.getProducto() == null || lote.getProducto().getIdProducto() == 0)
            throw new IllegalArgumentException("Producto inválido en lote");

        ProductoEntity producto = productoRepository.findById(lote.getProducto().getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        lote.setProducto(producto);
        if (lote.getEstado() == null) lote.setEstado(Boolean.TRUE);
        if (lote.getFechaIngreso() == null) lote.setFechaIngreso(LocalDate.now());
        System.out.println("Guardando lote: ");
        LoteEntity saved = loteRepository.save(lote);
        System.out.println("Lote Guardado: ");

        // Recalcular stock total del producto
        int total = producto.getLotes() == null ? 0 : producto.getLotes().stream()
                .filter(Objects::nonNull)
                .filter(l -> Boolean.TRUE.equals(l.getEstado()) && l.getCantidad() != null)
                .mapToInt(LoteEntity::getCantidad)
                .sum();

        producto.setStock(total);
        productoRepository.save(producto);
        System.out.println("producto final: ");


        return saved;
    }

    // ✅ Listar lotes por producto
    public List<LoteEntity> listarLotesPorProducto(Integer productoId) {
        return loteRepository.findByProducto_IdProductoOrderByFechaVencimientoAsc(productoId);
    }

    // ✅ Buscar lotes próximos a vencer
    public List<LoteEntity> findLotesVencimientoProximo(int dias) {
        LocalDate desde = LocalDate.now();
        LocalDate hasta = desde.plusDays(Math.max(dias, 0));
        return loteRepository.findByFechaVencimientoBetween(desde, hasta);
    }

    // ✅ Devolver DTOs de lotes próximos a vencer
    public List<ProductoLoteVencimientoDTO> findLotesVencimientoProximoDTO(int dias) {
        LocalDate hoy = LocalDate.now();
        return findLotesVencimientoProximo(dias).stream()
                .filter(Objects::nonNull)
                .map(l -> {
                    long diasRest = ChronoUnit.DAYS.between(hoy,
                            l.getFechaVencimiento() == null ? hoy : l.getFechaVencimiento());
                    ProductoLoteVencimientoDTO dto = new ProductoLoteVencimientoDTO();
                    dto.setIdLote(l.getIdLote());
                    dto.setProductoId(l.getProducto() != null ? l.getProducto().getIdProducto() : null);
                    dto.setNombreProducto(l.getProducto() != null ? l.getProducto().getNombre() : null);
                    dto.setCantidad(l.getCantidad());
                    dto.setFechaVencimiento(l.getFechaVencimiento());
                    dto.setDiasRestantes((int) diasRest);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ✅ Actualizar fecha de vencimiento
    @Transactional
    public LoteEntity updateFechaVencimiento(int idLote, LocalDate nuevaFecha) {
        LoteEntity lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        lote.setFechaVencimiento(nuevaFecha);
        return loteRepository.save(lote);
    }

    // ✅ Actualizar estado (activo/inactivo)
    @Transactional
    public LoteEntity updateEstadoLote(int idLote, Boolean nuevoEstado) {
        LoteEntity lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        lote.setEstado(nuevoEstado);
        return loteRepository.save(lote);
    }

    // ✅ Nuevo: actualizar cantidad
    @Transactional
    public LoteEntity updateCantidadLote(int idLote, Integer nuevaCantidad) {
        LoteEntity lote = loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
        lote.setCantidad(nuevaCantidad);
        return loteRepository.save(lote);
    }

    // Buscar por id
    public LoteEntity findById(Integer idLote) {
        return loteRepository.findById(idLote)
                .orElseThrow(() -> new IllegalArgumentException("Lote no encontrado"));
    }

    // Buscar por codigo de lote
    public LoteEntity findByCodigoLote(String codigoLote) {
        LoteEntity lote = loteRepository.findByCodigoLote(codigoLote);
        if (lote == null) throw new IllegalArgumentException("Lote no encontrado por codigo");
        return lote;
    }

    // Buscar lotes por fecha de vencimiento entre dos fechas (inclusive)
    public List<LoteEntity> findLotesByFechaVencimientoBetween(LocalDate desde, LocalDate hasta) {
        return loteRepository.findByFechaVencimientoBetween(desde, hasta);
    }

    public List<String>findAllCodigosLote(){
        List<LoteEntity> lotes = loteRepository.findAll();
        List<String> codigos = new ArrayList<>();
        for (LoteEntity lote : lotes) {
            if (lote.getCodigoLote() != null) {
                codigos.add(lote.getCodigoLote());
            }
        }
        return codigos;
    }

}
