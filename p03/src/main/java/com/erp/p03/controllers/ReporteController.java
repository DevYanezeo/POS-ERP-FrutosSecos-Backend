package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.erp.p03.services.VentaService;
import com.erp.p03.controllers.dto.ProductSalesDTO;
import com.erp.p03.controllers.dto.ProductMarginDTO;
import com.erp.p03.controllers.dto.ProductLossDTO;
import com.erp.p03.controllers.dto.FinanceSummaryDTO;

import java.util.List;

@RestController
@RequestMapping("api/reportes")
@CrossOrigin("*")
public class ReporteController {

    private final VentaService ventaService;

    public ReporteController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    /**
     * Productos más vendidos en una semana.
     * Query params opcionales:
     * - year: año (ej. 2025)
     * - month: mes (1-12) — si se pasa, `week` se interpreta como índice dentro del
     * mes
     * - week: semana del año (1-52) o índice dentro del mes
     * - limit: cantidad máxima de resultados
     */
    @GetMapping("/productos/semana")
    public ResponseEntity<List<ProductSalesDTO>> productosMasVendidosPorSemana(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMasVendidosPorSemana(year, month, week, limit);
        return ResponseEntity.ok(list);
    }

    /**
     * Productos más vendidos en un mes.
     * Query params opcionales:
     * - year: año (ej. 2025)
     * - month: mes (1-12)
     * - limit: cantidad máxima de resultados
     */
    @GetMapping("/productos/mes")
    public ResponseEntity<List<ProductSalesDTO>> productosMasVendidosPorMes(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMasVendidosPorMes(year, month, limit);
        return ResponseEntity.ok(list);
    }

    // ================= endpoints para productos menos vendidos =================

    /**
     * Productos menos vendidos en una semana. Misma query y params que
     * `/productos/semana`.
     */
    @GetMapping("/productos/semana/menos")
    public ResponseEntity<List<ProductSalesDTO>> productosMenosVendidosPorSemana(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMenosVendidosPorSemana(year, month, week, limit);
        return ResponseEntity.ok(list);
    }

    /**
     * Productos menos vendidos en un mes. Misma query y params que
     * `/productos/mes`.
     */
    @GetMapping("/productos/mes/menos")
    public ResponseEntity<List<ProductSalesDTO>> productosMenosVendidosPorMes(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMenosVendidosPorMes(year, month, limit);
        return ResponseEntity.ok(list);
    }

    // ================ endpoints para margen de ganancias ================

    /**
     * Margen por producto en una semana. Misma query y params que
     * `/productos/semana`.
     */
    @GetMapping("/productos/margen/semana")
    public ResponseEntity<List<ProductMarginDTO>> productosMargenPorSemana(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer limit) {
        List<ProductMarginDTO> list = ventaService.productosMargenPorSemana(year, month, week, limit);
        return ResponseEntity.ok(list);
    }

    /**
     * Margen por producto en un mes.
     */
    @GetMapping("/productos/margen/mes")
    public ResponseEntity<List<ProductMarginDTO>> productosMargenPorMes(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer limit) {
        List<ProductMarginDTO> list = ventaService.productosMargenPorMes(year, month, limit);
        return ResponseEntity.ok(list);
    }

    // ================ endpoints para pérdidas por lotes vencidos ================

    /**
     * Pérdidas por productos (lotes vencidos) en una semana.
     */
    @GetMapping("/productos/perdidas/semana")
    public ResponseEntity<List<ProductLossDTO>> productosPerdidasPorSemana(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer limit) {
        List<ProductLossDTO> list = ventaService.productosPerdidasPorSemana(year, month, week, limit);
        return ResponseEntity.ok(list);
    }

    /**
     * Pérdidas por productos (lotes vencidos) en un mes.
     */
    @GetMapping("/productos/perdidas/mes")
    public ResponseEntity<List<ProductLossDTO>> productosPerdidasPorMes(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer limit) {
        List<ProductLossDTO> list = ventaService.productosPerdidasPorMes(year, month, limit);
        return ResponseEntity.ok(list);
    }

    // ================= endpoints para reportes anuales =================

    @GetMapping("/productos/anio")
    public ResponseEntity<List<ProductSalesDTO>> productosMasVendidosPorAnio(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMasVendidosPorAnio(year, limit);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/productos/anio/menos")
    public ResponseEntity<List<ProductSalesDTO>> productosMenosVendidosPorAnio(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMenosVendidosPorAnio(year, limit);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/productos/margen/anio")
    public ResponseEntity<List<ProductMarginDTO>> productosMargenPorAnio(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer limit) {
        List<ProductMarginDTO> list = ventaService.productosMargenPorAnio(year, limit);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/productos/perdidas/anio")
    public ResponseEntity<List<ProductLossDTO>> productosPerdidasPorAnio(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer limit) {
        List<ProductLossDTO> list = ventaService.productosPerdidasPorAnio(year, limit);
        return ResponseEntity.ok(list);
    }

    /**
     * Resumen Financiero Completo (Ingresos vs Costos vs Gastos)
     */
    @GetMapping("/finanzas/resumen/{periodo}")
    public ResponseEntity<FinanceSummaryDTO> getResumenFinanciero(@PathVariable String periodo) {
        java.time.LocalDateTime start;
        java.time.LocalDateTime end;

        java.time.LocalDate today = java.time.LocalDate.now();

        if ("anio".equalsIgnoreCase(periodo) || "ano".equalsIgnoreCase(periodo)) {
            java.time.LocalDate first = today.with(java.time.temporal.TemporalAdjusters.firstDayOfYear());
            java.time.LocalDate last = today.with(java.time.temporal.TemporalAdjusters.lastDayOfYear());
            start = first.atStartOfDay();
            end = last.atTime(java.time.LocalTime.MAX);
        } else if ("mes".equalsIgnoreCase(periodo)) {
            java.time.LocalDate first = today.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
            java.time.LocalDate last = today.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
            start = first.atStartOfDay();
            end = last.atTime(java.time.LocalTime.MAX);
        } else {
            // Default: Semana actual
            java.time.LocalDate first = today
                    .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            // Asegurar que cubre hasta el domingo
            java.time.LocalDate last = first.plusDays(6);
            start = first.atStartOfDay();
            end = last.atTime(java.time.LocalTime.MAX);
        }

        FinanceSummaryDTO summary = ventaService.getFinanceSummary(start, end);
        return ResponseEntity.ok(summary);
    }
}
