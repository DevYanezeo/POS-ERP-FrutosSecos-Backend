package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.erp.p03.services.VentaService;
import com.erp.p03.controllers.dto.ProductSalesDTO;

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
     * - week: semana del año (1-52)
     * - month: mes (1-12)
     * - limit: cantidad máxima de resultados
     */
    @GetMapping("/productos/semana")
    public ResponseEntity<List<ProductSalesDTO>> productosMasVendidosPorSemana(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week,
            @RequestParam(required = false) Integer month,
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

}
