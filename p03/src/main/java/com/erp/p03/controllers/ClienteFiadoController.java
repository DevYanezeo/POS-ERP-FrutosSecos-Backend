package com.erp.p03.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.erp.p03.services.ClienteFiadoService;
import com.erp.p03.entities.ClienteFiadoEntity;

import java.util.List;

@RestController
@RequestMapping("api/clientesfiado")
@CrossOrigin("*")
public class ClienteFiadoController {

    private final ClienteFiadoService service;

    public ClienteFiadoController(ClienteFiadoService service) {
        this.service = service;
    }

    // Lista a los clientes con fiados
    @GetMapping
    public ResponseEntity<List<ClienteFiadoEntity>> listar() {
        return ResponseEntity.ok(service.findAll());
    }

    // Lista a clientes con fiados activos
    @GetMapping("/activo")
    public ResponseEntity<List<ClienteFiadoEntity>> listarActivos() {
        return ResponseEntity.ok(service.findAll().stream().filter(c -> Boolean.TRUE.equals(c.getActivo())).toList());
    }

    // Obtiene un cliente por la id
    @GetMapping("/{id}")
    public ResponseEntity<ClienteFiadoEntity> obtener(@PathVariable Integer id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    // Obtiene un cliente por el telefono
    @GetMapping("/buscar")
    public ResponseEntity<ClienteFiadoEntity> buscarPorTelefono(@RequestParam String telefono) {
        return service.findByTelefono(telefono).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Crea un nuevo cliente con fiado
    @PostMapping
    public ResponseEntity<ClienteFiadoEntity> crear(@RequestBody ClienteFiadoEntity c) {
        ClienteFiadoEntity creado = service.create(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Actualiza un cliente con fiado
    @PutMapping("/{id}")
    public ResponseEntity<ClienteFiadoEntity> actualizar(@PathVariable Integer id, @RequestBody ClienteFiadoEntity data) {
        ClienteFiadoEntity actualizado = service.update(id, data);
        return ResponseEntity.ok(actualizado);
    }

    // Elimina un cliente con fiado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}

