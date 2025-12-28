package com.erp.p03.controllers;

import com.erp.p03.entities.GastoEntity;
import com.erp.p03.services.GastoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@CrossOrigin(origins = "*") // Permitir peticiones desde Frontend
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @GetMapping
    public List<GastoEntity> listarGastos() {
        return gastoService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<GastoEntity> registrarGasto(@RequestBody GastoEntity gasto) {
        return ResponseEntity.ok(gastoService.guardarGasto(gasto));
    }

    @GetMapping("/rango")
    public List<GastoEntity> listarPorRango(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date inicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fin) {
        return gastoService.listarPorRangoFecha(inicio, fin);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {
        gastoService.eliminarGasto(id);
        return ResponseEntity.noContent().build();
    }
}
