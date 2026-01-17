package com.erp.p03.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "detalle_devoluciones")
public class DetalleDevolucionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_devolucion")
    private Integer idDetalleDevolucion;

    @Column(name = "devolucion_id")
    private Integer devolucionId;

    @Column(name = "detalle_venta_id")
    private Integer detalleVentaId; // Reference to original sale detail

    @Column(name = "producto_id")
    private Integer productoId;

    @Column(name = "cantidad_devuelta")
    private Integer cantidadDevuelta;

    @Column(name = "id_lote")
    private Integer idLote; // Lot to restore stock to

    @Column(name = "monto_devuelto")
    private Integer montoDevuelto; // Refund for this item

    // Transient fields for JSON responses
    @Transient
    private String productoNombre;

    @Transient
    private String codigoLote;
}
