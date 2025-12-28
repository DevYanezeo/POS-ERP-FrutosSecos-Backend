package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class ProductSalesDTO {
    private Integer productoId;
    private String nombre;
    private Integer totalCantidad;

    private Integer totalSubtotal;
    private String unidad;
    private Integer stockActual;
    private Integer totalCosto;
}
