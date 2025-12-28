package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import com.erp.p03.services.VentaService;
import com.erp.p03.controllers.dto.ProductSalesDTO;
import com.erp.p03.controllers.dto.ProductMarginDTO;
import com.erp.p03.controllers.dto.ProductLossDTO;
import com.erp.p03.controllers.dto.FinanceSummaryDTO;
import com.erp.p03.controllers.dto.ExpiredLotDTO;
import com.erp.p03.services.ReporteExcelService;

import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("api/reportes")
@CrossOrigin("*")
public class ReporteController {

    private final ReporteExcelService excelService;
    private final VentaService ventaService;

    public ReporteController(VentaService ventaService, ReporteExcelService excelService) {
        this.ventaService = ventaService;
        this.excelService = excelService;
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
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMasVendidosPorSemana(year, month, week, day, limit);
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

    @GetMapping("/productos/dia")
    public ResponseEntity<List<ProductSalesDTO>> productosMasVendidosPorDia(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMasVendidosPorDia(year, month, day, limit);
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
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMenosVendidosPorSemana(year, month, week, day, limit);
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

    @GetMapping("/productos/dia/menos")
    public ResponseEntity<List<ProductSalesDTO>> productosMenosVendidosPorDia(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductSalesDTO> list = ventaService.productosMenosVendidosPorDia(year, month, day, limit);
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
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductMarginDTO> list = ventaService.productosMargenPorSemana(year, month, week, day, limit);
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

    @GetMapping("/productos/margen/dia")
    public ResponseEntity<List<ProductMarginDTO>> productosMargenPorDia(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductMarginDTO> list = ventaService.productosMargenPorDia(year, month, day, limit);
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
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductLossDTO> list = ventaService.productosPerdidasPorSemana(year, month, week, day, limit);
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

    @GetMapping("/productos/perdidas/dia")
    public ResponseEntity<List<ProductLossDTO>> productosPerdidasPorDia(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day,
            @RequestParam(required = false) Integer limit) {
        List<ProductLossDTO> list = ventaService.productosPerdidasPorDia(year, month, day, limit);
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
    /**
     * Resumen Financiero Completo (Ingresos vs Costos vs Gastos)
     */
    @GetMapping("/finanzas/resumen/{periodo}")
    public ResponseEntity<FinanceSummaryDTO> getResumenFinanciero(
            @PathVariable String periodo,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day) {
        LocalDateTime start;
        LocalDateTime end;

        LocalDate today = LocalDate.now();

        if ("anio".equalsIgnoreCase(periodo) || "ano".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            LocalDate first = LocalDate.of(y, 1, 1);
            LocalDate last = first.with(TemporalAdjusters.lastDayOfYear());
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
        } else if ("mes".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            int m = (month == null) ? today.getMonthValue() : month;
            LocalDate first = LocalDate.of(y, m, 1);
            LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
        } else if ("dia".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            int m = (month == null) ? today.getMonthValue() : month;
            int d = (day == null) ? today.getDayOfMonth() : day;
            LocalDate date = LocalDate.of(y, m, d);
            start = date.atStartOfDay();
            end = date.atTime(LocalTime.MAX);
        } else {
            // Semana
            if (year != null && month != null && day != null) {
                int y = year;
                int m = month;
                int wk = (day - 1) / 7 + 1;
                if (wk > 5)
                    wk = 5;

                LocalDate firstOfMonth = LocalDate.of(y, m, 1);
                LocalDate lastOfMonth = firstOfMonth
                        .with(TemporalAdjusters.lastDayOfMonth());
                int maxWeeks = (lastOfMonth.getDayOfMonth() + 6) / 7;

                if (wk > maxWeeks)
                    wk = 1;

                int startDay = 1 + (wk - 1) * 7;
                LocalDate startDate = LocalDate.of(y, m, startDay);
                LocalDate endDate = startDate.plusDays(6);

                if (endDate.isAfter(lastOfMonth)) {
                    endDate = lastOfMonth;
                }

                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
            } else {
                LocalDate first = today
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate last = first.plusDays(6);
                start = first.atStartOfDay();
                end = last.atTime(LocalTime.MAX);
            }
        }

        FinanceSummaryDTO summary = ventaService.getFinanceSummary(start, end);
        return ResponseEntity.ok(summary);
    }

    /**
     * Detalle de pérdidas por vencimiento (lista de lotes)
     */
    @GetMapping("/productos/perdidas/detalle/{periodo}")
    public ResponseEntity<List<ExpiredLotDTO>> getDetallePerdidas(
            @PathVariable String periodo,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer day) {
        LocalDateTime start;
        LocalDateTime end;

        LocalDate today = LocalDate.now();

        if ("anio".equalsIgnoreCase(periodo) || "ano".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            LocalDate first = LocalDate.of(y, 1, 1);
            LocalDate last = first.with(TemporalAdjusters.lastDayOfYear());
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
        } else if ("mes".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            int m = (month == null) ? today.getMonthValue() : month;
            LocalDate first = LocalDate.of(y, m, 1);
            LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
        } else if ("dia".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            int m = (month == null) ? today.getMonthValue() : month;
            int d = (day == null) ? today.getDayOfMonth() : day;
            LocalDate date = LocalDate.of(y, m, d);
            start = date.atStartOfDay();
            end = date.atTime(LocalTime.MAX);
        } else {
            // Semana
            if (year != null && month != null && day != null) {
                int y = year;
                int m = month;
                int wk = (day - 1) / 7 + 1;
                if (wk > 5)
                    wk = 5;

                LocalDate firstOfMonth = LocalDate.of(y, m, 1);
                LocalDate lastOfMonth = firstOfMonth
                        .with(TemporalAdjusters.lastDayOfMonth());
                int maxWeeks = (lastOfMonth.getDayOfMonth() + 6) / 7;

                if (wk > maxWeeks)
                    wk = 1;

                int startDay = 1 + (wk - 1) * 7;
                LocalDate startDate = LocalDate.of(y, m, startDay);
                LocalDate endDate = startDate.plusDays(6);

                if (endDate.isAfter(lastOfMonth)) {
                    endDate = lastOfMonth;
                }

                start = startDate.atStartOfDay();
                end = endDate.atTime(LocalTime.MAX);
            } else {
                LocalDate first = today
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate last = first.plusDays(6);
                start = first.atStartOfDay();
                end = last.atTime(LocalTime.MAX);
            }
        }

        // Ajuste: Para pérdidas por vencimiento, no debemos proyectar pérdidas futuras.
        LocalDateTime now = LocalDateTime.now();
        if (end.isAfter(now)) {
            end = now;
        }

        List<ExpiredLotDTO> list = ventaService.getDetallePerdidasEntre(start, end);
        return ResponseEntity.ok(list);
    }

    /**
     * Exportar reporte financiero a Excel.
     */
    @GetMapping("/exportar/{periodo}")
    public ResponseEntity<byte[]> exportarReporteExcel(
            @PathVariable String periodo,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {

        LocalDateTime start;
        LocalDateTime end;
        LocalDate today = LocalDate.now();
        String title = "Reporte Financiero";

        if ("anio".equalsIgnoreCase(periodo) || "ano".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            LocalDate first = LocalDate.of(y, 1, 1);
            LocalDate last = first.with(TemporalAdjusters.lastDayOfYear());
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
            title = "Reporte Anual " + y;
        } else if ("mes".equalsIgnoreCase(periodo)) {
            int y = (year == null) ? today.getYear() : year;
            int m = (month == null) ? today.getMonthValue() : month;
            LocalDate first = LocalDate.of(y, m, 1);
            LocalDate last = first.with(TemporalAdjusters.lastDayOfMonth());
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
            title = "Reporte Mensual " + m + "/" + y;
        } else {
            return ResponseEntity.badRequest().body(null);
        }

        try {
            FinanceSummaryDTO resumen = ventaService.getFinanceSummary(start, end);
            List<ProductSalesDTO> topVentas;

            if ("mes".equalsIgnoreCase(periodo)) {
                topVentas = ventaService.productosMasVendidosPorMes(start.getYear(), start.getMonthValue(), 20);
            } else {
                topVentas = ventaService.productosMasVendidosPorAnio(start.getYear(), 20);
            }

            List<ProductLossDTO> topPerdidas = ventaService.productosPerdidasEntre(start, end, 20);

            byte[] content = excelService.generarReporteExcel(title, resumen, topVentas, topPerdidas);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_" + periodo + ".xlsx\"");

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
