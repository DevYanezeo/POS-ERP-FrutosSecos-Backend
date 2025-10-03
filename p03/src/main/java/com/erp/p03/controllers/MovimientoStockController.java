package com.erp.p03.controllers;

import com.erp.p03.controllers.dto.MovimientoStockRequest;
import com.erp.p03.entities.MovimientoStockEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.p03.services.MovimientoStockService;

@RestController
@RequestMapping("api/movimientos-stock")
@CrossOrigin("*")
public class MovimientoStockController {

    private final MovimientoStockService movimientoStockService;

    public MovimientoStockController(MovimientoStockService movimientoStockService) {
        this.movimientoStockService = movimientoStockService;
    }

    // Endpoint para registrar un movimiento de stock
    @PostMapping("/registrar")
    public ResponseEntity<MovimientoStockEntity> registrarMovimiento(@RequestBody MovimientoStockRequest request) {
        try {
            // Llama al servicio para registrar el movimiento y actualizar el stock
            MovimientoStockEntity movimiento = movimientoStockService.registrarMovimiento(request);
            return ResponseEntity.ok(movimiento);
        } catch (RuntimeException e) {
            // Si hay error (stock insuficiente, tipo no soportado, etc.)
            return ResponseEntity.badRequest().build();
        }
    }

}
