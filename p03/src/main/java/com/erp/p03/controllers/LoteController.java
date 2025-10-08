package com.erp.p03.controllers;


import com.erp.p03.controllers.dto.ProductoLoteVencimientoDTO;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.services.LoteService;
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
}
