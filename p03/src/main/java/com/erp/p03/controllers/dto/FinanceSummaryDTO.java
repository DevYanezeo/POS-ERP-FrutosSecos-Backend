package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class FinanceSummaryDTO {
    private Long totalIngresos;
    private Long totalCostoProductos;
    private Long gastosAdquisicion;
    private Long gastosOperacionales;
    private Long utilidadBruta;
    private Long utilidadNeta;
    private Double margenPorcentaje;
}
