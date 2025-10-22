package com.erp.p03.controllers.dto;

import lombok.Data;

import java.util.List;

@Data
public class VentaRequest {
    private Integer usuarioId;
    private String metodoPago;
    private Integer subtotal;
    private Integer iva;
    private Integer total;
    private List<DetalleVentaRequest> detalles;
}
