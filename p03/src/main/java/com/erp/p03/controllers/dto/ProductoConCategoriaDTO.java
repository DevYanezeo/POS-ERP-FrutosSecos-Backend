package com.erp.p03.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    private LocalDate fechaVencimiento; // fecha de vencimiento mínima entre lotes
    private String codigo;
    private Integer categoriaId;
    private String nombreCategoria;
    private List<LoteDTO> lotes; // lista de lotes asociados
}
