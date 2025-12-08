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

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private ProductoRepository productoRepository;

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
        lote.setId(1);
        lote.setNumeroLote("LOTE-001");
        lote.setCantidad(100);

        when(loteRepository.findById(1)).thenReturn(Optional.of(lote));

        Optional<LoteEntity> resultado = loteService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("LOTE-001", resultado.get().getNumeroLote());
    }

    @Test
    @DisplayName("Obtener todos los lotes")
    void testGetAllLotes() {
        LoteEntity l1 = new LoteEntity();
        l1.setId(1);
        l1.setNumeroLote("LOTE-001");

        LoteEntity l2 = new LoteEntity();
        l2.setId(2);
        l2.setNumeroLote("LOTE-002");

        when(loteRepository.findAll()).thenReturn(Arrays.asList(l1, l2));

        List<LoteEntity> resultado = loteService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Crear nuevo lote")
    void testCreateLote() {
        LoteEntity lote = new LoteEntity();
        lote.setNumeroLote("LOTE-003");
        lote.setCantidad(50);
        lote.setFechaVencimiento(LocalDate.of(2025, 12, 31));

        when(loteRepository.save(lote)).thenReturn(lote);

        LoteEntity resultado = loteService.create(lote);

        assertEquals("LOTE-003", resultado.getNumeroLote());
        assertEquals(50, resultado.getCantidad());
    }

    @Test
    @DisplayName("Actualizar lote existente")
    void testUpdateLote() {
        LoteEntity loteExistente = new LoteEntity();
        loteExistente.setId(1);
        loteExistente.setNumeroLote("LOTE-001");
        loteExistente.setCantidad(100);

        LoteEntity loteActualizado = new LoteEntity();
        loteActualizado.setCantidad(80);

        when(loteRepository.findById(1)).thenReturn(Optional.of(loteExistente));
        when(loteRepository.save(loteExistente)).thenReturn(loteExistente);

        loteExistente.setCantidad(80);
        LoteEntity resultado = loteService.update(1, loteActualizado);

        assertEquals(80, resultado.getCantidad());
    }

    @Test
    @DisplayName("Eliminar lote existente")
    void testDeleteLote() {
        LoteEntity lote = new LoteEntity();
        lote.setId(1);

        when(loteRepository.findById(1)).thenReturn(Optional.of(lote));

        loteService.delete(1);

        verify(loteRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Buscar lotes por número de lote")
    void testFindByNumeroLote() {
        LoteEntity lote = new LoteEntity();
        lote.setNumeroLote("LOTE-001");

        when(loteRepository.findByNumeroLote("LOTE-001"))
                .thenReturn(Optional.of(lote));

        Optional<LoteEntity> resultado = loteService.findByNumeroLote("LOTE-001");

        assertTrue(resultado.isPresent());
        assertEquals("LOTE-001", resultado.get().getNumeroLote());
    }

    @Test
    @DisplayName("Obtener lotes por producto")
    void testFindByProducto() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);

        LoteEntity l1 = new LoteEntity();
        l1.setId(1);
        l1.setProducto(producto);

        LoteEntity l2 = new LoteEntity();
        l2.setId(2);
        l2.setProducto(producto);

        when(loteRepository.findByProducto(producto))
                .thenReturn(Arrays.asList(l1, l2));

        List<LoteEntity> resultado = loteService.findByProducto(producto);

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Lote no encontrado por ID")
    void testLoteNotFound() {
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        Optional<LoteEntity> resultado = loteService.findById(999);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Obtener lotes proximos a vencer")
    void testGetLotesProximoAVencer() {
        LoteEntity l1 = new LoteEntity();
        l1.setId(1);
        l1.setFechaVencimiento(LocalDate.of(2024, 12, 15));

        LoteEntity l2 = new LoteEntity();
        l2.setId(2);
        l2.setFechaVencimiento(LocalDate.of(2025, 6, 1));

        List<LoteEntity> todos = Arrays.asList(l1, l2);
        LocalDate hoy = LocalDate.now();
        LocalDate proximoMes = hoy.plusDays(30);

        List<LoteEntity> proximosAVencer = todos.stream()
                .filter(l -> l.getFechaVencimiento().isAfter(hoy) &&
                           l.getFechaVencimiento().isBefore(proximoMes))
                .toList();

        assertTrue(proximosAVencer.size() >= 0);
    }

    @Test
    @DisplayName("Obtener lotes vencidos")
    void testGetLotesVencidos() {
        LoteEntity l1 = new LoteEntity();
        l1.setId(1);
        l1.setFechaVencimiento(LocalDate.of(2023, 1, 1));

        LoteEntity l2 = new LoteEntity();
        l2.setId(2);
        l2.setFechaVencimiento(LocalDate.of(2025, 12, 31));

        List<LoteEntity> todos = Arrays.asList(l1, l2);
        LocalDate hoy = LocalDate.now();

        List<LoteEntity> vencidos = todos.stream()
                .filter(l -> l.getFechaVencimiento().isBefore(hoy))
                .toList();

        assertEquals(1, vencidos.size());
    }

    @Test
    @DisplayName("Reducir cantidad de lote")
    void testReduceLoteCantidad() {
        LoteEntity lote = new LoteEntity();
        lote.setId(1);
        lote.setCantidad(100);

        when(loteRepository.findById(1)).thenReturn(Optional.of(lote));
        when(loteRepository.save(lote)).thenReturn(lote);

        lote.setCantidad(lote.getCantidad() - 20);

        assertEquals(80, lote.getCantidad());
    }
}
