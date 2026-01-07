package com.erp.p03.controllers;

import com.erp.p03.entities.DevolucionEntity;
import com.erp.p03.services.DevolucionService;
import com.erp.p03.services.DevolucionService.ItemDevolucion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devoluciones")
@CrossOrigin(origins = "*")
public class DevolucionController {

    @Autowired
    private DevolucionService devolucionService;

    /**
     * Process a full sale return
     * POST /api/devoluciones/venta/{idVenta}/completa
     */
    @PostMapping("/venta/{idVenta}/completa")
    public ResponseEntity<?> procesarDevolucionCompleta(
            @PathVariable Integer idVenta,
            @RequestBody DevolucionCompletaRequest request) {
        try {
            DevolucionEntity devolucion = devolucionService.procesarDevolucionCompleta(
                    idVenta,
                    request.getMotivo(),
                    request.getUsuarioId());
            return ResponseEntity.ok(devolucion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Process a partial sale return
     * POST /api/devoluciones/parcial
     */
    @PostMapping("/parcial")
    public ResponseEntity<?> procesarDevolucionParcial(@RequestBody DevolucionParcialRequest request) {
        try {
            DevolucionEntity devolucion = devolucionService.procesarDevolucionParcial(
                    request.getVentaId(),
                    request.getItems(),
                    request.getMotivo(),
                    request.getUsuarioId());
            return ResponseEntity.ok(devolucion);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all returns
     * GET /api/devoluciones
     */
    @GetMapping
    public ResponseEntity<List<DevolucionEntity>> listarDevoluciones() {
        List<DevolucionEntity> devoluciones = devolucionService.listarDevoluciones();
        return ResponseEntity.ok(devoluciones);
    }

    /**
     * Get return by ID
     * GET /api/devoluciones/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDevolucion(@PathVariable Integer id) {
        DevolucionEntity devolucion = devolucionService.findById(id);
        if (devolucion == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(devolucion);
    }

    // DTO Classes for requests
    public static class DevolucionCompletaRequest {
        private String motivo;
        private Integer usuarioId;

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }

        public Integer getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Integer usuarioId) {
            this.usuarioId = usuarioId;
        }
    }

    public static class DevolucionParcialRequest {
        private Integer ventaId;
        private List<ItemDevolucion> items;
        private String motivo;
        private Integer usuarioId;

        public Integer getVentaId() {
            return ventaId;
        }

        public void setVentaId(Integer ventaId) {
            this.ventaId = ventaId;
        }

        public List<ItemDevolucion> getItems() {
            return items;
        }

        public void setItems(List<ItemDevolucion> items) {
            this.items = items;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }

        public Integer getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(Integer usuarioId) {
            this.usuarioId = usuarioId;
        }
    }
}
