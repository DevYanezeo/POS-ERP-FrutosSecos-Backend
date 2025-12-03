package com.erp.p03.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import com.erp.p03.entities.ClienteFiadoEntity;
import com.erp.p03.repositories.ClienteFiadoRepository;

@Service
public class ClienteFiadoService {
    private final ClienteFiadoRepository repo;

    public ClienteFiadoService(ClienteFiadoRepository repo) {
        this.repo = repo;
    }

    public List<ClienteFiadoEntity> findAll() {
        return repo.findAll();
    }

    public Optional<ClienteFiadoEntity> findById(Integer id) {
        return repo.findById(id);
    }

    public Optional<ClienteFiadoEntity> findByTelefono(String telefono) {
        return repo.findByTelefono(telefono);
    }

    // Listar solo activos
    @Transactional
    public ClienteFiadoEntity create(ClienteFiadoEntity c) {
        // Normalizar telefono simple
        if (c.getTelefono() != null) c.setTelefono(c.getTelefono().trim());
        return repo.save(c);
    }

    // Actualizar cliente
    @Transactional
    public ClienteFiadoEntity update(Integer id, ClienteFiadoEntity data) {
        ClienteFiadoEntity existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        if (data.getNombre() != null) existing.setNombre(data.getNombre());
        if (data.getApellido() != null) existing.setApellido(data.getApellido());
        if (data.getTelefono() != null) existing.setTelefono(data.getTelefono());
        if (data.getEmail() != null) existing.setEmail(data.getEmail());
        if (data.getActivo() != null) existing.setActivo(data.getActivo());
        return repo.save(existing);
    }

    // Eliminar cliente
    @Transactional
    public void delete(Integer id) {
        // Soft delete
        ClienteFiadoEntity existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        existing.setActivo(false);
        repo.save(existing);
    }
}

