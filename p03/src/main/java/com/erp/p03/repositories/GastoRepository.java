package com.erp.p03.repositories;

import com.erp.p03.entities.GastoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<GastoEntity, Long> {
    List<GastoEntity> findByFechaBetween(Date fechaInicio, Date fechaFin);
    List<GastoEntity> findByTipo(String tipo);
}
