package com.erp.p03.services;

import com.erp.p03.entities.*;
import com.erp.p03.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DevolucionService {

    @Autowired
    private DevolucionRepository devolucionRepository;

    @Autowired
    private DetalleDevolucionRepository detalleDevolucionRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private LoteService loteService;

    @Autowired
    private ProductoService productoService;

    /**
     * Process a full sale return - returns all products from the sale
     */
    @Transactional
    public DevolucionEntity procesarDevolucionCompleta(Integer ventaId, String motivo, Integer usuarioId) {
        // 1. Validate sale exists
        VentaEntity venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        // 2. Check if sale hasn't been already fully returned
        List<DevolucionEntity> devolucionesExistentes = devolucionRepository.findByVentaId(ventaId);
        boolean yaDevueltaCompleta = devolucionesExistentes.stream()
                .anyMatch(d -> "COMPLETA".equals(d.getTipo()));

        if (yaDevueltaCompleta) {
            throw new RuntimeException("Esta venta ya fue devuelta completamente");
        }

        // 3. Get all sale details
        List<DetalleVentaEntity> detallesVenta = detalleVentaRepository.findByVentaId(ventaId);

        if (detallesVenta.isEmpty()) {
            throw new RuntimeException("No se encontraron productos en esta venta");
        }

        // 4. Create return record
        DevolucionEntity devolucion = new DevolucionEntity();
        devolucion.setVentaId(ventaId);
        devolucion.setFechaDevolucion(LocalDateTime.now());
        devolucion.setMotivo(motivo);
        devolucion.setMontoDevuelto(venta.getTotal());
        devolucion.setUsuarioId(usuarioId);
        devolucion.setTipo("COMPLETA");

        devolucion = devolucionRepository.save(devolucion);

        // 5. Process each product return
        List<DetalleDevolucionEntity> detallesDevolucion = new ArrayList<>();

        for (DetalleVentaEntity detalleVenta : detallesVenta) {
            DetalleDevolucionEntity detalleDevolucion = procesarDevolucionDetalle(
                    devolucion.getIdDevolucion(),
                    detalleVenta,
                    detalleVenta.getCantidad() // Full quantity
            );
            detallesDevolucion.add(detalleDevolucion);
        }

        devolucion.setDetalles(detallesDevolucion);
        return devolucion;
    }

    /**
     * Process a partial sale return - returns selected products/quantities
     */
    @Transactional
    public DevolucionEntity procesarDevolucionParcial(Integer ventaId, List<ItemDevolucion> items,
            String motivo, Integer usuarioId) {
        // 1. Validate sale exists
        VentaEntity venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un producto para devolver");
        }

        // 2. Calculate total refund amount
        int montoTotal = 0;
        for (ItemDevolucion item : items) {
            DetalleVentaEntity detalleVenta = detalleVentaRepository.findById(item.getDetalleVentaId())
                    .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado"));

            // Validate quantity
            if (item.getCantidad() > detalleVenta.getCantidad()) {
                throw new RuntimeException("No se puede devolver más cantidad de la vendida");
            }

            // Calculate proportional refund
            int montoItem = (detalleVenta.getPrecioUnitario() * item.getCantidad());
            montoTotal += montoItem;
        }

        // 3. Create return record
        DevolucionEntity devolucion = new DevolucionEntity();
        devolucion.setVentaId(ventaId);
        devolucion.setFechaDevolucion(LocalDateTime.now());
        devolucion.setMotivo(motivo);
        devolucion.setMontoDevuelto(montoTotal);
        devolucion.setUsuarioId(usuarioId);
        devolucion.setTipo("PARCIAL");

        devolucion = devolucionRepository.save(devolucion);

        // 4. Process each item return
        List<DetalleDevolucionEntity> detallesDevolucion = new ArrayList<>();

        for (ItemDevolucion item : items) {
            DetalleVentaEntity detalleVenta = detalleVentaRepository.findById(item.getDetalleVentaId()).get();

            DetalleDevolucionEntity detalleDevolucion = procesarDevolucionDetalle(
                    devolucion.getIdDevolucion(),
                    detalleVenta,
                    item.getCantidad());
            detallesDevolucion.add(detalleDevolucion);
        }

        devolucion.setDetalles(detallesDevolucion);
        return devolucion;
    }

    /**
     * Process return of a single item detail - restores stock to lot
     */
    private DetalleDevolucionEntity procesarDevolucionDetalle(Integer devolucionId,
            DetalleVentaEntity detalleVenta,
            Integer cantidadDevuelta) {
        // 1. Restore stock to original lot
        if (detalleVenta.getIdLote() != null) {
            LoteEntity lote = loteService.findById(detalleVenta.getIdLote());

            if (lote != null) {
                // Calculate new quantity
                Integer nuevaCantidad = lote.getCantidad() + cantidadDevuelta;

                // Update lot quantity
                loteService.updateCantidadLote(lote.getIdLote(), nuevaCantidad);

                // Reactivate lot if it was inactive
                if (!lote.getEstado()) {
                    loteService.updateEstadoLote(lote.getIdLote(), true);
                }
            } else {
                // Lot doesn't exist - this shouldn't happen but handle it
                throw new RuntimeException("Lote original no encontrado para la devolución");
            }
        }

        // 2. Update product stock
        Optional<ProductoEntity> productoOpt = productoService.findById(detalleVenta.getProductoId());
        if (productoOpt.isPresent()) {
            ProductoEntity producto = productoOpt.get();
            producto.setStock(producto.getStock() + cantidadDevuelta);
            productoService.save(producto);
        }

        // 3. Update sale detail - reduce quantity or delete if fully returned
        Integer cantidadRestante = detalleVenta.getCantidad() - cantidadDevuelta;

        if (cantidadRestante <= 0) {
            // Delete detail if all quantity returned
            detalleVentaRepository.deleteById(detalleVenta.getIdDetalleVenta());
        } else {
            // Update quantity if partial return
            detalleVenta.setCantidad(cantidadRestante);
            detalleVenta.setSubtotal(detalleVenta.getPrecioUnitario() * cantidadRestante);
            detalleVentaRepository.save(detalleVenta);
        }

        // 4. Update sale total
        Integer montoARestar = detalleVenta.getPrecioUnitario() * cantidadDevuelta;
        VentaEntity venta = ventaRepository.findById(detalleVenta.getVentaId()).orElse(null);
        if (venta != null) {
            venta.setTotal(venta.getTotal() - montoARestar);
            ventaRepository.save(venta);
        }

        // 5. Create detail return record
        DetalleDevolucionEntity detalleDevolucion = new DetalleDevolucionEntity();
        detalleDevolucion.setDevolucionId(devolucionId);
        detalleDevolucion.setDetalleVentaId(detalleVenta.getIdDetalleVenta());
        detalleDevolucion.setProductoId(detalleVenta.getProductoId());
        detalleDevolucion.setCantidadDevuelta(cantidadDevuelta);
        detalleDevolucion.setIdLote(detalleVenta.getIdLote());
        detalleDevolucion.setMontoDevuelto(detalleVenta.getPrecioUnitario() * cantidadDevuelta);

        return detalleDevolucionRepository.save(detalleDevolucion);
    }

    /**
     * Get all returns
     */
    public List<DevolucionEntity> listarDevoluciones() {
        List<DevolucionEntity> devoluciones = devolucionRepository.findAllByOrderByFechaDevolucionDesc();

        // Populate details for each return
        for (DevolucionEntity devolucion : devoluciones) {
            List<DetalleDevolucionEntity> detalles = detalleDevolucionRepository
                    .findByDevolucionId(devolucion.getIdDevolucion());

            // Populate product names
            for (DetalleDevolucionEntity detalle : detalles) {
                productoService.findById(detalle.getProductoId()).ifPresent(producto -> {
                    detalle.setProductoNombre(producto.getNombre());
                });

                if (detalle.getIdLote() != null) {
                    LoteEntity lote = loteService.findById(detalle.getIdLote());
                    if (lote != null) {
                        detalle.setCodigoLote(lote.getCodigoLote());
                    }
                }
            }

            devolucion.setDetalles(detalles);
        }

        return devoluciones;
    }

    /**
     * Get return by ID
     */
    public DevolucionEntity findById(Integer id) {
        DevolucionEntity devolucion = devolucionRepository.findById(id).orElse(null);

        if (devolucion != null) {
            List<DetalleDevolucionEntity> detalles = detalleDevolucionRepository.findByDevolucionId(id);

            // Populate product names
            for (DetalleDevolucionEntity detalle : detalles) {
                productoService.findById(detalle.getProductoId()).ifPresent(producto -> {
                    detalle.setProductoNombre(producto.getNombre());
                });

                if (detalle.getIdLote() != null) {
                    LoteEntity lote = loteService.findById(detalle.getIdLote());
                    if (lote != null) {
                        detalle.setCodigoLote(lote.getCodigoLote());
                    }
                }
            }

            devolucion.setDetalles(detalles);
        }

        return devolucion;
    }

    /**
     * Helper class for partial return items
     */
    public static class ItemDevolucion {
        private Integer detalleVentaId;
        private Integer cantidad;

        public ItemDevolucion() {
        }

        public ItemDevolucion(Integer detalleVentaId, Integer cantidad) {
            this.detalleVentaId = detalleVentaId;
            this.cantidad = cantidad;
        }

        public Integer getDetalleVentaId() {
            return detalleVentaId;
        }

        public void setDetalleVentaId(Integer detalleVentaId) {
            this.detalleVentaId = detalleVentaId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }
}
