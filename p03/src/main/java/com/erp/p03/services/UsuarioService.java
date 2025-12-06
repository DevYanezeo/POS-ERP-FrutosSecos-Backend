package com.erp.p03.services;

import com.erp.p03.entities.UsuarioEntity;
import org.springframework.stereotype.Service;

import com.erp.p03.repositories.UsuarioRepository;

import java.util.*;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }

    public UsuarioEntity findById(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public UsuarioEntity crearUsuario(UsuarioEntity usuario) {
        // Validaciones básicas: email y rut únicos
        if (usuario.getEmail() != null && usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email ya registrado");
        }
        if (usuario.getRut() != null && usuarioRepository.existsByRut(usuario.getRut())) {
            throw new IllegalArgumentException("RUT ya registrado");
        }
        // por simplicidad guardamos tal cual (si necesita cifrado de password, hacerlo antes)
        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity updateUsuario(Integer id, UsuarioEntity usuarioUpdated) {
        UsuarioEntity usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si el email cambia, comprobar unicidad
        if (usuarioUpdated.getEmail() != null && !usuarioUpdated.getEmail().equals(usuarioExistente.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioUpdated.getEmail())) {
                throw new IllegalArgumentException("Email ya registrado");
            }
            usuarioExistente.setEmail(usuarioUpdated.getEmail());
        }

        // Si el rut cambia, comprobar unicidad
        if (usuarioUpdated.getRut() != null && !usuarioUpdated.getRut().equals(usuarioExistente.getRut())) {
            if (usuarioRepository.existsByRut(usuarioUpdated.getRut())) {
                throw new IllegalArgumentException("RUT ya registrado");
            }
            usuarioExistente.setRut(usuarioUpdated.getRut());
        }

        // actualizar otros campos (si no son nulos)
        if (usuarioUpdated.getNombre() != null) usuarioExistente.setNombre(usuarioUpdated.getNombre());
        if (usuarioUpdated.getPassword() != null) usuarioExistente.setPassword(usuarioUpdated.getPassword());
        if (usuarioUpdated.getRol() != null) usuarioExistente.setRol(usuarioUpdated.getRol());
        if (usuarioUpdated.getTelefono() != null) usuarioExistente.setTelefono(usuarioUpdated.getTelefono());
        if (usuarioUpdated.getActivo() != null) usuarioExistente.setActivo(usuarioUpdated.getActivo());

        return usuarioRepository.save(usuarioExistente);
    }

    public void deleteUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

}
