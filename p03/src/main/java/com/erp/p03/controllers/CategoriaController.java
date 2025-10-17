package com.erp.p03.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
