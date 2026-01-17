package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.LoteEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.LoteRepository;
import com.erp.p03.repositories.ProductoRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("LoteService Tests")
class LoteServiceTest {

    @Mock private LoteRepository loteRepository;
    @Mock private ProductoRepository productoRepository;

    private LoteService loteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loteService = new LoteService(loteRepository, productoRepository);
    }

    @Test
    @DisplayName("Obtener lote por ID")
    void testGetLoteById() {
        LoteEntity lote = new LoteEntity();
        lote.setIdLote(1);
        lote.setCodigoLote("LOTE-001");
        lote.setCantidad(100);

        when(loteRepository.findById(1)).thenReturn(Optional.of(lote));

        LoteEntity resultado = loteService.findById(1);

        assertNotNull(resultado);
        assertEquals("LOTE-001", resultado.getCodigoLote());
    }

    @Test
    @DisplayName("Crear nuevo lote")
    void testCrearLote() {
        LoteEntity lote = new LoteEntity();
        lote.setCodigoLote("LOTE-003");
        lote.setCantidad(50);
        lote.setFechaVencimiento(LocalDate.of(2025, 12, 31));
        
        ProductoEntity producto = new ProductoEntity();
        producto.setIdProducto(1);
        lote.setProducto(producto);

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(loteRepository.save(any(LoteEntity.class))).thenReturn(lote);

        LoteEntity resultado = loteService.crearLote(lote);

        assertNotNull(resultado);
        assertEquals("LOTE-003", resultado.getCodigoLote());
        verify(loteRepository, times(1)).save(lote);
    }

    @Test
    @DisplayName("Actualizar cantidad de lote")
    void testUpdateCantidadLote() {
        LoteEntity lote = new LoteEntity();
        lote.setIdLote(1);
        lote.setCantidad(100);

        when(loteRepository.findById(1)).thenReturn(Optional.of(lote));
        when(loteRepository.save(any(LoteEntity.class))).thenReturn(lote);

        LoteEntity resultado = loteService.updateCantidadLote(1, 80);

        assertEquals(80, resultado.getCantidad());
        verify(loteRepository, times(1)).save(lote);
    }

    @Test
    @DisplayName("Buscar por código de lote")
    void testFindByCodigoLote() {
        LoteEntity lote = new LoteEntity();
        lote.setCodigoLote("COD-SCAN");

        when(loteRepository.findByCodigoLote("COD-SCAN")).thenReturn(lote);

        LoteEntity resultado = loteService.findByCodigoLote("COD-SCAN");

        assertNotNull(resultado);
        assertEquals("COD-SCAN", resultado.getCodigoLote());
    }
}
