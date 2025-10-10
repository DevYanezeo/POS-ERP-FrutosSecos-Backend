package com.erp.p03.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.ProductoEntity;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Integer>{

    List<ProductoEntity> findByEstado(Boolean estado);
    List<ProductoEntity> findByCategoriaId(Integer categoriaId);
    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);
    List<ProductoEntity> findByEstadoAndCategoriaId(Boolean estado, Integer categoriaId);
    List<ProductoEntity> findByPesoBetween(int min, int max);
}
