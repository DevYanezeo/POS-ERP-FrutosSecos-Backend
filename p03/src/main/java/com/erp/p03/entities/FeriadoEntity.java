package com.erp.p03.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "feriados")
public class FeriadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feriado")
    private Integer idFeriado;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "recurrente_anual")
    private Boolean recurrenteAnual = false;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "scope")
    private String scope = "GLOBAL";
}

