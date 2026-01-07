package com.erp.p03.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gastos")
public class GastoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gasto")
    private Long idGasto;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer monto;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(nullable = false)
    private String tipo; // OPERACIONAL, ADQUISICION, OTROS, INGRESO

    // Relación con Usuario (quién registró el gasto)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true)
    private UsuarioEntity usuario;

    // Opcional: Relación con Producto si es un gasto específico de producto (merma,
    // etc)
    @ManyToOne(optional = true)
    @JoinColumn(name = "producto_id", nullable = true)
    private ProductoEntity producto;
}
