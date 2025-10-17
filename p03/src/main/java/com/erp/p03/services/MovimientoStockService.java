package com.erp.p03.services;

import org.springframework.stereotype.Service;

import com.erp.p03.repositories.MovimientoStockRepository;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.entities.MovimientoStockEntity;
import com.erp.p03.repositories.ProductoRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.erp.p03.controllers.dto.MovimientoStockRequest;

@Service
public class MovimientoStockService {

    private final MovimientoStockRepository movimientoStockRepository;
    
    public MovimientoStockService(MovimientoStockRepository movimientoStockRepository) {
        this.movimientoStockRepository = movimientoStockRepository;
    }

    @Autowired
    private ProductoRepository productoRepository;

    // Registra un movimiento de stock y actualiza el stock del producto
    @Transactional
    public MovimientoStockEntity registrarMovimiento(MovimientoStockRequest request) {
        // Busca el producto por ID
        ProductoEntity producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        int cantidad = request.getCantidad();
        String tipo = request.getTipoMovimiento();
        // Según el tipo de movimiento, suma o resta stock
        if (tipo.equalsIgnoreCase("INGRESO") || tipo.equalsIgnoreCase("AJUSTE_POSITIVO")) {
            producto.setStock(producto.getStock() + cantidad); // Suma stock
        } else if (tipo.equalsIgnoreCase("MERMA") || tipo.equalsIgnoreCase("VENCIMIENTO") || tipo.equalsIgnoreCase("AJUSTE_NEGATIVO")) {
            int nuevoStock = producto.getStock() - cantidad;
            if (nuevoStock < 0) {
                throw new RuntimeException("Stock insuficiente para este movimiento");
            }
            producto.setStock(nuevoStock); // Resta stock
        } else {
            throw new RuntimeException("Tipo de movimiento no soportado");
        }
        // Guarda el producto actualizado
        productoRepository.save(producto);
        // Crea y guarda el movimiento
        MovimientoStockEntity movimiento = new MovimientoStockEntity();
        movimiento.setProductoId(request.getProductoId());
        movimiento.setUsuarioId(request.getUsuarioId());
        movimiento.setTipoMovimiento(tipo);
        movimiento.setFecha(LocalDateTime.now());
        return movimientoStockRepository.save(movimiento);
    }



}
