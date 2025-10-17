package com.erp.p03.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
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
    private Integer stock; // total de todos los lotes activos
    private String unidad; // kg, lt, und
    private Boolean estado; // activo o inactivo
    @Column(name = "codigo", unique = true)
    private String codigo; // codigo de barras
    @Column(name = "categoria_id")
    private Integer categoriaId;
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("producto")
    private List<LoteEntity> lotes = new ArrayList<>();


}