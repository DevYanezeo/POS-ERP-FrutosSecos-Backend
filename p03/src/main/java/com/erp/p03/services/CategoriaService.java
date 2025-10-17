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
}
