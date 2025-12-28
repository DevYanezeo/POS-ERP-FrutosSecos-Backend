package com.erp.p03.controllers.dto;

import lombok.Data;

/**
 * DTO para actualizar/crear información de un fiado desde el cliente.
 * Fecha de vencimiento se maneja como String en formato DD/MM/AAAA.
 */
@Data
public class FiadoUpdateRequest {
    private Integer clienteId;
    private String fechaVencimientoPago; // dd/MM/yyyy
    private Integer saldoPendiente;
    private Boolean fiado; // permitir activar/desactivar
}

