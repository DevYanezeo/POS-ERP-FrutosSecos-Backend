package com.erp.p03.entities;

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
}