package com.erp.p03.repositories;

import com.erp.p03.entities.LoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<LoteEntity, Integer> {
    List<LoteEntity> findByProducto_IdProductoOrderByFechaVencimientoAsc(Integer productoId);
    List<LoteEntity> findByFechaVencimientoBetween(LocalDate desde, LocalDate hasta);
    // Buscar lote por su código de etiqueta
    LoteEntity findByCodigoLote(String codigoLote);
}
