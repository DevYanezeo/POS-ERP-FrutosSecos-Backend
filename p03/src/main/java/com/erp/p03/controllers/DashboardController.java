package com.erp.p03.controllers;

import com.erp.p03.controllers.dto.DashboardSummaryDTO;
import com.erp.p03.entities.DetalleVentaEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.entities.VentaEntity;
import com.erp.p03.repositories.DetalleVentaRepository;
import com.erp.p03.repositories.ProductoRepository;
import com.erp.p03.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate yesterday = today.minusDays(1);

        // 1. Ventas de Hoy
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        List<VentaEntity> ventasHoy = ventaRepository.findByFechaBetween(startOfToday, endOfToday);

        int totalVentasHoy = ventasHoy.stream().mapToInt(VentaEntity::getTotal).sum();
        summary.setVentasHoy(totalVentasHoy);
        summary.setTransaccionesHoy(ventasHoy.size());

        // 2. Ventas de Ayer (para porcentaje)
        LocalDateTime startOfYesterday = yesterday.atStartOfDay();
        LocalDateTime endOfYesterday = yesterday.atTime(LocalTime.MAX);
        List<VentaEntity> ventasAyer = ventaRepository.findByFechaBetween(startOfYesterday, endOfYesterday);

        int totalVentasAyer = ventasAyer.stream().mapToInt(VentaEntity::getTotal).sum();

        if (totalVentasAyer > 0) {
            double change = ((double) (totalVentasHoy - totalVentasAyer) / totalVentasAyer) * 100;
            summary.setPorcentajeVentasAyer(change);
        } else {
            summary.setPorcentajeVentasAyer(totalVentasHoy > 0 ? 100.0 : 0.0);
        }

        // 3. Productos Vendidos y Unicos
        int productosVendidos = 0;
        Set<Integer> uniqueProducts = new HashSet<>();

        for (VentaEntity venta : ventasHoy) {
            // Fetch contents for each sale
            List<DetalleVentaEntity> detalles = detalleVentaRepository.findByVentaId(venta.getIdVenta());
            for (DetalleVentaEntity detalle : detalles) {
                productosVendidos += detalle.getCantidad();
                uniqueProducts.add(detalle.getProductoId());
            }
        }
        summary.setProductosVendidos(productosVendidos);
        summary.setProductosUnicos(uniqueProducts.size());

        // 4. Venta Promedio
        if (!ventasHoy.isEmpty()) {
            summary.setVentaPromedio(totalVentasHoy / ventasHoy.size());
        } else {
            summary.setVentaPromedio(0);
        }

        // 5. Stock Total y Alertas
        List<ProductoEntity> productos = productoRepository.findAll();
        int stockTotal = productos.stream().mapToInt(p -> p.getStock() != null ? p.getStock() : 0).sum();
        // Assuming low stock is <= 20, ideally configurable
        int alertasStock = (int) productos.stream().filter(p -> p.getStock() != null && p.getStock() <= 20).count();

        summary.setStockTotal(stockTotal);
        summary.setAlertasStock(alertasStock);

        return ResponseEntity.ok(summary);
    }
}
