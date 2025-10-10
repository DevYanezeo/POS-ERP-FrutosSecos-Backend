package com.erp.p03.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "productos")
public class ProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private int idProducto;
    private String nombre;
    private String descripcion;
    private String imagen;
    private Integer precio; // en clp
    private Integer stock;
    private String unidad; // kg, lt, und
    private Boolean estado; // activo o inactivo
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento; // fecha de vencimiento
    private String codigo;
    @Column(name = "categoria_id")
    private Integer categoriaId;
    private Integer peso; // peso en gramos
}