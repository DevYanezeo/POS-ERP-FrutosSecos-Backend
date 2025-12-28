package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class ProductMarginDTO {
    private Integer productoId;
    private String nombre;
    private Integer totalCantidad; // total unidades vendidas en el periodo
    private Integer totalIngreso;  // suma de subtotales
    private Integer totalCosto;    // suma de (cantidad * costo)
    private Integer precioUnitario; // precio unitario actual del producto (puede ser null)

    // Campos calculados
    private Integer margen; // totalIngreso - totalCosto (puede ser null si no hay costo)
    private Double ingresoSobrePrecio; // compatibilidad: totalIngreso / precioUnitario (double), null si precioUnitario null o 0
    private Double margenSobreIngreso; // margen / totalIngreso (porcentaje entre 0 y 1), null si totalIngreso null o 0

    // Nuevos campos: métricas más intuitivas
    private Double ingresoPorUnidad; // totalIngreso / totalCantidad (double), null si totalCantidad null o 0
    private Double ingresoSobrePrecioRelativo; // ingresoPorUnidad / precioUnitario_actual (double), null si precioUnitario null o 0
}
