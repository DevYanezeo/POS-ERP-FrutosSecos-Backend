package com.erp.p03.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "clientes_fiados")
public class ClienteFiadoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    private String nombre;
    private String apellido;
    private String telefono;
    private String email;

    private Boolean activo = true;

    // Fecha de creación del registro
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

