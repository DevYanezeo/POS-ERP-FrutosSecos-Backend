package com.erp.p03.controllers.dto;

import lombok.Data;

@Data
public class ClienteFiadoDTO {
    private Integer clienteId;
    private Integer cantidadFiados;
    private Integer totalPendiente;

    // Datos del cliente para mostrar en frontend
    private String nombre;
    private String apellido;
    private String telefono;
}

