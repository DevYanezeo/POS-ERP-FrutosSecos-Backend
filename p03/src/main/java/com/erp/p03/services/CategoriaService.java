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

    // Método para obtener todas las categorías
    public List<CategoriaEntity> findAll() {
        return categoriaRepository.findAll();
    }

    // crear nueva categoria
    public CategoriaEntity crearCategoria(CategoriaEntity categoria) {
        return categoriaRepository.save(categoria);
    }

    public CategoriaEntity actualizarCategoria(Integer id, CategoriaEntity categoriaDetails) {
        CategoriaEntity categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada: " + id));
        categoria.setNombre(categoriaDetails.getNombre());
        return categoriaRepository.save(categoria);
    }

    public void eliminarCategoria(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
