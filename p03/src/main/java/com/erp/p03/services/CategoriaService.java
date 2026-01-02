package com.erp.p03.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.repositories.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    //  Método para obtener todas las categorías
    public List<CategoriaEntity> findAll() {
        return categoriaRepository.findAll();
    }

    // crear nueva categoria
    public CategoriaEntity crearCategoria(CategoriaEntity categoria) {
        return categoriaRepository.save(categoria);
    }

    // actualizar categoria
    public CategoriaEntity actualizarCategoria(int id, CategoriaEntity categoria) {
        CategoriaEntity existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        existente.setNombre(categoria.getNombre());
        return categoriaRepository.save(existente);
    }

    // eliminar categoria
    public void eliminarCategoria(int id) {
        categoriaRepository.deleteById(id);
    }
}
