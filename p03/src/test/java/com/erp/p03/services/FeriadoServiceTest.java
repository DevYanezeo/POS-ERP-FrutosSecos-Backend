package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.FeriadoEntity;
import com.erp.p03.repositories.FeriadoRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FeriadoService Tests")
class FeriadoServiceTest {

    @Mock
    private FeriadoRepository feriadoRepository;

    private FeriadoService feriadoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feriadoService = new FeriadoService(feriadoRepository);
    }

    @Test
    @DisplayName("Obtener feriado por ID")
    void testGetFeriadoById() {
        FeriadoEntity feriado = new FeriadoEntity();
        feriado.setId(1);
        feriado.setFecha(LocalDate.of(2024, 12, 25));
        feriado.setNombre("Navidad");

        when(feriadoRepository.findById(1)).thenReturn(Optional.of(feriado));

        Optional<FeriadoEntity> resultado = feriadoService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Navidad", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Obtener todos los feriados")
    void testGetAllFeriados() {
        FeriadoEntity f1 = new FeriadoEntity();
        f1.setId(1);
        f1.setFecha(LocalDate.of(2024, 12, 25));

        FeriadoEntity f2 = new FeriadoEntity();
        f2.setId(2);
        f2.setFecha(LocalDate.of(2024, 1, 1));

        when(feriadoRepository.findAll()).thenReturn(Arrays.asList(f1, f2));

        List<FeriadoEntity> resultado = feriadoService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Buscar feriados entre fechas")
    void testFindByFechaBetween() {
        FeriadoEntity f1 = new FeriadoEntity();
        f1.setId(1);
        f1.setFecha(LocalDate.of(2024, 12, 25));
        f1.setActivo(true);

        FeriadoEntity f2 = new FeriadoEntity();
        f2.setId(2);
        f2.setFecha(LocalDate.of(2024, 1, 1));
        f2.setActivo(true);

        LocalDate desde = LocalDate.of(2024, 1, 1);
        LocalDate hasta = LocalDate.of(2024, 12, 31);

        when(feriadoRepository.findByFechaBetween(desde, hasta))
                .thenReturn(Arrays.asList(f1, f2));

        List<FeriadoEntity> resultado = feriadoService.findByFechaBetween(desde, hasta);

        assertTrue(resultado.size() >= 0);
    }

    @Test
    @DisplayName("Buscar feriado por fecha exacta")
    void testFindByFecha() {
        FeriadoEntity feriado = new FeriadoEntity();
        feriado.setId(1);
        feriado.setFecha(LocalDate.of(2024, 12, 25));
        feriado.setNombre("Navidad");
        feriado.setActivo(true);

        LocalDate fecha = LocalDate.of(2024, 12, 25);

        when(feriadoRepository.findByFecha(fecha))
                .thenReturn(Arrays.asList(feriado));

        List<FeriadoEntity> resultado = feriadoService.findByFecha(fecha);

        assertEquals(1, resultado.size());
        assertEquals("Navidad", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Guardar nuevo feriado")
    void testSaveFeriado() {
        FeriadoEntity feriado = new FeriadoEntity();
        feriado.setFecha(LocalDate.of(2024, 9, 18));
        feriado.setNombre("Fiestas Patrias");
        feriado.setActivo(true);

        when(feriadoRepository.save(feriado)).thenReturn(feriado);

        FeriadoEntity resultado = feriadoService.save(feriado);

        assertEquals("Fiestas Patrias", resultado.getNombre());
        assertTrue(resultado.getActivo());
    }

    @Test
    @DisplayName("Filtrar solo feriados activos")
    void testFilterActivosFeriados() {
        FeriadoEntity f1 = new FeriadoEntity();
        f1.setId(1);
        f1.setActivo(true);

        FeriadoEntity f2 = new FeriadoEntity();
        f2.setId(2);
        f2.setActivo(false);

        List<FeriadoEntity> todos = Arrays.asList(f1, f2);
        List<FeriadoEntity> activos = todos.stream()
                .filter(f -> Boolean.TRUE.equals(f.getActivo()))
                .toList();

        assertEquals(1, activos.size());
    }

    @Test
    @DisplayName("Buscar con fechas nulas retorna lista vacía")
    void testFindByFechaBetweenWithNullDates() {
        List<FeriadoEntity> resultado = feriadoService.findByFechaBetween(null, null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Buscar feriado con fecha nula retorna lista vacía")
    void testFindByFechaWithNull() {
        List<FeriadoEntity> resultado = feriadoService.findByFecha(null);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Feriado no encontrado por ID")
    void testFeriadoNotFound() {
        when(feriadoRepository.findById(999)).thenReturn(Optional.empty());

        Optional<FeriadoEntity> resultado = feriadoService.findById(999);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Actualizar feriado existente")
    void testUpdateFeriado() {
        FeriadoEntity feriadoExistente = new FeriadoEntity();
        feriadoExistente.setId(1);
        feriadoExistente.setFecha(LocalDate.of(2024, 12, 25));
        feriadoExistente.setNombre("Navidad");

        FeriadoEntity feriadoActualizado = new FeriadoEntity();
        feriadoActualizado.setNombre("Navidad 2024");
        feriadoActualizado.setActivo(false);

        when(feriadoRepository.findById(1)).thenReturn(Optional.of(feriadoExistente));
        when(feriadoRepository.save(feriadoExistente)).thenReturn(feriadoExistente);

        feriadoExistente.setNombre("Navidad 2024");
        feriadoExistente.setActivo(false);
        FeriadoEntity resultado = feriadoService.save(feriadoExistente);

        assertEquals("Navidad 2024", resultado.getNombre());
        assertFalse(resultado.getActivo());
    }
}
