package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.erp.p03.entities.PagoEntity;

@Repository
public interface PagoRepository extends JpaRepository<PagoEntity, Integer> {
    List<PagoEntity> findByVentaIdOrderByFechaDesc(Integer ventaId);
}

