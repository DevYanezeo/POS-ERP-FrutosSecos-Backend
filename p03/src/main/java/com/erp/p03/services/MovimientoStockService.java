package com.erp.p03.services;

import org.springframework.stereotype.Service;

import com.erp.p03.repositories.MovimientoStockRepository;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoStockRepository;
    
    public MovimientoStockService(MovimientoStockRepository movimientoStockRepository) {
        this.movimientoStockRepository = movimientoStockRepository;
    }

}
