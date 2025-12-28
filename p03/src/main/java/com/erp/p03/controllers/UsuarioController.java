package com.erp.p03.controllers;

import com.erp.p03.entities.UsuarioEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.p03.services.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<UsuarioEntity>> findAll() {
        List<UsuarioEntity> usuarios = usuarioService.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> getById(@PathVariable Integer id) {
        try {
            UsuarioEntity usuario = usuarioService.findById(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<UsuarioEntity> crear(@RequestBody UsuarioEntity usuario) {
        try {
            UsuarioEntity saved = usuarioService.crearUsuario(usuario);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> actualizar(@PathVariable Integer id, @RequestBody UsuarioEntity usuario) {
        try {
            UsuarioEntity updated = usuarioService.updateUsuario(id, usuario);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        try {
            usuarioService.deleteUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
