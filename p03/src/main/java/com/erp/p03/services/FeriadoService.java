package com.erp.p03.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erp.p03.entities.FeriadoEntity;
import com.erp.p03.repositories.FeriadoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FeriadoService {

    private final FeriadoRepository feriadoRepository;

    public FeriadoService(FeriadoRepository feriadoRepository) {
        this.feriadoRepository = feriadoRepository;
    }

    public List<FeriadoEntity> findAll() {
        return feriadoRepository.findAll();
    }

    public Optional<FeriadoEntity> findById(Integer id) {
        return feriadoRepository.findById(id);
    }

    public List<FeriadoEntity> findByFechaBetween(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) return List.of();
        return feriadoRepository.findByFechaBetween(desde, hasta).stream()
                .filter(f -> Boolean.TRUE.equals(f.getActivo()))
                .toList();
    }

    @Transactional
    public FeriadoEntity save(FeriadoEntity feriado) {
        // podría añadirse validación de unicidad por fecha+scope
        return feriadoRepository.save(feriado);
    }

    @Transactional
    public FeriadoEntity update(Integer id, FeriadoEntity patch) {
        FeriadoEntity existing = feriadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feriado no encontrado"));
        if (patch.getFecha() != null) existing.setFecha(patch.getFecha());
        if (patch.getNombre() != null) existing.setNombre(patch.getNombre());
        if (patch.getRecurrenteAnual() != null) existing.setRecurrenteAnual(patch.getRecurrenteAnual());
        if (patch.getActivo() != null) existing.setActivo(patch.getActivo());
        if (patch.getScope() != null) existing.setScope(patch.getScope());
        return feriadoRepository.save(existing);
    }

    @Transactional
    public void deleteById(Integer id) {
        feriadoRepository.deleteById(id);
    }
}

