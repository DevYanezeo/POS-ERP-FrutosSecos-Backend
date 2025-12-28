package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class ProductLossDTO {
    private Integer productoId;
    private String nombre;
    private Integer totalCantidadPerdida; // unidades vencidas en el periodo
    private Integer totalPerdida; // total perdido en CLP (cantidad * costo)
}

