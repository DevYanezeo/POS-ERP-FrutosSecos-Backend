package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.erp.p03.services.VentaService;
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

}
