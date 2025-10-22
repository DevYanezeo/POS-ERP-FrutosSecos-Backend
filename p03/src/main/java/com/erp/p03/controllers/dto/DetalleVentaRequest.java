package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class DetalleVentaRequest {
    private Integer productoId;
    private Integer cantidad;
    private Integer precioUnitario;
    private Integer idLote; // lote desde donde se descuenta
}
