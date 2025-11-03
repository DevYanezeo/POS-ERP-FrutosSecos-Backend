package com.erp.p03.services;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Comparator;
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

    public void deleteById(Integer id) {
        ventaRepository.deleteById(id);
    }

    // Metodo auxiliar: parse de fechas limitado al formato dd/MM/yyyy (ej. 30/10/2025).
    // Si la entrada no incluye hora se interpreta como inicio del día.
    // No se aceptan formatos en texto ni ISO en esta versión.
    private LocalDateTime parseFlexibleDate(String input, boolean endOfDay) {
        if (input == null || input.isBlank()) return null;
        String s = input.trim();

        // Rechazar explícitamente strings que claramente no sean dd/MM/yyyy
        // (por ejemplo si contienen letras o 'T') para evitar ambigüedad.
        if (s.contains("T") || s.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException("Formato de fecha inválido: '" + input + "'. Formato aceptado: dd/MM/yyyy (ej. 20/11/2025).");
        }

        // Intentar formato dd/MM/yyyy o d/M/uuuu (ej. 20/11/2025 o 1/1/2025)
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
        try {
            LocalDate d = LocalDate.parse(s, f);
            return endOfDay ? LocalDateTime.of(d, LocalTime.MAX) : d.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            throw new IllegalArgumentException("Formato de fecha inválido: '" + input + "'. Formato aceptado: dd/MM/yyyy (ej. 20/11/2025).");
        }
    }

    /**
     * Devuelve el historial de ventas. Los parámetros son opcionales:
     * - desde: fecha/hora o fecha en formatos flexibles
     * - hasta: fecha/hora o fecha en formatos flexibles
     * - usuarioId: filtra ventas realizadas por ese usuario
     *
     * Si no se pasa ningún filtro, devuelve todas las ventas ordenadas por fecha descendente.
     */
    public List<VentaEntity> historialVentas(String desdeIso, String hastaIso, Integer usuarioId) {
        LocalDateTime desde = null;
        LocalDateTime hasta = null;
        // Ahora usamos parseFlexibleDate: para 'desde' tomamos inicio de día si no hay hora;
        // para 'hasta' tomamos fin de día si la entrada no especifica hora.
        if (desdeIso != null && !desdeIso.isBlank()) {
            desde = parseFlexibleDate(desdeIso, false);
        }
        if (hastaIso != null && !hastaIso.isBlank()) {
            // Si el input incluye 'T' asumimos que contiene hora; si no, parseFlexibleDate con endOfDay=true
            boolean containsTime = hastaIso.contains("T") || hastaIso.matches(".*\\d:.*");
            hasta = parseFlexibleDate(hastaIso, !containsTime);
        }

        List<VentaEntity> ventas;

        // Elegir consulta adecuada según parámetros para filtrar en BD
        if (desde != null && hasta != null && usuarioId != null) {
            ventas = ventaRepository.findByFechaBetweenAndUsuarioId(desde, hasta, usuarioId);
        } else if (desde != null && hasta != null) {
            ventas = ventaRepository.findByFechaBetween(desde, hasta);
        } else if (desde != null && usuarioId != null) {
            ventas = ventaRepository.findByFechaAfterAndUsuarioId(desde, usuarioId);
        } else if (hasta != null && usuarioId != null) {
            ventas = ventaRepository.findByFechaBeforeAndUsuarioId(hasta, usuarioId);
        } else if (usuarioId != null) {
            ventas = ventaRepository.findByUsuarioId(usuarioId);
        } else if (desde != null) {
            ventas = ventaRepository.findByFechaAfter(desde);
        } else if (hasta != null) {
            ventas = ventaRepository.findByFechaBefore(hasta);
        } else {
            ventas = ventaRepository.findAllByOrderByFechaDesc();
        }

        // Asegurar orden por fecha descendente antes de devolver
        ventas.sort(Comparator.comparing(VentaEntity::getFecha, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return ventas;
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

    // ======================== Quitar producto de una venta activa =========================
    /**
     * Quita (elimina) un detalle de venta de una venta activa y revierte sus efectos en stock.
     * - Si se proporciona idLote, la cantidad se suma de vuelta a ese lote.
     * - Si no se proporciona idLote, se registra un movimiento de tipo "INGRESO" para aumentar el stock del producto.
     * - Actualiza subtotal, iva y total de la venta y elimina el detalle.
     *
     * NOTA: actualmente `DetalleVentaEntity` no guarda el idLote usado al confirmar la venta; por
     * eso se acepta `idLote` opcional desde el cliente para poder restaurar la cantidad al lote original.
     * Si no se proporciona, sólo se repone el stock del producto (no se modifica lotes).
     */
    @Transactional
    public VentaEntity quitarProductoDeVenta(Integer ventaId, Integer detalleId, Integer idLote, Integer usuarioId) {
        // Validar existencia de venta y detalle
        VentaEntity venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        DetalleVentaEntity detalle = detalleVentaRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle de venta no encontrado"));

        if (!Objects.equals(detalle.getVentaId(), ventaId)) {
            throw new IllegalArgumentException("El detalle no pertenece a la venta indicada");
        }

        int cantidad = Optional.ofNullable(detalle.getCantidad()).orElse(0);
        int subtotalDetalle = Optional.ofNullable(detalle.getSubtotal()).orElse(0);

        // 1) Revertir lote o registrar movimiento de ingreso
        if (idLote != null) {
            // Añadir la cantidad devuelta al lote indicado
            LoteEntity lote = loteService.findById(idLote);
            int cantidadLoteActual = Optional.ofNullable(lote.getCantidad()).orElse(0);
            loteService.updateCantidadLote(idLote, cantidadLoteActual + cantidad);

            // Recalcular stock total del producto y persistir
            ProductoEntity producto = productoService.findById(detalle.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            int totalStock = producto.getLotes() == null ? 0 : producto.getLotes().stream()
                    .filter(Objects::nonNull)
                    .filter(l -> Boolean.TRUE.equals(l.getEstado()) && l.getCantidad() != null)
                    .mapToInt(LoteEntity::getCantidad)
                    .sum();
            producto.setStock(totalStock);
            productoService.save(producto);
        } else {
            // Registrar movimiento de stock de tipo INGRESO para reponer stock del producto
            com.erp.p03.controllers.dto.MovimientoStockRequest mr = new com.erp.p03.controllers.dto.MovimientoStockRequest();
            mr.setProductoId(detalle.getProductoId());
            mr.setUsuarioId(usuarioId);
            mr.setTipoMovimiento("INGRESO");
            mr.setCantidad(cantidad);
            movimientoStockService.registrarMovimiento(mr);
        }

        // 2) Actualizar totales de la venta
        int oldSubtotal = Optional.ofNullable(venta.getSubtotal()).orElse(0);
        int oldIva = Optional.ofNullable(venta.getIva()).orElse(0);
        int oldTotal = Optional.ofNullable(venta.getTotal()).orElse(0);

        int ivaRemovida = Math.toIntExact(Math.round(subtotalDetalle * 0.19));
        int newSubtotal = Math.max(oldSubtotal - subtotalDetalle, 0);
        int newIva = Math.max(oldIva - ivaRemovida, 0);
        int newTotal = Math.max(oldTotal - subtotalDetalle - ivaRemovida, 0);

        venta.setSubtotal(newSubtotal);
        venta.setIva(newIva);
        venta.setTotal(newTotal);
        ventaRepository.save(venta);

        // 3) Eliminar detalle
        detalleVentaRepository.deleteById(detalleId);

        return venta;
    }

    /**
     * Versión convenience: quitar un producto de una venta identificando por productoId en vez de idDetalle.
     * Busca el detalle correspondiente y delega a quitarProductoDeVenta.
     */
    @Transactional
    public VentaEntity quitarProductoDeVentaPorProducto(Integer ventaId, Integer productoId, Integer idLote, Integer usuarioId) {
        DetalleVentaEntity detalle = detalleVentaRepository.findByVentaIdAndProductoId(ventaId, productoId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle de venta no encontrado para el producto en esa venta"));
        return quitarProductoDeVenta(ventaId, detalle.getIdDetalleVenta(), idLote, usuarioId);
    }

}
