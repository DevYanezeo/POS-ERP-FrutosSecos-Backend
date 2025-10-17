package com.erp.p03.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lotes")
public class LoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;
    @ManyToOne(fetch = FetchType.LAZY) // relacion uno a muchos
    @JoinColumn(name = "producto_id", nullable = false)
    @JsonIgnoreProperties({"lotes", "hibernateLazyInitializer", "handler"})
    private ProductoEntity producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "codigo_lote")
    private String codigoLote;

    @Column(name = "estado")
    private Boolean estado; // activo o inactivo
}
