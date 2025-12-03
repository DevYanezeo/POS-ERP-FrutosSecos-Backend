package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.erp.p03.services.VentaService;
import com.erp.p03.controllers.dto.FiadoUpdateRequest;
import com.erp.p03.controllers.dto.ClienteFiadoDTO;
import com.erp.p03.entities.VentaEntity;

import java.util.List;

@RestController
@RequestMapping("api/fiados")
@CrossOrigin("*")
public class FiadoController {

    private final VentaService ventaService;

    public FiadoController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // Obtiene un fiado por su id
    @GetMapping("/{id}")
    public ResponseEntity<VentaEntity> getFiado(@PathVariable Integer id) {
        VentaEntity v = ventaService.getFiadoById(id);
        if (v == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(v);
    }

    // Marca una venta como fiado
    @PostMapping("/{id}/marcar")
    public ResponseEntity<VentaEntity> marcarComoFiado(@PathVariable Integer id, @RequestBody FiadoUpdateRequest req) {
        VentaEntity v = ventaService.marcarComoFiado(id, req);
        return ResponseEntity.status(HttpStatus.OK).body(v);
    }

    // Actualiza los datos de un fiado
    @PatchMapping("/{id}")
    public ResponseEntity<VentaEntity> actualizarFiado(@PathVariable Integer id, @RequestBody FiadoUpdateRequest req) {
        VentaEntity v = ventaService.actualizarFiado(id, req);
        return ResponseEntity.ok(v);
    }

    // Cancela un fiado
    @DeleteMapping("/{id}")
    public ResponseEntity<VentaEntity> cancelarFiado(@PathVariable Integer id) {
        VentaEntity v = ventaService.cancelarFiado(id);
        return ResponseEntity.ok(v);
    }

    // Lista todos los fiados, opcionalmente solo los pendientes
    @GetMapping
    public ResponseEntity<List<VentaEntity>> listarFiados(@RequestParam(required = false, defaultValue = "true") boolean pendientesOnly) {
        List<VentaEntity> list = ventaService.listarFiados(pendientesOnly);
        return ResponseEntity.ok(list);
    }

    // Lista los clientes que tienen fiados, opcionalmente solo los con saldos pendientes
    @GetMapping("/clientes")
    public ResponseEntity<List<ClienteFiadoDTO>> listarClientesConFiado(@RequestParam(required = false, defaultValue = "true") boolean pendientesOnly) {
        List<ClienteFiadoDTO> list = ventaService.listarClientesConFiado(pendientesOnly);
        return ResponseEntity.ok(list);
    }

    // Lista los fiados de un cliente específico, opcionalmente solo los pendientes
    @GetMapping("/clientes/{clienteId}/fiados")
    public ResponseEntity<List<VentaEntity>> listarFiadosPorCliente(@PathVariable Integer clienteId,
                                                                    @RequestParam(required = false, defaultValue = "true") boolean pendientesOnly) {
        List<VentaEntity> list = ventaService.listarFiadosPorCliente(clienteId, pendientesOnly);
        return ResponseEntity.ok(list);
    }

}
