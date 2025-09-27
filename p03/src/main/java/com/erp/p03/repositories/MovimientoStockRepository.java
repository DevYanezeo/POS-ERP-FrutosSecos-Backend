package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.MovimientoStockEntity;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStockEntity, Integer>{

}
