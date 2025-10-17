package com.erp.p03.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoteDTO {
    private Integer idLote;
    private Integer cantidad;
    private LocalDate fechaVencimiento;
    private LocalDate fechaIngreso;
    private String codigoLote;
    private Boolean estado;
}

