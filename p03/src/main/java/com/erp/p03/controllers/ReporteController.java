package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.erp.p03.services.VentaService;
import com.erp.p03.controllers.dto.ProductSalesDTO;
import com.erp.p03.controllers.dto.ProductMarginDTO;

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
     * - month: mes (1-12) — si se pasa, `week` se interpreta como índice dentro del mes
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
     * Productos menos vendidos en una semana. Misma query y params que `/productos/semana`.
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
     * Productos menos vendidos en un mes. Misma query y params que `/productos/mes`.
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
     * Margen por producto en una semana. Misma query y params que `/productos/semana`.
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

}
