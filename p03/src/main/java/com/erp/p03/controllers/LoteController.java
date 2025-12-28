package com.erp.p03.controllers;

import com.erp.p03.controllers.dto.ProductoLoteVencimientoDTO;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.services.LoteService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("api/lote")
@CrossOrigin("*")
public class LoteController {

    private static final Logger logger = LoggerFactory.getLogger(LoteController.class);

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    // ✅ Crear lote
    @PostMapping("/crear")
    public ResponseEntity<LoteEntity> crearLote(@RequestBody LoteEntity lote) {
        try {
            LoteEntity saved = loteService.crearLote(lote);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            logger.error("Error al crear lote ({}): {}", e.getClass().getSimpleName(), e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    // ✅ Listar lotes por producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<LoteEntity>> listarPorProducto(@PathVariable Integer productoId) {
        List<LoteEntity> lotes = loteService.listarLotesPorProducto(productoId);
        return ResponseEntity.ok(lotes);
    }

    // ✅ Buscar lotes próximos a vencer
    @GetMapping("/vencimiento")
    public ResponseEntity<List<LoteEntity>> vencimientosProximos(@RequestParam(defaultValue = "30") int dias) {
        List<LoteEntity> proximos = loteService.findLotesVencimientoProximo(dias);
        return ResponseEntity.ok(proximos);
    }

    // ✅ Alertas de vencimiento (DTO)
    @GetMapping("/alertas")
    public ResponseEntity<List<ProductoLoteVencimientoDTO>> alertasVencimiento(
            @RequestParam(defaultValue = "30") int dias) {
        List<ProductoLoteVencimientoDTO> alertas = loteService.findLotesVencimientoProximoDTO(dias);
        return ResponseEntity.ok(alertas);
    }

    // ✅ Modificar fecha de vencimiento
    @PatchMapping("/{id}/fecha-vencimiento")
    public ResponseEntity<LoteEntity> modificarFechaVencimiento(
            @PathVariable int id,
            @RequestBody FechaVencimientoRequest request) {
        try {
            LoteEntity actualizado = loteService.updateFechaVencimiento(id, request.getFechaVencimiento());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @Setter
    public static class FechaVencimientoRequest {
        private java.time.LocalDate fechaVencimiento;
    }

    // ✅ Modificar estado
    @PatchMapping("/{id}/estado")
    public ResponseEntity<LoteEntity> modificarEstadoLote(
            @PathVariable int id,
            @RequestBody EstadoLoteRequest request) {
        try {
            LoteEntity actualizado = loteService.updateEstadoLote(id, request.getEstado());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @Setter
    public static class EstadoLoteRequest {
        private Boolean estado;
    }

    // ✅ Nuevo: Modificar cantidad
    @PatchMapping("/{id}/cantidad")
    public ResponseEntity<LoteEntity> modificarCantidadLote(
            @PathVariable int id,
            @RequestBody CantidadLoteRequest request) {
        try {
            LoteEntity actualizado = loteService.updateCantidadLote(id, request.getCantidad());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @Setter
    public static class CantidadLoteRequest {
        private Integer cantidad;
    }

    // ✅ Nuevo: Modificar costo
    @PatchMapping("/{id}/costo")
    public ResponseEntity<LoteEntity> modificarCostoLote(
            @PathVariable int id,
            @RequestBody CostoLoteRequest request) {
        try {
            LoteEntity actualizado = loteService.updateCostoLote(id, request.getCosto());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Getter
    @Setter
    public static class CostoLoteRequest {
        private Integer costo;
    }

    // ✅ Buscar lote por codigo (para escaner)
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<LoteEntity> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            LoteEntity lote = loteService.findByCodigoLote(codigo);
            return ResponseEntity.ok(lote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all-codigos")
    public ResponseEntity<List<String>> obtenerTodosCodigos() {
        List<String> codigos = loteService.findAllCodigosLote();
        return ResponseEntity.ok(codigos);
    }

}
