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
import com.erp.p03.controllers.dto.PagoRequest;
import com.erp.p03.controllers.dto.ClienteFiadoDTO;
import com.erp.p03.controllers.dto.FiadoUpdateRequest;
import com.erp.p03.entities.DetalleVentaEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.entities.VentaEntity;
import com.erp.p03.entities.PagoEntity;
import com.erp.p03.entities.ClienteFiadoEntity;
import com.erp.p03.repositories.VentaRepository;
import com.erp.p03.repositories.DetalleVentaRepository;
import com.erp.p03.repositories.PagoRepository;
import com.erp.p03.controllers.dto.VentaWithHolidayDTO;
import com.erp.p03.entities.FeriadoEntity;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoStockService movimientoStockService;
    private final ProductoService productoService;
    private final LoteService loteService;
    private final FeriadoService feriadoService;
    private final PagoRepository pagoRepository;
    private final ClienteFiadoService clienteFiadoService; // servicio para clientes fiados

    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository,
                        MovimientoStockService movimientoStockService,
                        ProductoService productoService,
                        LoteService loteService,
                        FeriadoService feriadoService,
                        PagoRepository pagoRepository,
                        ClienteFiadoService clienteFiadoService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.movimientoStockService = movimientoStockService;
        this.productoService = productoService;
        this.loteService = loteService;
        this.feriadoService = feriadoService; // ahora se inyecta el servicio de feriados
        this.pagoRepository = pagoRepository;
        this.clienteFiadoService = clienteFiadoService;
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

        // Gestión de fiados: si request.fiado == true, marcar venta como fiado y establecer saldoPendiente = total
        if (request.getFiado() != null && Boolean.TRUE.equals(request.getFiado())) {
            venta.setFiado(true);
            venta.setSaldoPendiente(Optional.ofNullable(request.getTotal()).orElse(0));
            venta.setClienteId(request.getClienteId());
            // parse fechaVencimientoPago si viene (formato DD/MM/AAAA)
            if (request.getFechaVencimientoPago() != null && !request.getFechaVencimientoPago().isBlank()) {
                try {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
                    LocalDate d = LocalDate.parse(request.getFechaVencimientoPago().trim(), f);
                    venta.setFechaVencimientoPago(d);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Formato fechaVencimientoPago inválido. Usar DD/MM/AAAA");
                }
            }
        } else {
            venta.setFiado(false);
            venta.setSaldoPendiente(0);
        }

        VentaEntity savedVenta = ventaRepository.save(venta);

        // 2) Crear Detalles y movimientos por cada detalle
        List<DetalleVentaEntity> detalles = request.getDetalles().stream().map(d -> {
            DetalleVentaEntity dv = new DetalleVentaEntity();
            dv.setVentaId(savedVenta.getIdVenta());
            dv.setProductoId(d.getProductoId());
            dv.setCantidad(d.getCantidad());
            dv.setPrecioUnitario(d.getPrecioUnitario());
            dv.setSubtotal(d.getPrecioUnitario() * d.getCantidad());
            // Si la venta no es fiado y tiene metodoPago, aplicarlo directamente al detalle
            if (!Boolean.TRUE.equals(savedVenta.getFiado()) && savedVenta.getMetodoPago() != null) {
                dv.setMetodoPago(savedVenta.getMetodoPago());
            }
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
     * Quitar un producto de una venta identificando por productoId en vez de idDetalle.
     * Busca el detalle correspondiente y delega a quitarProductoDeVenta.
     */
    @Transactional
    public VentaEntity quitarProductoDeVentaPorProducto(Integer ventaId, Integer productoId, Integer idLote, Integer usuarioId) {
        DetalleVentaEntity detalle = detalleVentaRepository.findByVentaIdAndProductoId(ventaId, productoId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle de venta no encontrado para el producto en esa venta"));
        return quitarProductoDeVenta(ventaId, detalle.getIdDetalleVenta(), idLote, usuarioId);
    }

    // Transforma el historial de ventas en DTOs marcando feriados
    public List<VentaWithHolidayDTO> historialVentasConFeriados(String desde, String hasta, Integer usuarioId) {
        List<VentaEntity> ventas = historialVentas(desde, hasta, usuarioId);

        // Determinar disponibilidad del servicio de feriados y precargar feriados recurrentes.
        final boolean hasFeriadoService = this.feriadoService != null;
        final List<FeriadoEntity> recurrentes = hasFeriadoService
                ? feriadoService.findAll().stream()
                    .filter(f -> Boolean.TRUE.equals(f.getRecurrenteAnual()) && Boolean.TRUE.equals(f.getActivo()) && f.getFecha() != null)
                    .toList()
                : List.of();

        return ventas.stream().map(v -> {
             VentaWithHolidayDTO dto = new VentaWithHolidayDTO();
            dto.setIdVenta(v.getIdVenta());
            dto.setFecha(v.getFecha());
            dto.setUsuarioId(v.getUsuarioId());
            dto.setSubtotal(v.getSubtotal());
            dto.setIva(v.getIva());
            dto.setTotal(v.getTotal());
            dto.setMetodoPago(v.getMetodoPago());
            dto.setIsHoliday(false);
            dto.setHolidayName(null);

            if (hasFeriadoService && v.getFecha() != null) {
                LocalDate fecha = v.getFecha().toLocalDate();
                List<FeriadoEntity> exactos = feriadoService.findByFecha(fecha);
                if (!exactos.isEmpty()) {
                    dto.setIsHoliday(true);
                    dto.setHolidayName(exactos.get(0).getNombre());
                } else {
                    for (FeriadoEntity fr : recurrentes) {
                        if (fr.getFecha() != null && fr.getFecha().getDayOfMonth() == fecha.getDayOfMonth()
                                && fr.getFecha().getMonth() == fecha.getMonth()) {
                            dto.setIsHoliday(true);
                            dto.setHolidayName(fr.getNombre());
                            break;
                        }
                    }
                }
            }

            return dto;
        }).toList();
    }

    /**
     * Lista de ventas marcadas como fiado. Si pendientesOnly es true devuelve solo las que tienen saldo pendiente >0.
     */
    public List<VentaEntity> listarFiados(boolean pendientesOnly) {
        if (pendientesOnly) {
            return ventaRepository.findByFiadoTrueAndSaldoPendienteGreaterThan(0);
        }
        return ventaRepository.findByFiadoTrue();
    }

    /**
     * Registrar un pago para una venta (fiado). Si el pago cubre el saldo, marca la venta como pagada
     * y propaga el metodoPago a los detalles de la venta.
     */
    @Transactional
    public PagoEntity registrarPago(Integer ventaId, PagoRequest request) {
        if (request == null || request.getMonto() == null || request.getMonto() <= 0) {
            throw new IllegalArgumentException("Monto de pago inválido");
        }

        VentaEntity venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        Integer saldo = Optional.ofNullable(venta.getSaldoPendiente()).orElse(venta.getTotal());
        if (saldo == null) saldo = 0;

        if (!Boolean.TRUE.equals(venta.getFiado()) && saldo <= 0) {
            // Si no es fiado y no tiene saldo, no hay sentido en registrar pago aquí
            throw new IllegalArgumentException("La venta no está marcada como fiado o no tiene saldo pendiente");
        }

        int montoPago = request.getMonto();

        PagoEntity pago = new PagoEntity();
        pago.setVentaId(ventaId);
        pago.setUsuarioId(request.getUsuarioId());
        pago.setMonto(montoPago);
        pago.setMetodoPago(request.getMetodoPago());
        pago.setFecha(LocalDateTime.now());
        pagoRepository.save(pago);

        // Actualizar saldo
        int nuevoSaldo = Math.max(saldo - montoPago, 0);
        venta.setSaldoPendiente(nuevoSaldo);

        if (nuevoSaldo == 0) {
            venta.setFiado(false);
            venta.setMetodoPago(request.getMetodoPago());
            venta.setPagoCompletadoAt(LocalDateTime.now());

            // Propagar metodoPago a los detalles
            List<DetalleVentaEntity> detalles = detalleVentaRepository.findByVentaId(ventaId);
            for (DetalleVentaEntity d : detalles) {
                d.setMetodoPago(request.getMetodoPago());
            }
            detalleVentaRepository.saveAll(detalles);
        }

        ventaRepository.save(venta);
        return pago;
    }

    public List<PagoEntity> getPagosByVentaId(Integer ventaId) {
        return pagoRepository.findByVentaIdOrderByFechaDesc(ventaId);
    }

    // ================== Métodos adicionales para CRUD de fiados y clientes con fiado ==================

    /**
     * Marca una venta existente como fiado (activa el fiado y establece saldoPendiente si no existe).
     * Se aceptan campos opcionales en el request para clienteId y fechaVencimientoPago.
     */
    @Transactional
    public VentaEntity marcarComoFiado(Integer ventaId, FiadoUpdateRequest req) {
        VentaEntity v = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        // Activar fiado
        v.setFiado(true);
        // Si no se especifica saldo, se establece al total de la venta
        if (req.getSaldoPendiente() != null) {
            v.setSaldoPendiente(req.getSaldoPendiente());
        } else {
            v.setSaldoPendiente(Optional.ofNullable(v.getTotal()).orElse(0));
        }
        if (req.getClienteId() != null) v.setClienteId(req.getClienteId());
        if (req.getFiado() != null) v.setFiado(req.getFiado()); // permite forzar true/false si se quiere

        if (req.getFechaVencimientoPago() != null && !req.getFechaVencimientoPago().isBlank()) {
            try {
                DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
                java.time.LocalDate d = java.time.LocalDate.parse(req.getFechaVencimientoPago().trim(), f);
                v.setFechaVencimientoPago(d);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Formato fechaVencimientoPago inválido. Usar DD/MM/AAAA");
            }
        }
        return ventaRepository.save(v);
    }

    /**
     * Devuelve una venta marcada como fiado por su id.
     */
    public VentaEntity getFiadoById(Integer ventaId) {
        VentaEntity v = ventaRepository.findById(ventaId).orElse(null);
        if (v == null) return null;
        return Boolean.TRUE.equals(v.getFiado()) ? v : null;
    }

    /**
     * Actualiza campos relevantes de un fiado (clienteId, fechaVencimientoPago, saldoPendiente, fiado)
     * La fecha se recibe en formato dd/MM/yyyy (String). Si la cadena es null o vacía no se modifica.
     */
    @Transactional
    public VentaEntity actualizarFiado(Integer ventaId, FiadoUpdateRequest req) {
        VentaEntity v = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        if (!Boolean.TRUE.equals(v.getFiado())) {
            throw new IllegalArgumentException("La venta indicada no está marcada como fiado");
        }

        if (req.getClienteId() != null) v.setClienteId(req.getClienteId());
        if (req.getSaldoPendiente() != null) v.setSaldoPendiente(req.getSaldoPendiente());
        if (req.getFiado() != null) v.setFiado(req.getFiado());

        if (req.getFechaVencimientoPago() != null && !req.getFechaVencimientoPago().isBlank()) {
            try {
                DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
                java.time.LocalDate d = java.time.LocalDate.parse(req.getFechaVencimientoPago().trim(), f);
                v.setFechaVencimientoPago(d);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Formato fechaVencimientoPago inválido. Usar DD/MM/AAAA");
            }
        }

        return ventaRepository.save(v);
    }

    /**
     * "Cancelar" un fiado: desmarca la venta como fiado y limpia saldo y fecha de vencimiento.
     * No elimina la venta para preservar historial.
     */
    @Transactional
    public VentaEntity cancelarFiado(Integer ventaId) {
        VentaEntity v = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        if (!Boolean.TRUE.equals(v.getFiado())) {
            throw new IllegalArgumentException("La venta indicada no está marcada como fiado");
        }
        v.setFiado(false);
        v.setSaldoPendiente(0);
        v.setFechaVencimientoPago(null);
        // No borramos clienteId para mantener historico; si se desea, frontend puede limpiar
        return ventaRepository.save(v);
    }

    /**
     * Devuelve un resumen por cliente que tiene fiados: clienteId, totalPendiente y cantidadDeFiados.
     * Si `pendientesOnly` es true considera sólo ventas con saldoPendiente>0.
     */
    public List<ClienteFiadoDTO> listarClientesConFiado(boolean pendientesOnly) {
        List<VentaEntity> fiados = pendientesOnly ?
                ventaRepository.findByFiadoTrueAndSaldoPendienteGreaterThan(0) : ventaRepository.findByFiadoTrue();

        // Agrupar por clienteId
        java.util.Map<Integer, ClienteFiadoDTO> map = new java.util.HashMap<>();
        for (VentaEntity v : fiados) {
            Integer clienteId = v.getClienteId();
            if (clienteId == null) continue; // ignorar ventas sin cliente asociado
            ClienteFiadoDTO dto = map.get(clienteId);
            if (dto == null) {
                dto = new ClienteFiadoDTO();
                dto.setClienteId(clienteId);
                dto.setCantidadFiados(0);
                dto.setTotalPendiente(0);
                // rellenar datos del cliente si existe
                java.util.Optional<com.erp.p03.entities.ClienteFiadoEntity> optCliente = clienteFiadoService.findById(clienteId);
                if (optCliente.isPresent()) {
                    com.erp.p03.entities.ClienteFiadoEntity c = optCliente.get();
                    dto.setNombre(c.getNombre());
                    dto.setApellido(c.getApellido());
                    dto.setTelefono(c.getTelefono());
                }
                map.put(clienteId, dto);
            }
            dto.setCantidadFiados(dto.getCantidadFiados() + 1);
            dto.setTotalPendiente(dto.getTotalPendiente() + (v.getSaldoPendiente() == null ? 0 : v.getSaldoPendiente()));
        }

        return map.values().stream().toList();
    }

    /**
     * Lista las ventas fiadas de un cliente.
     */
    public List<VentaEntity> listarFiadosPorCliente(Integer clienteId, boolean pendientesOnly) {
        if (clienteId == null) throw new IllegalArgumentException("clienteId requerido");
        List<VentaEntity> fiados = ventaRepository.findByFiadoTrue();
        java.util.List<VentaEntity> result = new java.util.ArrayList<>();
        for (VentaEntity v : fiados) {
            if (clienteId.equals(v.getClienteId())) {
                if (pendientesOnly) {
                    if (v.getSaldoPendiente() != null && v.getSaldoPendiente() > 0) result.add(v);
                } else result.add(v);
            }
        }
        return result;
    }

}
