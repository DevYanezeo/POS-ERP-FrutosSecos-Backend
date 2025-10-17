package com.erp.p03.repositories;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.erp.p03.entities.ProductoEntity;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Integer> {

    // Filtros
    List<ProductoEntity> findByEstado(Boolean estado);
    List<ProductoEntity> findByCategoriaId(Integer categoriaId);
    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);
    List<ProductoEntity> findByEstadoAndCategoriaId(Boolean estado, Integer categoriaId);

    List<ProductoEntity> findByPrecioBetween(BigDecimal min, BigDecimal max);

    // Ordenamientos por nombre
    List<ProductoEntity> findAllByOrderByNombreAsc();
    List<ProductoEntity> findAllByOrderByNombreDesc();

    // Ordenamientos por stock
    List<ProductoEntity> findAllByOrderByStockAsc();
    List<ProductoEntity> findAllByOrderByStockDesc();

    // Ordenamientos por precio
    List<ProductoEntity> findAllByOrderByPrecioAsc();
    List<ProductoEntity> findAllByOrderByPrecioDesc();

    @Query("SELECT DISTINCT p FROM ProductoEntity p LEFT JOIN FETCH p.lotes l")
    List<ProductoEntity> findAllWithLotes();
}
