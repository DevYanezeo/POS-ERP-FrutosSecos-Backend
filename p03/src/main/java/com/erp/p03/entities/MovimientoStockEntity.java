package com.erp.p03.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movimientos_stock")
public class MovimientoStockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private int idMovimiento;    
    @Column(name = "producto_id")
    private Integer productoId;    
    private LocalDateTime fecha;    
    @Column(name = "usuario_id")
    private Integer usuarioId;    
    @Column(name = "tipo_movimiento")
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE_POSITIVO, AJUSTE_NEGATIVO, MERMA, VENCIMIENTO
}