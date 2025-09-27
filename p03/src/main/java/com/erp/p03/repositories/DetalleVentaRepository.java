package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.DetalleVentaEntity;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity, Integer>{

}
