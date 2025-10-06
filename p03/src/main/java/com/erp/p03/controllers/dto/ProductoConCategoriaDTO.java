package com.erp.p03.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoConCategoriaDTO {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private String imagen;
    private Integer precio; // en clp
    private Integer stock;
    private String unidad; // kg, lt, und
    private Boolean estado; // activo o inactivo
    private LocalDate fechaVencimiento; // fecha de vencimiento
    private String codigo;
    private Integer categoriaId;
    private String nombreCategoria;
}
