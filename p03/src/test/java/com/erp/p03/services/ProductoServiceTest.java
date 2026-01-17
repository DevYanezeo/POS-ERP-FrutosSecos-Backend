package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.ProductoRepository;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProductoService Tests")
class ProductoServiceTest {

    @Mock private ProductoRepository productoRepository;

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productoService = new ProductoService(productoRepository);
    }

    @Test
    @DisplayName("Obtener producto por ID")
    void testGetProductoById() {
        ProductoEntity producto = new ProductoEntity();
        producto.setIdProducto(1);
        producto.setNombre("Almendra");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        Optional<ProductoEntity> resultado = productoService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Almendra", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Obtener todos los productos")
    void testGetAllProductos() {
        ProductoEntity p1 = new ProductoEntity();
        p1.setIdProducto(1);
        ProductoEntity p2 = new ProductoEntity();
        p2.setIdProducto(2);

        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<ProductoEntity> resultado = productoService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Guardar producto")
    void testSaveProducto() {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Nuez");

        when(productoRepository.save(any(ProductoEntity.class))).thenReturn(producto);

        ProductoEntity resultado = productoService.save(producto);

        assertNotNull(resultado);
        assertEquals("Nuez", resultado.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }
}
