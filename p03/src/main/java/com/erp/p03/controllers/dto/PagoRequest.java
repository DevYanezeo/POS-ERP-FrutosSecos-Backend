package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class PagoRequest {
    private Integer monto;
    private String metodoPago;
    private Integer usuarioId;
}
