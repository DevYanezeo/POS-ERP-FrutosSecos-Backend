package com.erp.p03.controllers.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpiredLotDTO {
    private Integer loteId;
    private String codigoLote;
    private String productoNombre;
    private Integer cantidad;
    private Integer costoUnitario;
    private Integer perdidaTotal;
    private LocalDate fechaVencimiento;
}
