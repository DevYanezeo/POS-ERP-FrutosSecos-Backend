package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class FinanceSummaryDTO {
    private Long totalIngresos; // Ingresos por ventas
    private Long ingresosAdicionales; // Ingresos registrados como tipo INGRESO
    private Long totalCostoProductos;
    private Long gastosAdquisicion;
    private Long gastosOperacionales;
    private Long utilidadBruta;
    private Long utilidadNeta;
    private Double margenPorcentaje;
}
