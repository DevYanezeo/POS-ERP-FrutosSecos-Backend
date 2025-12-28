package com.erp.p03.services;

import com.erp.p03.entities.GastoEntity;
import com.erp.p03.repositories.GastoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;

    public List<GastoEntity> listarTodos() {
        return gastoRepository.findAll();
    }

    public GastoEntity guardarGasto(GastoEntity gasto) {
        if (gasto.getFecha() == null) {
            gasto.setFecha(new Date());
        }
        return gastoRepository.save(gasto);
    }

    public List<GastoEntity> listarPorRangoFecha(Date inicio, Date fin) {
        return gastoRepository.findByFechaBetween(inicio, fin);
    }
    
    public void eliminarGasto(Long id) {
        gastoRepository.deleteById(id);
    }
}
