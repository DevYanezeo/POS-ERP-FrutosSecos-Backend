package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.p03.entities.DetalleVentaEntity;
import java.util.Optional;
import java.util.List;
import java.sql.Timestamp;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaEntity, Integer>{

    // Encuentra un detalle por ventaId y productoId (usar para operaciones sin conocer idDetalle)
    Optional<DetalleVentaEntity> findByVentaIdAndProductoId(Integer ventaId, Integer productoId);

    // Devuelve todos los detalles asociados a una venta
    List<DetalleVentaEntity> findByVentaId(Integer ventaId);

    // Consulta agregada: suma cantidad y subtotal por producto en un rango de fechas (basado en la fecha de la venta)
    // Retorna filas con: producto_id, nombre, totalCantidad, totalSubtotal
    @Query(value = "SELECT d.producto_id AS productoId, p.nombre AS nombre, SUM(d.cantidad) AS totalCantidad, SUM(d.subtotal) AS totalSubtotal " +
            "FROM detalle_ventas d " +
            "JOIN ventas v ON d.venta_id = v.id_venta " +
            "JOIN productos p ON d.producto_id = p.id_producto " +
            "WHERE v.fecha BETWEEN :start AND :end " +
            "GROUP BY d.producto_id, p.nombre " +
            "ORDER BY SUM(d.cantidad) DESC", nativeQuery = true)
    List<Object[]> findProductSalesBetweenDates(@Param("start") Timestamp start, @Param("end") Timestamp end);

}
