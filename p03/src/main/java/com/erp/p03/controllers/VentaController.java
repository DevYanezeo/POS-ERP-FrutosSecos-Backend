package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.erp.p03.services.VentaService;
import com.erp.p03.controllers.dto.VentaWithHolidayDTO;
import com.erp.p03.controllers.dto.VentaRequest;
import com.erp.p03.entities.VentaEntity;
import java.util.List;

@RestController
@RequestMapping("api/ventas")
@CrossOrigin("*")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<VentaEntity> listar() {
        return ventaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaEntity> obtener(@PathVariable Integer id) {
        VentaEntity v = ventaService.findById(id);
        if (v == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(v);
    }

    @PostMapping("/confirmar")
    public ResponseEntity<VentaEntity> confirmarVenta(@RequestBody VentaRequest request) {
        VentaEntity v = ventaService.confirmarVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(v);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        ventaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina un detalle (producto) de una venta activa y revierte el stock.
     * Ruta: DELETE /api/ventas/{ventaId}/detalles/{detalleId}?idLote=123&usuarioId=5
     * - idLote: (opcional) si se conoce el lote original, la cantidad se suma de vuelta a ese lote.
     * - usuarioId: (opcional pero recomendado) id del usuario que realiza la operación (para movimientos de stock).
     * Devuelve la venta actualizada.
     */
    @DeleteMapping("/{ventaId}/detalles/{detalleId}")
    public ResponseEntity<VentaEntity> quitarProductoDeVenta(
            @PathVariable Integer ventaId,
            @PathVariable Integer detalleId,
            @RequestParam(required = false) Integer idLote,
            @RequestParam(required = false) Integer usuarioId) {
        VentaEntity updated = ventaService.quitarProductoDeVenta(ventaId, detalleId, idLote, usuarioId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Convenience: eliminar detalle indicando productoId en lugar de idDetalle.
     * DELETE /api/ventas/{ventaId}/detalles/producto/{productoId}?idLote=123&usuarioId=5
     */
    @DeleteMapping("/{ventaId}/detalles/producto/{productoId}")
    public ResponseEntity<VentaEntity> quitarProductoPorProducto(
            @PathVariable Integer ventaId,
            @PathVariable Integer productoId,
            @RequestParam(required = false) Integer idLote,
            @RequestParam(required = false) Integer usuarioId) {
        VentaEntity updated = ventaService.quitarProductoDeVentaPorProducto(ventaId, productoId, idLote, usuarioId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Historial de ventas: permite filtrar opcionalmente por rango de fechas y por usuario.
     */
    @GetMapping("/historial")
    public ResponseEntity<List<VentaEntity>> historial(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) Integer usuarioId) {
        List<VentaEntity> lista = ventaService.historialVentas(desde, hasta, usuarioId);
        return ResponseEntity.ok(lista);
    }

    /**
     * Historial con feriados: devuelve DTOs que incluyen `isHoliday` y `holidayName`.
     * GET /api/ventas/historial/feriados?desde=dd/MM/yyyy&hasta=dd/MM/yyyy&usuarioId=1
     */
    @GetMapping("/historial/feriados")
    public ResponseEntity<List<VentaWithHolidayDTO>> historialConFeriados(
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) Integer usuarioId) {
        List<VentaWithHolidayDTO> lista = ventaService.historialVentasConFeriados(desde, hasta, usuarioId);
        return ResponseEntity.ok(lista);
    }

}
