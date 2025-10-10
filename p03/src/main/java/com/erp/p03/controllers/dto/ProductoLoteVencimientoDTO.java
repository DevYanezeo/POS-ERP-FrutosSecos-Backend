package com.erp.p03.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoLoteVencimientoDTO {
    private Integer idLote;
    private Integer productoId;
    private String nombreProducto;
    private Integer cantidad;
    private LocalDate fechaVencimiento;
    private Integer diasRestantes;
}

