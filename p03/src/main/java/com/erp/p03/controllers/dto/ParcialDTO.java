package com.erp.p03.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParcialDTO {

    private String nombre;
    private String descripcion;
    private Integer precio; // en clp
    private String unidad; // kg, lt, und
    private Boolean estado; // activo o inactivo

}
