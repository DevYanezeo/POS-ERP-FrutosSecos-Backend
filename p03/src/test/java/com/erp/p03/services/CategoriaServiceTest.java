package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.repositories.CategoriaRepository;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CategoriaService Tests")
class CategoriaServiceTest {

    @Mock private CategoriaRepository categoriaRepository;

    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoriaService = new CategoriaService(categoriaRepository);
    }

    @Test
    @DisplayName("Obtener todas las categorías")
    void testGetAllCategorias() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setIdCategoria(1);
        c1.setNombre("Frutos Secos");

        CategoriaEntity c2 = new CategoriaEntity();
        c2.setIdCategoria(2);
        c2.setNombre("Legumbres");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<CategoriaEntity> resultado = categoriaService.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Frutos Secos", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Crear nueva categoría")
    void testCrearCategoria() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Especias");

        when(categoriaRepository.save(any(CategoriaEntity.class))).thenReturn(categoria);

        CategoriaEntity resultado = categoriaService.crearCategoria(categoria);

        assertNotNull(resultado);
        assertEquals("Especias", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }
}
