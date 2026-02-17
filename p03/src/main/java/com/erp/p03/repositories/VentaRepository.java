package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.VentaEntity;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<VentaEntity, Integer> {

    // Devuelve todas las ventas ordenadas por fecha descendente
    List<VentaEntity> findAllByOrderByFechaDesc();

    // Filtrado por rango de fechas
    List<VentaEntity> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);

    // Filtrado por usuario
    List<VentaEntity> findByUsuarioId(Integer usuarioId);

    // Filtrado por rango y usuario
    List<VentaEntity> findByFechaBetweenAndUsuarioId(LocalDateTime desde, LocalDateTime hasta, Integer usuarioId);

    // Fecha mayor/menor para casos en que sólo se dan uno de los límites
    List<VentaEntity> findByFechaAfter(LocalDateTime desde);

    List<VentaEntity> findByFechaBefore(LocalDateTime hasta);

    List<VentaEntity> findByFechaAfterAndUsuarioId(LocalDateTime desde, Integer usuarioId);

    List<VentaEntity> findByFechaBeforeAndUsuarioId(LocalDateTime hasta, Integer usuarioId);

    // ----------------- Métodos para fiado -----------------
    // Todas las ventas marcadas como fiado
    List<VentaEntity> findByFiadoTrue();

    // Ventas fiadas con saldo pendiente > 0
    List<VentaEntity> findByFiadoTrueAndSaldoPendienteGreaterThan(Integer amount);

    // Suma de descuentos globales en un rango de fechas
    @org.springframework.data.jpa.repository.Query("SELECT SUM(v.descuentoGlobal) FROM VentaEntity v WHERE v.fecha BETWEEN :desde AND :hasta")
    Integer sumDescuentoGlobalBetween(LocalDateTime desde, LocalDateTime hasta);
}
