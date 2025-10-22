package com.erp.p03.services;

import java.time.LocalDateTime;
import java.util.List;
import com.erp.p03.entities.LoteEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.p03.controllers.dto.DetalleVentaRequest;
import com.erp.p03.controllers.dto.VentaRequest;
import com.erp.p03.entities.DetalleVentaEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.entities.VentaEntity;
import com.erp.p03.repositories.DetalleVentaRepository;
import com.erp.p03.repositories.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoStockService movimientoStockService;
    private final ProductoService productoService;
    private final LoteService loteService;

    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        MovimientoStockService movimientoStockService,
                        ProductoService productoService,
                        LoteService loteService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.movimientoStockService = movimientoStockService;
        this.productoService = productoService;
        this.loteService = loteService;
    }

    public List<VentaEntity> findAll() {
        return ventaRepository.findAll();
    }

    public VentaEntity findById(Integer id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public VentaEntity save(VentaEntity venta) {
        return ventaRepository.save(venta);
    }

    public void deleteById(Integer id) {
        ventaRepository.deleteById(id);
    }

    /**
     * Flujo transaccional de confirmación de venta:
     * - Crea la entidad Venta
     * - Crea los DetalleVenta
     * - Para cada detalle crea un MovimientoStock (SALIDA)
     * - Actualiza la cantidad del lote correspondiente y recalcula el stock del producto
     */
    @Transactional
    public VentaEntity confirmarVenta(VentaRequest request) {
        // 1) Crear Venta
        VentaEntity venta = new VentaEntity();
        venta.setFecha(LocalDateTime.now());
        venta.setUsuarioId(request.getUsuarioId());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setSubtotal(request.getSubtotal());
        venta.setIva(request.getIva());
        venta.setTotal(request.getTotal());
        VentaEntity savedVenta = ventaRepository.save(venta);

        // 2) Crear Detalles y movimientos por cada detalle
        List<DetalleVentaEntity> detalles = request.getDetalles().stream().map(d -> {
            DetalleVentaEntity dv = new DetalleVentaEntity();
            dv.setVentaId(savedVenta.getIdVenta());
            dv.setProductoId(d.getProductoId());
            dv.setCantidad(d.getCantidad());
            dv.setPrecioUnitario(d.getPrecioUnitario());
            dv.setSubtotal(d.getPrecioUnitario() * d.getCantidad());
            return dv;
        }).toList();

        // Guardar detalles
        detalleVentaRepository.saveAll(detalles);

        // Para cada detalle: registrar movimiento SALIDA, actualizar lote y producto
        for (int i = 0; i < detalles.size(); i++) {
            DetalleVentaEntity dv = detalles.get(i);
            DetalleVentaRequest dreq = request.getDetalles().get(i);

            // Registrar movimiento stock SALIDA
            com.erp.p03.controllers.dto.MovimientoStockRequest mr = new com.erp.p03.controllers.dto.MovimientoStockRequest();
            mr.setProductoId(dv.getProductoId());
            mr.setUsuarioId(request.getUsuarioId());
            mr.setTipoMovimiento("SALIDA");
            mr.setCantidad(dv.getCantidad());
            movimientoStockService.registrarMovimiento(mr);

            // Actualizar cantidad del lote
            if (dreq.getIdLote() != null) {
                // Restar la cantidad en el lote especificado
                loteService.updateCantidadLote(dreq.getIdLote(),
                        loteService.findById(dreq.getIdLote()).getCantidad() - dv.getCantidad());
            } else {
                // Si no hay lote especificado, buscar lotes por producto y restar en orden de vencimiento (FIFO por vencimiento)
                // Usaremos los lotes del producto
                ProductoEntity producto = productoService.findById(dv.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                int restante = dv.getCantidad();
                for (LoteEntity lote : producto.getLotes()) {
                    if (!Boolean.TRUE.equals(lote.getEstado())) continue;
                    int disponible = lote.getCantidad() == null ? 0 : lote.getCantidad();
                    if (disponible <= 0) continue;
                    int aRestar = Math.min(disponible, restante);
                    loteService.updateCantidadLote(lote.getIdLote(), disponible - aRestar);
                    restante -= aRestar;
                    if (restante <= 0) break;
                }
                if (restante > 0) throw new RuntimeException("Stock insuficiente para el producto " + dv.getProductoId());
            }

            // Recalcular stock total del producto (ya lo hace loteService al actualizar lote en muchos casos, pero hacemos una sincronización final)
            ProductoEntity prod = productoService.findById(dv.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            // productoService.quitarStock ya actualiza stock desde lotes internamente, pero para seguridad:
            int totalStock = prod.getLotes().stream()
                    .filter(l -> Boolean.TRUE.equals(l.getEstado()) && l.getCantidad() != null)
                    .mapToInt(LoteEntity::getCantidad)
                    .sum();
            prod.setStock(totalStock);
            productoService.save(prod);
        }

        return savedVenta;
    }

}
