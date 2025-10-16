package com.erp.p03.controllers;


import com.erp.p03.controllers.dto.ProductoLoteVencimientoDTO;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.services.LoteService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/lote")
@CrossOrigin("*")
public class LoteController {
    private final LoteService loteService;

    public LoteController(LoteService loteService) {
         this.loteService = loteService;
     }

    @PostMapping("/crear")
    public ResponseEntity<LoteEntity> crearLote(@RequestBody LoteEntity lote) {
        try {
            LoteEntity saved = loteService.crearLote(lote);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<LoteEntity>> listarPorProducto(@PathVariable Integer productoId) {
        List<LoteEntity> lotes = loteService.listarLotesPorProducto(productoId);
        return ResponseEntity.ok(lotes);
    }

    @GetMapping("/vencimiento")
    public ResponseEntity<List<LoteEntity>> vencimientosProximos(@RequestParam(defaultValue = "30") int dias) {
        List<LoteEntity> proximos = loteService.findLotesVencimientoProximo(dias);
        return ResponseEntity.ok(proximos);
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<ProductoLoteVencimientoDTO>> alertasVencimiento(@RequestParam(defaultValue = "30") int dias) {
        List<ProductoLoteVencimientoDTO> alertas = loteService.findLotesVencimientoProximoDTO(dias);
        return ResponseEntity.ok(alertas);
    }

    // Modifica la fecha de vencimiento de un lote existente
    @PatchMapping("/{id}/fecha-vencimiento")
    public ResponseEntity<LoteEntity> modificarFechaVencimiento(@PathVariable int id, @RequestBody FechaVencimientoRequest request) {
        // Llama al servicio para actualizar la fecha
        try {
            LoteEntity actualizado = loteService.updateFechaVencimiento(id, request.getFechaVencimiento());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Getter-Setter para recibir la nueva fecha de vencimiento
    @Getter @Setter
    public static class FechaVencimientoRequest {
        private java.time.LocalDate fechaVencimiento;
    }

    //Modifica el estado (activo/inactivo en booleano) de un lote existente

    @PatchMapping("/{id}/estado")
    public ResponseEntity<LoteEntity> modificarEstadoLote(@PathVariable int id, @RequestBody EstadoLoteRequest request) {
        try {
            LoteEntity actualizado = loteService.updateEstadoLote(id, request.getEstado());
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Getter-Setter para recibir el nuevo estado del Lote
    @Getter @Setter
    public static class EstadoLoteRequest {
        private Boolean estado;
    }
}
