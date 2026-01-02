package com.erp.p03.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.services.CategoriaService;

@RestController
@RequestMapping("api/categorias")
@CrossOrigin("*")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    //  Endpoint para listar todas las categorías
    @GetMapping
    public ResponseEntity<List<CategoriaEntity>> getAllCategorias() {
        List<CategoriaEntity> categorias = categoriaService.findAll();
        return ResponseEntity.ok(categorias);
    }
    // crear nueva categoria
    @PostMapping("/crear")
    public ResponseEntity<CategoriaEntity> crearCategoria(@RequestBody CategoriaEntity categoria) {
        CategoriaEntity nuevaCategoria = categoriaService.crearCategoria(categoria);
        return ResponseEntity.ok(nuevaCategoria);
    }

    // actualizar categoria
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaEntity> actualizarCategoria(@PathVariable int id, @RequestBody CategoriaEntity categoria) {
        CategoriaEntity actualizada = categoriaService.actualizarCategoria(id, categoria);
        return ResponseEntity.ok(actualizada);
    }

    // eliminar categoria
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable int id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }


}
