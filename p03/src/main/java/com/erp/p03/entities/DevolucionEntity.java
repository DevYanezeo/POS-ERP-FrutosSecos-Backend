package com.erp.p03.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "devoluciones")
public class DevolucionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion")
    private Integer idDevolucion;

    @Column(name = "venta_id")
    private Integer ventaId; // Original sale

    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    private String motivo; // Reason for return

    @Column(name = "monto_devuelto")
    private Integer montoDevuelto; // Total refund amount

    @Column(name = "usuario_id")
    private Integer usuarioId; // Who processed the return

    @Column(name = "tipo")
    private String tipo; // COMPLETA, PARCIAL

    // Transient field for including details in JSON responses
    @Transient
    private List<DetalleDevolucionEntity> detalles;
}
