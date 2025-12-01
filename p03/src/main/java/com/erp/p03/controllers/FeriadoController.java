package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.erp.p03.services.FeriadoService;
import com.erp.p03.entities.FeriadoEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("api/feriados")
@CrossOrigin("*")
public class FeriadoController {

    private final FeriadoService feriadoService;

    public FeriadoController(FeriadoService feriadoService) {
        this.feriadoService = feriadoService;
    }

    @GetMapping
    public List<FeriadoEntity> listar() {
        return feriadoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeriadoEntity> obtener(@PathVariable Integer id) {
        return feriadoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Devuelve feriados en un rango. Acepta fechas en formato DD/MM/AAAA (ej. 20/11/2025)
     * para mantener consistencia con los endpoints de ventas del proyecto.
     */
    @GetMapping("/rango")
    public ResponseEntity<List<FeriadoEntity>> porRango(@RequestParam String desde, @RequestParam String hasta) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d/M/uuuu");
        LocalDate d;
        LocalDate h;
        try {
            d = LocalDate.parse(desde, f);
            h = LocalDate.parse(hasta, f);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        List<FeriadoEntity> lista = feriadoService.findByFechaBetween(d, h);
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<FeriadoEntity> crear(@RequestBody FeriadoEntity body) {
        FeriadoEntity created = feriadoService.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FeriadoEntity> actualizar(@PathVariable Integer id, @RequestBody FeriadoEntity patch) {
        try {
            FeriadoEntity updated = feriadoService.update(id, patch);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        feriadoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

