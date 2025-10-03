package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class MovimientoStockRequest {
    private Integer productoId;
    private Integer usuarioId;
    private String tipoMovimiento; // INGRESO, AJUSTE_POSITIVO, MERMA, VENCIMIENTO, AJUSTE_NEGATIVO
    private Integer cantidad;
}

