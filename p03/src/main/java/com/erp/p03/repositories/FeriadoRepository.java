package com.erp.p03.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.FeriadoEntity;

@Repository
public interface FeriadoRepository extends JpaRepository<FeriadoEntity, Integer> {
    List<FeriadoEntity> findByFechaBetween(LocalDate desde, LocalDate hasta);
    List<FeriadoEntity> findByFecha(LocalDate fecha);
}

