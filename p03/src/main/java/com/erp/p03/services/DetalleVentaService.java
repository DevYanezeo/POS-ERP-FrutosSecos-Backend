package com.erp.p03.services;

import org.springframework.stereotype.Service;

import com.erp.p03.repositories.DetalleVentaRepository;

@Service
public class DetalleVentaService {

    private final DetalleVentaRepository detalleVentaRepository;
    
    public DetalleVentaService(DetalleVentaRepository detalleVentaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
    }

}
