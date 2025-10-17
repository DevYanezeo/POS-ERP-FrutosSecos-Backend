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
@Table(name = "ventas")
public class VentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private int idVenta;    
    private LocalDateTime fecha;    
    @Column(name = "usuario_id")
    private Integer usuarioId;    
    private Integer subtotal; // precio sin IVA en pesos chilenos
    private Integer iva; // IVA en pesos chilenos (19%)
    private Integer total; // total en pesos chilenos
    @Column(name = "metodo_pago")
    private String metodoPago; // EFECTIVO, DEBITO, CREDITO, TRANSFERENCIA
}