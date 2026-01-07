package com.erp.p03.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "detalle_ventas")
public class DetalleVentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private int idDetalleVenta;

    @Column(name = "venta_id")
    private Integer ventaId;

    @Column(name = "producto_id")
    private Integer productoId;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Integer precioUnitario; // precio unitario en pesos chilenos

    private Integer subtotal; // subtotal en pesos chilenos

    // Metodo de pago utilizado en esta venta
    @Column(name = "metodo_pago")
    private String metodoPago; // EFECTIVO, DEBITO, CREDITO, TRANSFERENCIA

    // Nuevo: id del lote usado (opcional). Permite revertir cantidad al lote
    // correcto.
    @Column(name = "id_lote")
    private Integer idLote;

    // Nuevo: costo unitario histórico aplicado a este detalle (puede ser null si no
    // se registró)
    @Column(name = "costo_unitario")
    private Integer costoUnitario;

    // Campo transient para incluir nombre del producto en JSON
    @Transient
    private String productoNombre;

    // Campo transient para incluir unidad del producto en JSON (kg, lt, und)
    @Transient
    private String productoUnidad;

    // Campo transient para incluir código del lote en JSON
    @Transient
    private String codigoLote;
}