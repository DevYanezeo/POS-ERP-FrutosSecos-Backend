package com.erp.p03.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp.p03.entities.UsuarioEntity;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer>{
    
    Optional<UsuarioEntity> findByEmail(String email);
    Optional<UsuarioEntity> findByRut(String rut);
    boolean existsByEmail(String email);
    boolean existsByRut(String rut);
}
