package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.DetalleDevolucionEntity;
import java.util.List;

@Repository
public interface DetalleDevolucionRepository extends JpaRepository<DetalleDevolucionEntity, Integer> {

    // Find all details for a specific return
    List<DetalleDevolucionEntity> findByDevolucionId(Integer devolucionId);

    // Find by original sale detail ID
    List<DetalleDevolucionEntity> findByDetalleVentaId(Integer detalleVentaId);

    // Find by product ID
    List<DetalleDevolucionEntity> findByProductoId(Integer productoId);
}
