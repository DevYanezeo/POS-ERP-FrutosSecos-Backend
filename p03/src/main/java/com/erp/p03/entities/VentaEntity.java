package com.erp.p03.entities;

import java.time.LocalDateTime;
import java.time.LocalDate;

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

    // Campos para fiados
    private Boolean fiado; // si la venta fue a crédito (fiado)

    @Column(name = "saldo_pendiente")
    private Integer saldoPendiente; // monto restante por pagar (misma unidad que total)

    @Column(name = "fecha_vencimiento_pago")
    private LocalDate fechaVencimientoPago; // fecha límite para pagar el fiado (nullable)

    @Column(name = "pago_completado_at")
    private LocalDateTime pagoCompletadoAt;

    @Column(name = "cliente_id")
    private Integer clienteId; // opcional: referencia a cliente si existe

    // Constructor de compatibilidad con la versión previa de tests / código
    // Firma: (int idVenta, LocalDateTime fecha, Integer usuarioId, Integer subtotal, Integer iva, Integer total, String metodoPago)
    public VentaEntity(int idVenta, LocalDateTime fecha, Integer usuarioId, Integer subtotal, Integer iva, Integer total, String metodoPago) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.metodoPago = metodoPago;
        // dejar el resto de campos en null/default
        this.fiado = false;
        this.saldoPendiente = 0;
        this.fechaVencimientoPago = null;
        this.pagoCompletadoAt = null;
        this.clienteId = null;
    }
}