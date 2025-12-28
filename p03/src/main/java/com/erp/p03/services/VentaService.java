package com.erp.p03.services;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

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
import com.erp.p03.controllers.dto.ProductSalesDTO;
import java.sql.Timestamp;
import java.time.temporal.WeekFields;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoStockService movimientoStockService;
    private final ProductoService productoService;
    private final LoteService loteService;
    private final FeriadoService feriadoService;
    private final GastoService gastoService; // Added GastoService
    private final PagoRepository pagoRepository;
    private final ClienteFiadoService clienteFiadoService; // servicio para clientes fiados

    public VentaService(VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            MovimientoStockService movimientoStockService,
            ProductoService productoService,
            LoteService loteService,
            FeriadoService feriadoService,
            GastoService gastoService,
            PagoRepository pagoRepository,
            ClienteFiadoService clienteFiadoService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.movimientoStockService = movimientoStockService;
        this.productoService = productoService;
        this.loteService = loteService;
        this.feriadoService = feriadoService;
        this.gastoService = gastoService;
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

    // Metodo auxiliar: parse de fechas limitado al formato dd/MM/yyyy (ej.
    // 30/10/2025).
    // Si la entrada no incluye hora se interpreta como inicio del día.
    // No se aceptan formatos en texto ni ISO en esta versión.
    private LocalDateTime parseFlexibleDate(String input, boolean endOfDay) {
        if (input == null || input.isBlank())
            return null;
        String s = input.trim();

        // Rechazar explícitamente strings que claramente no sean dd/MM/yyyy
        // (por ejemplo si contienen letras o 'T') para evitar ambigüedad.
        if (s.contains("T") || s.matches(".*[A-Za-z].*")) {
            throw new IllegalArgumentException(
                    "Formato de fecha inválido: '" + input + "'. Formato aceptado: dd/MM/yyyy (ej. 20/11/2025).");
        }

        // Intentar formato dd/MM/yyyy o d/M/uuuu (ej. 20/11/2025 o 1/1/2025)
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
        try {
            LocalDate d = LocalDate.parse(s, f);
            return endOfDay ? LocalDateTime.of(d, LocalTime.MAX) : d.atStartOfDay();
        } catch (DateTimeParseException ignored) {
            throw new IllegalArgumentException(
                    "Formato de fecha inválido: '" + input + "'. Formato aceptado: dd/MM/yyyy (ej. 20/11/2025).");
        }
    }

    /**
     * Devuelve el historial de ventas. Los parámetros son opcionales:
     * - desde: fecha/hora o fecha en formatos flexibles
     * - hasta: fecha/hora o fecha en formatos flexibles
     * - usuarioId: filtra ventas realizadas por ese usuario
     *
     * Si no se pasa ningún filtro, devuelve todas las ventas ordenadas por fecha
     * descendente.
     */
    public List<VentaEntity> historialVentas(String desdeIso, String hastaIso, Integer usuarioId) {
        LocalDateTime desde = null;
        LocalDateTime hasta = null;
        // Ahora usamos parseFlexibleDate: para 'desde' tomamos inicio de día si no hay
        // hora;
        // para 'hasta' tomamos fin de día si la entrada no especifica hora.
        if (desdeIso != null && !desdeIso.isBlank()) {
            desde = parseFlexibleDate(desdeIso, false);
        }
        if (hastaIso != null && !hastaIso.isBlank()) {
            // Si el input incluye 'T' asumimos que contiene hora; si no, parseFlexibleDate
            // con endOfDay=true
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
        ventas.sort(Comparator.comparing(VentaEntity::getFecha, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        return ventas;
    }

    /**
     * Flujo transaccional de confirmación de venta:
     * - Crea la entidad Venta
     * - Crea los DetalleVenta
     * - Para cada detalle crea un MovimientoStock (SALIDA)
     * - Actualiza la cantidad del lote correspondiente y recalcula el stock del
     * producto
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

        // Gestión de fiados: si request.fiado == true, marcar venta como fiado y
        // establecer saldoPendiente = total
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
        // Ahora se manejan los lotes y se guarda idLote y costoUnitario en cada
        // DetalleVentaEntity.
        List<DetalleVentaEntity> detallesACrear = new java.util.ArrayList<>();

        for (DetalleVentaRequest dreq : request.getDetalles()) {
            if (dreq == null)
                continue;
            Integer pedidoCantidad = Optional.ofNullable(dreq.getCantidad()).orElse(0);
            if (pedidoCantidad <= 0)
                continue;

            // Si se especifica idLote, consumir de ese lote en un solo detalle
            if (dreq.getIdLote() != null) {
                LoteEntity lote = loteService.findById(dreq.getIdLote());
                int disponible = Optional.ofNullable(lote.getCantidad()).orElse(0);
                if (disponible < pedidoCantidad) {
                    throw new RuntimeException("Stock insuficiente en el lote " + dreq.getIdLote());
                }
                DetalleVentaEntity dv = new DetalleVentaEntity();
                dv.setVentaId(savedVenta.getIdVenta());
                dv.setProductoId(dreq.getProductoId());
                dv.setCantidad(pedidoCantidad);
                dv.setPrecioUnitario(dreq.getPrecioUnitario());
                dv.setSubtotal(Optional.ofNullable(dreq.getPrecioUnitario()).orElse(0) * pedidoCantidad);
                if (!Boolean.TRUE.equals(savedVenta.getFiado()) && savedVenta.getMetodoPago() != null) {
                    dv.setMetodoPago(savedVenta.getMetodoPago());
                }
                dv.setIdLote(dreq.getIdLote());
                dv.setCostoUnitario(lote.getCosto());
                detallesACrear.add(dv);

                // Actualizar cantidad del lote
                loteService.updateCantidadLote(dreq.getIdLote(), disponible - pedidoCantidad);

                // Registrar movimiento para este lote
                com.erp.p03.controllers.dto.MovimientoStockRequest mr = new com.erp.p03.controllers.dto.MovimientoStockRequest();
                mr.setProductoId(dreq.getProductoId());
                mr.setUsuarioId(request.getUsuarioId());
                mr.setTipoMovimiento("SALIDA");
                mr.setCantidad(pedidoCantidad);
                movimientoStockService.registrarMovimiento(mr);

            } else {
                // No se especifica lote: consumir lotes disponibles en orden de vencimiento
                ProductoEntity producto = productoService.findById(dreq.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                int restante = pedidoCantidad;
                for (LoteEntity lote : producto.getLotes()) {
                    if (!Boolean.TRUE.equals(lote.getEstado()))
                        continue;
                    int disponible = Optional.ofNullable(lote.getCantidad()).orElse(0);
                    if (disponible <= 0)
                        continue;
                    int aRestar = Math.min(disponible, restante);

                    DetalleVentaEntity dv = new DetalleVentaEntity();
                    dv.setVentaId(savedVenta.getIdVenta());
                    dv.setProductoId(dreq.getProductoId());
                    dv.setCantidad(aRestar);
                    dv.setPrecioUnitario(dreq.getPrecioUnitario());
                    dv.setSubtotal(Optional.ofNullable(dreq.getPrecioUnitario()).orElse(0) * aRestar);
                    if (!Boolean.TRUE.equals(savedVenta.getFiado()) && savedVenta.getMetodoPago() != null) {
                        dv.setMetodoPago(savedVenta.getMetodoPago());
                    }
                    dv.setIdLote(lote.getIdLote());
                    dv.setCostoUnitario(lote.getCosto());
                    detallesACrear.add(dv);

                    // Actualizar lote
                    loteService.updateCantidadLote(lote.getIdLote(), disponible - aRestar);

                    // Registrar movimiento por el lote
                    com.erp.p03.controllers.dto.MovimientoStockRequest mr = new com.erp.p03.controllers.dto.MovimientoStockRequest();
                    mr.setProductoId(dreq.getProductoId());
                    mr.setUsuarioId(request.getUsuarioId());
                    mr.setTipoMovimiento("SALIDA");
                    mr.setCantidad(aRestar);
                    movimientoStockService.registrarMovimiento(mr);

                    restante -= aRestar;
                    if (restante <= 0)
                        break;
                }
                if (restante > 0)
                    throw new RuntimeException("Stock insuficiente para el producto " + dreq.getProductoId());
            }
        }

        // Guardar todos los detalles creados
        detalleVentaRepository.saveAll(detallesACrear);

        // Recalcular stock total de cada producto afectado y persistir
        // Para simplicidad, recalculamos por cada detalle creado
        java.util.Set<Integer> productosAfectados = new java.util.HashSet<>();
        for (DetalleVentaEntity dv : detallesACrear)
            productosAfectados.add(dv.getProductoId());
        for (Integer pid : productosAfectados) {
            ProductoEntity prod = productoService.findById(pid)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            int totalStock = prod.getLotes().stream()
                    .filter(l -> Boolean.TRUE.equals(l.getEstado()) && l.getCantidad() != null)
                    .mapToInt(LoteEntity::getCantidad)
                    .sum();
            prod.setStock(totalStock);
            productoService.save(prod);
        }

        return savedVenta;
    }

    // ======================== Quitar producto de una venta activa
    // =========================
    /**
     * Quita (elimina) un detalle de venta de una venta activa y revierte sus
     * efectos en stock.
     * - Si se proporciona idLote, la cantidad se suma de vuelta a ese lote.
     * - Si no se proporciona idLote, se registra un movimiento de tipo "INGRESO"
     * para aumentar el stock del producto.
     * - Actualiza subtotal, iva y total de la venta y elimina el detalle.
     *
     * NOTA: actualmente `DetalleVentaEntity` no guarda el idLote usado al confirmar
     * la venta; por
     * eso se acepta `idLote` opcional desde el cliente para poder restaurar la
     * cantidad al lote original.
     * Si no se proporciona, sólo se repone el stock del producto (no se modifica
     * lotes).
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
            int totalStock = producto.getLotes() == null ? 0
                    : producto.getLotes().stream()
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
     * Quitar un producto de una venta identificando por productoId en vez de
     * idDetalle.
     * Busca el detalle correspondiente y delega a quitarProductoDeVenta.
     */
    @Transactional
    public VentaEntity quitarProductoDeVentaPorProducto(Integer ventaId, Integer productoId, Integer idLote,
            Integer usuarioId) {
        DetalleVentaEntity detalle = detalleVentaRepository.findByVentaIdAndProductoId(ventaId, productoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Detalle de venta no encontrado para el producto en esa venta"));
        return quitarProductoDeVenta(ventaId, detalle.getIdDetalleVenta(), idLote, usuarioId);
    }

    // Transforma el historial de ventas en DTOs marcando feriados
    public List<VentaWithHolidayDTO> historialVentasConFeriados(String desde, String hasta, Integer usuarioId) {
        List<VentaEntity> ventas = historialVentas(desde, hasta, usuarioId);

        // Determinar disponibilidad del servicio de feriados y precargar feriados
        // recurrentes.
        final boolean hasFeriadoService = this.feriadoService != null;
        final List<FeriadoEntity> recurrentes = hasFeriadoService
                ? feriadoService.findAll().stream()
                        .filter(f -> Boolean.TRUE.equals(f.getRecurrenteAnual()) && Boolean.TRUE.equals(f.getActivo())
                                && f.getFecha() != null)
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
     * Lista de ventas marcadas como fiado. Si pendientesOnly es true devuelve solo
     * las que tienen saldo pendiente >0.
     */
    public List<VentaEntity> listarFiados(boolean pendientesOnly) {
        List<VentaEntity> ventas;

        if (pendientesOnly) {
            ventas = ventaRepository.findByFiadoTrueAndSaldoPendienteGreaterThan(0);
        } else {
            ventas = ventaRepository.findByFiadoTrue();
        }

        // ← AGREGAR ESTO: Cargar cliente para cada venta
        ventas.forEach(venta -> {
            if (venta.getClienteId() != null) {
                clienteFiadoService.findById(venta.getClienteId())
                        .ifPresent(venta::setCliente);
            }
        });

        return ventas;
    }

    /**
     * Registrar un pago para una venta (fiado). Si el pago cubre el saldo, marca la
     * venta como pagada
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
        if (saldo == null)
            saldo = 0;

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

    // ================== Métodos adicionales para CRUD de fiados y clientes con
    // fiado ==================

    /**
     * Marca una venta existente como fiado (activa el fiado y establece
     * saldoPendiente si no existe).
     * Se aceptan campos opcionales en el request para clienteId y
     * fechaVencimientoPago.
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
        if (req.getClienteId() != null)
            v.setClienteId(req.getClienteId());
        if (req.getFiado() != null)
            v.setFiado(req.getFiado()); // permite forzar true/false si se quiere

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
        if (v == null || !Boolean.TRUE.equals(v.getFiado())) {
            return null;
        }

        // ← AGREGAR ESTO: Cargar cliente
        if (v.getClienteId() != null) {
            clienteFiadoService.findById(v.getClienteId())
                    .ifPresent(v::setCliente);
        }

        return v;
    }

    /**
     * Actualiza campos relevantes de un fiado (clienteId, fechaVencimientoPago,
     * saldoPendiente, fiado)
     * La fecha se recibe en formato dd/MM/AAAA (String). Si la cadena es null o
     * vacía no se modifica.
     */
    @Transactional
    public VentaEntity actualizarFiado(Integer ventaId, FiadoUpdateRequest req) {
        VentaEntity v = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        if (!Boolean.TRUE.equals(v.getFiado())) {
            throw new IllegalArgumentException("La venta indicada no está marcada como fiado");
        }

        if (req.getClienteId() != null)
            v.setClienteId(req.getClienteId());
        if (req.getSaldoPendiente() != null)
            v.setSaldoPendiente(req.getSaldoPendiente());
        if (req.getFiado() != null)
            v.setFiado(req.getFiado());

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
     * "Cancelar" un fiado: desmarca la venta como fiado y limpia saldo y fecha de
     * vencimiento.
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
        // No borramos clienteId para mantener historico; si se desea, frontend puede
        // limpiar
        return ventaRepository.save(v);
    }

    /**
     * Devuelve un resumen por cliente que tiene fiados: clienteId, totalPendiente y
     * cantidadDeFiados.
     * Si `pendientesOnly` es true considera sólo ventas con saldoPendiente>0.
     */
    public List<ClienteFiadoDTO> listarClientesConFiado(boolean pendientesOnly) {
        List<VentaEntity> fiados = pendientesOnly ? ventaRepository.findByFiadoTrueAndSaldoPendienteGreaterThan(0)
                : ventaRepository.findByFiadoTrue();

        // Agrupar por clienteId
        java.util.Map<Integer, ClienteFiadoDTO> map = new java.util.HashMap<>();
        for (VentaEntity v : fiados) {
            Integer clienteId = v.getClienteId();
            if (clienteId == null)
                continue; // ignorar ventas sin cliente asociado
            ClienteFiadoDTO dto = map.get(clienteId);
            if (dto == null) {
                dto = new ClienteFiadoDTO();
                dto.setClienteId(clienteId);
                dto.setCantidadFiados(0);
                dto.setTotalPendiente(0);
                // rellenar datos del cliente si existe
                java.util.Optional<com.erp.p03.entities.ClienteFiadoEntity> optCliente = clienteFiadoService
                        .findById(clienteId);
                if (optCliente.isPresent()) {
                    com.erp.p03.entities.ClienteFiadoEntity c = optCliente.get();
                    dto.setNombre(c.getNombre());
                    dto.setApellido(c.getApellido());
                    dto.setTelefono(c.getTelefono());
                }
                map.put(clienteId, dto);
            }
            dto.setCantidadFiados(dto.getCantidadFiados() + 1);
            dto.setTotalPendiente(
                    dto.getTotalPendiente() + (v.getSaldoPendiente() == null ? 0 : v.getSaldoPendiente()));
        }

        return map.values().stream().toList();
    }

    /**
     * Lista las ventas fiadas de un cliente.
     */
    public List<VentaEntity> listarFiadosPorCliente(Integer clienteId, boolean pendientesOnly) {
        if (clienteId == null)
            throw new IllegalArgumentException("clienteId requerido");

        List<VentaEntity> fiados = ventaRepository.findByFiadoTrue();
        List<VentaEntity> result = new ArrayList<>();

        // Cargar el cliente una sola vez
        Optional<ClienteFiadoEntity> clienteOpt = clienteFiadoService.findById(clienteId);

        for (VentaEntity v : fiados) {
            if (clienteId.equals(v.getClienteId())) {
                if (pendientesOnly) {
                    if (v.getSaldoPendiente() != null && v.getSaldoPendiente() > 0) {
                        clienteOpt.ifPresent(v::setCliente); // ← AGREGAR
                        result.add(v);
                    }
                } else {
                    clienteOpt.ifPresent(v::setCliente); // ← AGREGAR
                    result.add(v);
                }
            }
        }
        return result;
    }
    // -------------------- Reportes: productos mas vendidos --------------------

    /**
     * Metodo genérico que consulta la repo y mapea a DTOs.
     */
    public java.util.List<ProductSalesDTO> productosMasVendidosEntre(LocalDateTime start, LocalDateTime end,
            Integer limit) {
        // Validar parámetros
        if (start == null || end == null)
            throw new IllegalArgumentException("start y end son requeridos");
        Timestamp tsStart = Timestamp.valueOf(start);
        Timestamp tsEnd = Timestamp.valueOf(end);
        // Consultar repo
        java.util.List<Object[]> rows = detalleVentaRepository.findProductSalesBetweenDates(tsStart, tsEnd);
        java.util.List<ProductSalesDTO> result = new java.util.ArrayList<>();
        int count = 0;
        // Mapear a DTOs
        for (Object[] row : rows) {
            if (limit != null && count >= limit)
                break;
            ProductSalesDTO dto = new ProductSalesDTO();
            Object o0 = row[0];
            Object o1 = row[1];
            Object o2 = row[2];
            Object o3 = row[3];
            Integer productoId = null;
            // Determinar tipo de o0 y convertir a Integer
            if (o0 instanceof BigInteger)
                productoId = ((BigInteger) o0).intValue();
            else if (o0 instanceof Number)
                productoId = ((Number) o0).intValue();
            dto.setProductoId(productoId);
            dto.setNombre(o1 == null ? null : o1.toString());
            Integer totalCantidad = null;
            // Determinar tipo de o2 y convertir a Integer
            if (o2 instanceof BigDecimal)
                totalCantidad = ((BigDecimal) o2).intValue();
            else if (o2 instanceof BigInteger)
                totalCantidad = ((BigInteger) o2).intValue();
            else if (o2 instanceof Number)
                totalCantidad = ((Number) o2).intValue();
            dto.setTotalCantidad(totalCantidad == null ? 0 : totalCantidad);
            Integer totalSubtotal = null;
            // Determinar tipo de o3 y convertir a Integer
            if (o3 instanceof BigDecimal)
                totalSubtotal = ((BigDecimal) o3).intValue();
            else if (o3 instanceof BigInteger)
                totalSubtotal = ((BigInteger) o3).intValue();
            else if (o3 instanceof Number)
                totalSubtotal = ((Number) o3).intValue();
            dto.setTotalSubtotal(totalSubtotal == null ? 0 : totalSubtotal);
            result.add(dto);
            count++;
        }
        return result;
    }

    /**
     * Productos mas vendidos en una semana.
     * Comportamiento actualizado: si se proporciona `month` (1-12), entonces `week`
     * se interpreta como
     * el índice de semana dentro del mes (1 = días 1-7, 2 = días 8-14, 3 = días
     * 15-21, 4 = días 22-28, 5 = días 29-fin).
     * Si `month` es null, preservamos el comportamiento previo usando
     * WeekFields.ISO (semana ISO del año).
     */
    public java.util.List<ProductSalesDTO> productosMasVendidosPorSemana(Integer year, Integer month, Integer week,
            Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        // Si month es null, usamos semana ISO del año
        if (month == null) {
            WeekFields wf = WeekFields.ISO;
            int targetYear = (year == null) ? today.get(wf.weekBasedYear()) : year;
            int targetWeek = (week == null) ? today.get(wf.weekOfWeekBasedYear()) : week;

            LocalDate firstDay = LocalDate.now()
                    .with(wf.weekBasedYear(), targetYear)
                    .with(wf.weekOfWeekBasedYear(), targetWeek)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastDay = firstDay.plusDays(6);
            LocalDateTime start = firstDay.atStartOfDay();
            LocalDateTime end = lastDay.atTime(LocalTime.MAX);
            return productosMasVendidosEntre(start, end, limit);
        }
        // Si month es proporcionado, interpretamos week como semana dentro del mes
        else {
            int y = (year == null) ? today.getYear() : year;
            int m = month;
            // Validar mes
            if (m < 1 || m > 12)
                throw new IllegalArgumentException("month debe estar entre 1 y 12");
            LocalDate firstOfMonth = LocalDate.of(y, m, 1);
            LocalDate lastOfMonth = firstOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            int maxWeeks = (lastOfMonth.getDayOfMonth() + 6) / 7; // semanas completas en el mes
            int wk = (week == null) ? 1 : week;
            // Validar rango de semana dentro del mes
            if (wk < 1 || wk > maxWeeks)
                throw new IllegalArgumentException("week dentro del mes fuera de rango (1.." + maxWeeks + ")");
            int startDay = 1 + (wk - 1) * 7;
            LocalDate startDate = LocalDate.of(y, m, startDay);
            LocalDate endDate = startDate.plusDays(6);
            // Ajustar endDate si excede fin de mes
            if (endDate.isAfter(lastOfMonth))
                endDate = lastOfMonth;
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            return productosMasVendidosEntre(start, end, limit);
        }
    }

    /**
     * Productos mas vendidos en un mes. Parámetros opcionales: year y month (1-12).
     */
    public java.util.List<ProductSalesDTO> productosMasVendidosPorMes(Integer year, Integer month, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        // Determinar año y mes
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;
        // Validar mes
        LocalDate first = LocalDate.of(y, m, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosMasVendidosEntre(start, end, limit);
    }

    // -------------------- Reportes: productos menos vendidos --------------------

    /**
     * Versión genérica para obtener productos menos vendidos entre dos instantes.
     * Reusa la consulta nativa creada en DetalleVentaRepository (orden ASC por
     * cantidad).
     */
    public java.util.List<ProductSalesDTO> productosMenosVendidosEntre(LocalDateTime start, LocalDateTime end,
            Integer limit) {
        if (start == null || end == null)
            throw new IllegalArgumentException("start y end son requeridos");
        Timestamp tsStart = Timestamp.valueOf(start);
        Timestamp tsEnd = Timestamp.valueOf(end);
        java.util.List<Object[]> rows = detalleVentaRepository.findLeastProductSalesBetweenDates(tsStart, tsEnd);
        java.util.List<ProductSalesDTO> result = new java.util.ArrayList<>();
        int count = 0;
        for (Object[] row : rows) {
            if (limit != null && count >= limit)
                break;
            ProductSalesDTO dto = new ProductSalesDTO();
            Object o0 = row[0];
            Object o1 = row[1];
            Object o2 = row[2];
            Object o3 = row[3];
            Integer productoId = null;
            if (o0 instanceof BigInteger)
                productoId = ((BigInteger) o0).intValue();
            else if (o0 instanceof Number)
                productoId = ((Number) o0).intValue();
            dto.setProductoId(productoId);
            dto.setNombre(o1 == null ? null : o1.toString());
            Integer totalCantidad = null;
            if (o2 instanceof BigDecimal)
                totalCantidad = ((BigDecimal) o2).intValue();
            else if (o2 instanceof BigInteger)
                totalCantidad = ((BigInteger) o2).intValue();
            else if (o2 instanceof Number)
                totalCantidad = ((Number) o2).intValue();
            dto.setTotalCantidad(totalCantidad == null ? 0 : totalCantidad);
            Integer totalSubtotal = null;
            if (o3 instanceof BigDecimal)
                totalSubtotal = ((BigDecimal) o3).intValue();
            else if (o3 instanceof BigInteger)
                totalSubtotal = ((BigInteger) o3).intValue();
            else if (o3 instanceof Number)
                totalSubtotal = ((Number) o3).intValue();
            dto.setTotalSubtotal(totalSubtotal == null ? 0 : totalSubtotal);
            result.add(dto);
            count++;
        }
        return result;
    }

    /**
     * Productos menos vendidos en una semana. Misma semántica que
     * `productosMasVendidosPorSemana`.
     */
    public java.util.List<ProductSalesDTO> productosMenosVendidosPorSemana(Integer year, Integer month, Integer week,
            Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        if (month == null) {
            WeekFields wf = WeekFields.ISO;
            int targetYear = (year == null) ? today.get(wf.weekBasedYear()) : year;
            int targetWeek = (week == null) ? today.get(wf.weekOfWeekBasedYear()) : week;

            LocalDate firstDay = LocalDate.now()
                    .with(wf.weekBasedYear(), targetYear)
                    .with(wf.weekOfWeekBasedYear(), targetWeek)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastDay = firstDay.plusDays(6);
            LocalDateTime start = firstDay.atStartOfDay();
            LocalDateTime end = lastDay.atTime(LocalTime.MAX);
            return productosMenosVendidosEntre(start, end, limit);
        } else {
            int y = (year == null) ? today.getYear() : year;
            int m = month;
            if (m < 1 || m > 12)
                throw new IllegalArgumentException("month debe estar entre 1 y 12");
            LocalDate firstOfMonth = LocalDate.of(y, m, 1);
            LocalDate lastOfMonth = firstOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            int maxWeeks = (lastOfMonth.getDayOfMonth() + 6) / 7;
            int wk = (week == null) ? 1 : week;
            if (wk < 1 || wk > maxWeeks)
                throw new IllegalArgumentException("week dentro del mes fuera de rango (1.." + maxWeeks + ")");
            int startDay = 1 + (wk - 1) * 7;
            LocalDate startDate = LocalDate.of(y, m, startDay);
            LocalDate endDate = startDate.plusDays(6);
            if (endDate.isAfter(lastOfMonth))
                endDate = lastOfMonth;
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            return productosMenosVendidosEntre(start, end, limit);
        }
    }

    /**
     * Productos menos vendidos en un mes.
     */
    public java.util.List<ProductSalesDTO> productosMenosVendidosPorMes(Integer year, Integer month, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;
        LocalDate first = LocalDate.of(y, m, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosMenosVendidosEntre(start, end, limit);
    }

    /**
     * Calcula margen por producto entre dos instantes: usa la consulta nativa que
     * suma ingreso y costo histórico y mapea a DTOs con calculos adicionales.
     */
    public java.util.List<com.erp.p03.controllers.dto.ProductMarginDTO> productosMargenEntre(LocalDateTime start,
            LocalDateTime end, Integer limit) {
        if (start == null || end == null)
            throw new IllegalArgumentException("start y end son requeridos");
        // Convertir a Timestamp
        Timestamp tsStart = Timestamp.valueOf(start);
        Timestamp tsEnd = Timestamp.valueOf(end);
        // Consultar repo
        java.util.List<Object[]> rows = detalleVentaRepository.findProductMarginBetweenDates(tsStart, tsEnd);
        java.util.List<com.erp.p03.controllers.dto.ProductMarginDTO> result = new java.util.ArrayList<>();
        int count = 0;
        // Mapear a DTOs
        for (Object[] row : rows) {
            if (limit != null && count >= limit)
                break;
            com.erp.p03.controllers.dto.ProductMarginDTO dto = new com.erp.p03.controllers.dto.ProductMarginDTO();
            Object o0 = row[0]; // productoId
            Object o1 = row[1]; // nombre
            Object o2 = row[2]; // totalCantidad
            Object o3 = row[3]; // totalIngreso
            Object o4 = row[4]; // totalCosto

            Integer productoId = null;
            // Determinar tipo de o0 y convertir a Integer
            if (o0 instanceof java.math.BigInteger)
                productoId = ((java.math.BigInteger) o0).intValue();
            else if (o0 instanceof Number)
                productoId = ((Number) o0).intValue();
            dto.setProductoId(productoId);
            dto.setNombre(o1 == null ? null : o1.toString());

            Integer totalCantidad = null;
            // Determinar tipo de o2 y convertir a Integer
            if (o2 instanceof java.math.BigDecimal)
                totalCantidad = ((java.math.BigDecimal) o2).intValue();
            else if (o2 instanceof java.math.BigInteger)
                totalCantidad = ((java.math.BigInteger) o2).intValue();
            else if (o2 instanceof Number)
                totalCantidad = ((Number) o2).intValue();
            dto.setTotalCantidad(totalCantidad == null ? 0 : totalCantidad);

            Integer totalIngreso = null;
            // Determinar tipo de o3 y convertir a Integer
            if (o3 instanceof java.math.BigDecimal)
                totalIngreso = ((java.math.BigDecimal) o3).intValue();
            else if (o3 instanceof java.math.BigInteger)
                totalIngreso = ((java.math.BigInteger) o3).intValue();
            else if (o3 instanceof Number)
                totalIngreso = ((Number) o3).intValue();
            dto.setTotalIngreso(totalIngreso == null ? 0 : totalIngreso);

            Integer totalCosto = null;
            // Determinar tipo de o4 y convertir a Integer
            if (o4 instanceof java.math.BigDecimal)
                totalCosto = ((java.math.BigDecimal) o4).intValue();
            else if (o4 instanceof java.math.BigInteger)
                totalCosto = ((java.math.BigInteger) o4).intValue();
            else if (o4 instanceof Number)
                totalCosto = ((Number) o4).intValue();
            dto.setTotalCosto(totalCosto == null ? 0 : totalCosto);

            // precio unitario actual (desde producto si existe)
            Integer precioUnitario = null;
            if (dto.getProductoId() != null) {
                java.util.Optional<com.erp.p03.entities.ProductoEntity> pOpt = productoService
                        .findById(dto.getProductoId());
                if (pOpt.isPresent())
                    precioUnitario = pOpt.get().getPrecio();
            }
            dto.setPrecioUnitario(precioUnitario);

            // calculados
            dto.setMargen(dto.getTotalIngreso() - dto.getTotalCosto());
            if (dto.getPrecioUnitario() != null && dto.getPrecioUnitario() > 0) {
                dto.setIngresoSobrePrecio(dto.getTotalIngreso() / (double) dto.getPrecioUnitario());
            } else {
                dto.setIngresoSobrePrecio(null);
            }
            if (dto.getTotalIngreso() != null && dto.getTotalIngreso() > 0) {
                dto.setMargenSobreIngreso(dto.getMargen() / (double) dto.getTotalIngreso());
            } else {
                dto.setMargenSobreIngreso(null);
            }

            // NUEVOS: ingreso por unidad (promedio real) y relación respecto al precio
            // actual
            if (dto.getTotalCantidad() != null && dto.getTotalCantidad() > 0) {
                dto.setIngresoPorUnidad(dto.getTotalIngreso() / (double) dto.getTotalCantidad());
                if (dto.getPrecioUnitario() != null && dto.getPrecioUnitario() > 0) {
                    dto.setIngresoSobrePrecioRelativo(dto.getIngresoPorUnidad() / (double) dto.getPrecioUnitario());
                } else {
                    dto.setIngresoSobrePrecioRelativo(null);
                }
            } else {
                dto.setIngresoPorUnidad(null);
                dto.setIngresoSobrePrecioRelativo(null);
            }

            result.add(dto);
            count++;
        }
        return result;
    }

    // Productos margen en una semana
    public java.util.List<com.erp.p03.controllers.dto.ProductMarginDTO> productosMargenPorSemana(Integer year,
            Integer month, Integer week, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        if (month == null) {
            WeekFields wf = WeekFields.ISO;
            int targetYear = (year == null) ? today.get(wf.weekBasedYear()) : year;
            int targetWeek = (week == null) ? today.get(wf.weekOfWeekBasedYear()) : week;
            // Calcular primer y último día de la semana
            LocalDate firstDay = LocalDate.now()
                    .with(wf.weekBasedYear(), targetYear)
                    .with(wf.weekOfWeekBasedYear(), targetWeek)
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastDay = firstDay.plusDays(6);
            LocalDateTime start = firstDay.atStartOfDay();
            LocalDateTime end = lastDay.atTime(LocalTime.MAX);
            return productosMargenEntre(start, end, limit);
        }
        // Si month es proporcionado, interpretamos week como semana dentro del mes
        else {
            int y = (year == null) ? today.getYear() : year;
            int m = month;
            if (m < 1 || m > 12)
                throw new IllegalArgumentException("month debe estar entre 1 y 12");
            LocalDate firstOfMonth = LocalDate.of(y, m, 1);
            LocalDate lastOfMonth = firstOfMonth.with(TemporalAdjusters.lastDayOfMonth());
            int maxWeeks = (lastOfMonth.getDayOfMonth() + 6) / 7;
            int wk = (week == null) ? 1 : week;
            if (wk < 1 || wk > maxWeeks)
                throw new IllegalArgumentException("week dentro del mes fuera de rango (1.." + maxWeeks + ")");
            int startDay = 1 + (wk - 1) * 7;
            LocalDate startDate = LocalDate.of(y, m, startDay);
            LocalDate endDate = startDate.plusDays(6);
            if (endDate.isAfter(lastOfMonth))
                endDate = lastOfMonth;
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(LocalTime.MAX);
            return productosMargenEntre(start, end, limit);
        }
    }

    // Productos margen en un mes
    public java.util.List<com.erp.p03.controllers.dto.ProductMarginDTO> productosMargenPorMes(Integer year,
            Integer month, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;
        LocalDate first = LocalDate.of(y, m, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosMargenEntre(start, end, limit);
    }

    /**
     * Calcula pérdidas por productos basadas en lotes vencidos entre dos instantes.
     * Se interpreta como la suma de (cantidad_actual_del_lote * costo_del_lote)
     * para los lotes
     * cuya fecha_vencimiento está entre start y end (inclusive).
     * Nota: refleja pérdidas según la cantidad registrada en el lote; no
     * reconstruye históricos.
     */
    public java.util.List<com.erp.p03.controllers.dto.ProductLossDTO> productosPerdidasEntre(LocalDateTime start,
            LocalDateTime end, Integer limit) {
        if (start == null || end == null)
            throw new IllegalArgumentException("start y end son requeridos");
        java.time.LocalDate desde = start.toLocalDate();
        java.time.LocalDate hasta = end.toLocalDate();

        java.util.List<com.erp.p03.entities.LoteEntity> lotes = loteService.findLotesByFechaVencimientoBetween(desde,
                hasta);
        java.util.Map<Integer, com.erp.p03.controllers.dto.ProductLossDTO> map = new java.util.HashMap<>();

        for (com.erp.p03.entities.LoteEntity lote : lotes) {
            if (lote == null)
                continue;
            Integer pid = lote.getProducto() == null ? null : lote.getProducto().getIdProducto();
            if (pid == null)
                continue;
            Integer cantidad = lote.getCantidad() == null ? 0 : lote.getCantidad();
            Integer costo = lote.getCosto() == null ? 0 : lote.getCosto();

            // Si el costo es 0 (modelo de gastos de adquisición), usamos el precio de venta
            // como referencia (lucro cesante)
            int valorUnitario = (costo > 0) ? costo
                    : (lote.getProducto() != null && lote.getProducto().getPrecio() != null
                            ? lote.getProducto().getPrecio()
                            : 0);

            int perdida = cantidad * valorUnitario;

            com.erp.p03.controllers.dto.ProductLossDTO dto = map.get(pid);
            if (dto == null) {
                dto = new com.erp.p03.controllers.dto.ProductLossDTO();
                dto.setProductoId(pid);
                dto.setNombre(lote.getProducto() == null ? null : lote.getProducto().getNombre());
                dto.setTotalCantidadPerdida(0);
                dto.setTotalPerdida(0);
                map.put(pid, dto);
            }
            dto.setTotalCantidadPerdida(dto.getTotalCantidadPerdida() + cantidad);
            dto.setTotalPerdida(dto.getTotalPerdida() + perdida);
        }

        java.util.List<com.erp.p03.controllers.dto.ProductLossDTO> result = new java.util.ArrayList<>(map.values());
        // Ordenar por pérdida descendente (las mayores pérdidas primero)
        result.sort(java.util.Comparator.comparing(com.erp.p03.controllers.dto.ProductLossDTO::getTotalPerdida,
                java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())).reversed());
        if (limit != null && limit > 0 && result.size() > limit)
            return result.subList(0, limit);
        return result;
    }

    public java.util.List<com.erp.p03.controllers.dto.ProductLossDTO> productosPerdidasPorSemana(Integer year,
            Integer month, Integer week, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        if (month == null) {
            java.time.temporal.WeekFields wf = java.time.temporal.WeekFields.ISO;
            int targetYear = (year == null) ? today.get(wf.weekBasedYear()) : year;
            int targetWeek = (week == null) ? today.get(wf.weekOfWeekBasedYear()) : week;

            java.time.LocalDate firstDay = LocalDate.now()
                    .with(wf.weekBasedYear(), targetYear)
                    .with(wf.weekOfWeekBasedYear(), targetWeek)
                    .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            java.time.LocalDate lastDay = firstDay.plusDays(6);
            java.time.LocalDateTime start = firstDay.atStartOfDay();
            java.time.LocalDateTime end = lastDay.atTime(LocalTime.MAX);
            return productosPerdidasEntre(start, end, limit);
        } else {
            int y = (year == null) ? today.getYear() : year;
            int m = month;
            if (m < 1 || m > 12)
                throw new IllegalArgumentException("month debe estar entre 1 y 12");
            java.time.LocalDate firstOfMonth = java.time.LocalDate.of(y, m, 1);
            java.time.LocalDate lastOfMonth = firstOfMonth.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
            int maxWeeks = (lastOfMonth.getDayOfMonth() + 6) / 7;
            int wk = (week == null) ? 1 : week;
            if (wk < 1 || wk > maxWeeks)
                throw new IllegalArgumentException("week dentro del mes fuera de rango (1.." + maxWeeks + ")");
            int startDay = 1 + (wk - 1) * 7;
            java.time.LocalDate startDate = java.time.LocalDate.of(y, m, startDay);
            java.time.LocalDate endDate = startDate.plusDays(6);
            if (endDate.isAfter(lastOfMonth))
                endDate = lastOfMonth;
            java.time.LocalDateTime start = startDate.atStartOfDay();
            java.time.LocalDateTime end = endDate.atTime(LocalTime.MAX);
            return productosPerdidasEntre(start, end, limit);
        }
    }

    // pérdidas en un mes
    public java.util.List<com.erp.p03.controllers.dto.ProductLossDTO> productosPerdidasPorMes(Integer year,
            Integer month, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        int m = (month == null) ? today.getMonthValue() : month;
        java.time.LocalDate first = java.time.LocalDate.of(y, m, 1);
        java.time.LocalDate last = first.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
        java.time.LocalDateTime start = first.atStartOfDay();
        // CLAMP: El fin del reporte de pérdidas no debe exceder "hoy",
        // porque algo que vence mañana no es pérdida hoy.
        if (last.isAfter(today)) {
            last = today;
        }
        java.time.LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosPerdidasEntre(start, end, limit);
    }

    // ================== Resumen Financiero Integrado ==================

    public com.erp.p03.controllers.dto.FinanceSummaryDTO getFinanceSummary(LocalDateTime start, LocalDateTime end) {
        // 1. Obtener Margen de Productos (Ingresos y Costos de Ventas)
        // Usamos productosMargenEntre para obtener ingresos y costos de productos
        // vendidos
        List<com.erp.p03.controllers.dto.ProductMarginDTO> margins = productosMargenEntre(start, end, null);

        long totalIngresos = 0;
        long totalCostoProductos = 0;

        for (com.erp.p03.controllers.dto.ProductMarginDTO m : margins) {
            totalIngresos += (m.getTotalIngreso() != null ? m.getTotalIngreso() : 0);
            totalCostoProductos += (m.getTotalCosto() != null ? m.getTotalCosto() : 0);
        }

        // 2. Obtener Gastos del periodo
        List<com.erp.p03.entities.GastoEntity> gastos = gastoService.listarPorRangoFecha(
                java.sql.Date.valueOf(start.toLocalDate()),
                java.sql.Date.valueOf(end.toLocalDate()));

        long gastosAdquisicion = 0;
        long gastosOperacionales = 0; // Incluye OPERACIONAL y OTROS

        for (com.erp.p03.entities.GastoEntity g : gastos) {
            if ("ADQUISICION".equalsIgnoreCase(g.getTipo())) {
                gastosAdquisicion += g.getMonto();
            } else {
                // OPERACIONAL y OTROS (y cualquier otro tipo futuro) van a
                // Operacionales/General
                gastosOperacionales += g.getMonto();
            }
        }

        // 3. Calcular Totales
        // Utilidad Bruta = Ingresos por Venta - Costos de Productos Vendidos - Gastos
        // Adquisición (Mercadería extra)
        long utilidadBruta = totalIngresos - totalCostoProductos - gastosAdquisicion;

        // Utilidad Neta = Utilidad Bruta - Gastos Operacionales
        long utilidadNeta = utilidadBruta - gastosOperacionales;

        // Margen Neto %
        double margenPorcentaje = (totalIngresos > 0) ? ((double) utilidadNeta / totalIngresos) * 100 : 0.0;

        com.erp.p03.controllers.dto.FinanceSummaryDTO summary = new com.erp.p03.controllers.dto.FinanceSummaryDTO();
        summary.setTotalIngresos(totalIngresos);
        summary.setTotalCostoProductos(totalCostoProductos);
        summary.setGastosAdquisicion(gastosAdquisicion);
        summary.setGastosOperacionales(gastosOperacionales);
        summary.setUtilidadBruta(utilidadBruta);
        summary.setUtilidadNeta(utilidadNeta);
        summary.setMargenPorcentaje(Math.round(margenPorcentaje * 100.0) / 100.0);

        return summary;
    }

    /**
     * Productos más vendidos en un año.
     */
    public java.util.List<ProductSalesDTO> productosMasVendidosPorAnio(Integer year, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        LocalDate first = LocalDate.of(y, 1, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfYear());
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosMasVendidosEntre(start, end, limit);
    }

    /**
     * Productos menos vendidos en un año.
     */
    public java.util.List<ProductSalesDTO> productosMenosVendidosPorAnio(Integer year, Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        LocalDate first = LocalDate.of(y, 1, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfYear());
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosMenosVendidosEntre(start, end, limit);
    }

    /**
     * Margen por producto en un año.
     */
    public java.util.List<com.erp.p03.controllers.dto.ProductMarginDTO> productosMargenPorAnio(Integer year,
            Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        LocalDate first = LocalDate.of(y, 1, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfYear());
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosMargenEntre(start, end, limit);
    }

    /**
     * Pérdidas por productos (lotes vencidos) en un año.
     */
    public java.util.List<com.erp.p03.controllers.dto.ProductLossDTO> productosPerdidasPorAnio(Integer year,
            Integer limit) {
        java.time.LocalDate today = java.time.LocalDate.now();
        int y = (year == null) ? today.getYear() : year;
        LocalDate first = LocalDate.of(y, 1, 1);
        LocalDate last = first.with(TemporalAdjusters.lastDayOfYear());
        // CLAMP: No reportar pérdidas futuras. Si el año es el actual, cortar en "hoy".
        if (last.isAfter(today)) {
            last = today;
        }
        LocalDateTime start = first.atStartOfDay();
        LocalDateTime end = last.atTime(LocalTime.MAX);
        return productosPerdidasEntre(start, end, limit);
    }
}
