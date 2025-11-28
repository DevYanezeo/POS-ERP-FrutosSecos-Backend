package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.DetalleVentaEntity;
import java.util.Optional;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity, Integer>{

    // Encuentra un detalle por ventaId y productoId (usar para operaciones sin conocer idDetalle)
    Optional<DetalleVentaEntity> findByVentaIdAndProductoId(Integer ventaId, Integer productoId);

    // Devuelve todos los detalles asociados a una venta
    List<DetalleVentaEntity> findByVentaId(Integer ventaId);

}
