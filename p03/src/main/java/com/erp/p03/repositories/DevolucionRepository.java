package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.DevolucionEntity;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<DevolucionEntity, Integer> {

    // Find all returns ordered by date descending
    List<DevolucionEntity> findAllByOrderByFechaDevolucionDesc();

    // Find returns by original sale ID
    List<DevolucionEntity> findByVentaId(Integer ventaId);

    // Find returns within date range
    List<DevolucionEntity> findByFechaDevolucionBetween(LocalDateTime desde, LocalDateTime hasta);

    // Find by user who processed the return
    List<DevolucionEntity> findByUsuarioId(Integer usuarioId);
}
