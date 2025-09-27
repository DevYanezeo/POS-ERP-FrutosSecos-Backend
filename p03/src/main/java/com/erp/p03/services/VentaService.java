package com.erp.p03.services;

import org.springframework.stereotype.Service;

import com.erp.p03.repositories.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    
    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

}
