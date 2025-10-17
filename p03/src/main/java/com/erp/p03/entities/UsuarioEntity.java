package com.erp.p03.entities;

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
@Table(name = "usuarios")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int idUsuario;    
    private String nombre;    
    @Column(unique = true)
    private String email;    
    private String password;   
    private String rol; // ADMIN, VENDEDOR, CAJERO
    @Column(unique = true, length = 12)
    private String rut; // RUT (ej: 12345678-9)
    private String telefono;    
    private Boolean activo; // para activar/desactivar usuarios
}