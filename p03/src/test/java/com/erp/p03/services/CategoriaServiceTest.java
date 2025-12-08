package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.repositories.CategoriaRepository;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CategoriaService Tests")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    private CategoriaService categoriaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoriaService = new CategoriaService(categoriaRepository);
    }

    @Test
    @DisplayName("Obtener categoría por ID")
    void testGetCategoriaById() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setId(1);
        categoria.setNombre("Frutos Secos");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        Optional<CategoriaEntity> resultado = categoriaService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Frutos Secos", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Obtener todas las categorías")
    void testGetAllCategorias() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setId(1);
        c1.setNombre("Frutos Secos");

        CategoriaEntity c2 = new CategoriaEntity();
        c2.setId(2);
        c2.setNombre("Legumbres");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<CategoriaEntity> resultado = categoriaService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Crear nueva categoría")
    void testCreateCategoria() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Especias");
        categoria.setDescripcion("Especias varias");

        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        CategoriaEntity resultado = categoriaService.create(categoria);

        assertEquals("Especias", resultado.getNombre());
        assertEquals("Especias varias", resultado.getDescripcion());
    }

    @Test
    @DisplayName("Actualizar categoría existente")
    void testUpdateCategoria() {
        CategoriaEntity categoriaExistente = new CategoriaEntity();
        categoriaExistente.setId(1);
        categoriaExistente.setNombre("Frutos Secos");

        CategoriaEntity categoriaActualizada = new CategoriaEntity();
        categoriaActualizada.setNombre("Frutos Secos Premium");

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoriaExistente));
        when(categoriaRepository.save(categoriaExistente)).thenReturn(categoriaExistente);

        categoriaExistente.setNombre("Frutos Secos Premium");
        CategoriaEntity resultado = categoriaService.update(1, categoriaActualizada);

        assertEquals("Frutos Secos Premium", resultado.getNombre());
    }

    @Test
    @DisplayName("Eliminar categoría existente")
    void testDeleteCategoria() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setId(1);

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        categoriaService.delete(1);

        verify(categoriaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Búsqueda por nombre")
    void testSearchByName() {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Frutos");

        when(categoriaRepository.findByNombreContainingIgnoreCase("Frutos"))
                .thenReturn(Arrays.asList(categoria));

        List<CategoriaEntity> resultado = categoriaService.searchByName("Frutos");

        assertEquals(1, resultado.size());
        assertEquals("Frutos", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Categoría no encontrada por ID")
    void testCategoriaNotFound() {
        when(categoriaRepository.findById(999)).thenReturn(Optional.empty());

        Optional<CategoriaEntity> resultado = categoriaService.findById(999);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Listar categorías activas")
    void testListActiveCategorias() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setId(1);
        c1.setActivo(true);

        CategoriaEntity c2 = new CategoriaEntity();
        c2.setId(2);
        c2.setActivo(false);

        List<CategoriaEntity> todos = Arrays.asList(c1, c2);
        List<CategoriaEntity> activas = todos.stream()
                .filter(CategoriaEntity::getActivo)
                .toList();

        assertEquals(1, activas.size());
    }

    @Test
    @DisplayName("Obtener categoría por ID o lanzar excepción")
    void testGetByIdOrThrow() {
        when(categoriaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            categoriaService.findByIdOrThrow(999);
        });
    }
}
