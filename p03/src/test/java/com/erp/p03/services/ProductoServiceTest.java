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

    @Mock
    private ProductoRepository productoRepository;

    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productoService = new ProductoService(productoRepository);
    }

    @Test
    @DisplayName("Obtener producto por ID existente")
    void testGetProductoById() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setNombre("Almendra");
        producto.setStock(100);

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        Optional<ProductoEntity> resultado = productoService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Almendra", resultado.get().getNombre());
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Obtener producto por ID no existente")
    void testGetProductoByIdNotFound() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        Optional<ProductoEntity> resultado = productoService.findById(999);

        assertFalse(resultado.isPresent());
        verify(productoRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Obtener todos los productos")
    void testGetAllProductos() {
        ProductoEntity p1 = new ProductoEntity();
        p1.setId(1);
        p1.setNombre("Almendra");

        ProductoEntity p2 = new ProductoEntity();
        p2.setId(2);
        p2.setNombre("Avellana");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<ProductoEntity> resultado = productoService.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Almendra", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Crear nuevo producto")
    void testCreateProducto() {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Nuez");
        producto.setStock(50);

        when(productoRepository.save(producto)).thenReturn(producto);

        ProductoEntity resultado = productoService.create(producto);

        assertEquals("Nuez", resultado.getNombre());
        assertEquals(50, resultado.getStock());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    @DisplayName("Actualizar producto existente")
    void testUpdateProducto() {
        ProductoEntity productoExistente = new ProductoEntity();
        productoExistente.setId(1);
        productoExistente.setNombre("Almendra");
        productoExistente.setStock(100);

        ProductoEntity productoActualizado = new ProductoEntity();
        productoActualizado.setNombre("Almendra Premium");
        productoActualizado.setStock(150);

        when(productoRepository.findById(1)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(productoExistente)).thenReturn(productoExistente);

        ProductoEntity resultado = productoService.update(1, productoActualizado);

        assertEquals("Almendra Premium", resultado.getNombre());
        assertEquals(150, resultado.getStock());
        verify(productoRepository, times(1)).save(productoExistente);
    }

    @Test
    @DisplayName("Quitar stock del disponible")
    void testRemoveStock() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);

        ProductoEntity resultado = productoService.removeStock(1, 30);

        assertEquals(70, resultado.getStock());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    @DisplayName("Quitar más stock del disponible lanza excepción")
    void testRemoveStockExceedsAvailable() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(50);

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(IllegalArgumentException.class, () -> {
            productoService.removeStock(1, 100);
        });
    }

    @Test
    @DisplayName("Detectar productos con stock bajo")
    void testLowStockProducts() {
        ProductoEntity p1 = new ProductoEntity();
        p1.setId(1);
        p1.setStock(5);

        ProductoEntity p2 = new ProductoEntity();
        p2.setId(2);
        p2.setStock(100);

        when(productoRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<ProductoEntity> bajoStock = productoService.findLowStock(10);

        assertEquals(1, bajoStock.size());
        assertEquals(5, bajoStock.get(0).getStock());
    }

    @Test
    @DisplayName("Buscar productos por nombre")
    void testSearchProductosByName() {
        ProductoEntity p1 = new ProductoEntity();
        p1.setId(1);
        p1.setNombre("Almendra");

        when(productoRepository.findByNombreContainingIgnoreCase("Almendra"))
                .thenReturn(Arrays.asList(p1));

        List<ProductoEntity> resultado = productoService.searchByName("Almendra");

        assertEquals(1, resultado.size());
        assertEquals("Almendra", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Obtener productos por estado")
    void testGetProductosByEstado() {
        ProductoEntity p1 = new ProductoEntity();
        p1.setId(1);
        p1.setEstado("Disponible");

        when(productoRepository.findByEstado("Disponible"))
                .thenReturn(Arrays.asList(p1));

        List<ProductoEntity> resultado = productoService.findByEstado("Disponible");

        assertEquals(1, resultado.size());
        assertEquals("Disponible", resultado.get(0).getEstado());
    }

    @Test
    @DisplayName("Producto no encontrado lanza excepción")
    void testProductoNotFoundThrowsException() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            productoService.findByIdOrThrow(999);
        });
    }

    @Test
    @DisplayName("Ordenamiento por nombre ascendente")
    void testSortByNameAscending() {
        ProductoEntity p1 = new ProductoEntity();
        p1.setNombre("Nuez");

        ProductoEntity p2 = new ProductoEntity();
        p2.setNombre("Almendra");

        List<ProductoEntity> productos = Arrays.asList(p1, p2);
        productos.sort((a, b) -> a.getNombre().compareTo(b.getNombre()));

        assertEquals("Almendra", productos.get(0).getNombre());
        assertEquals("Nuez", productos.get(1).getNombre());
    }

    @Test
    @DisplayName("Filtro por múltiples criterios")
    void testFilterByMultipleCriteria() {
        ProductoEntity p1 = new ProductoEntity();
        p1.setId(1);
        p1.setNombre("Almendra");
        p1.setEstado("Disponible");
        p1.setStock(100);

        ProductoEntity p2 = new ProductoEntity();
        p2.setId(2);
        p2.setNombre("Avellana");
        p2.setEstado("Agotado");
        p2.setStock(0);

        List<ProductoEntity> todos = Arrays.asList(p1, p2);

        List<ProductoEntity> filtrados = todos.stream()
                .filter(p -> "Disponible".equals(p.getEstado()))
                .filter(p -> p.getStock() > 50)
                .toList();

        assertEquals(1, filtrados.size());
        assertEquals("Almendra", filtrados.get(0).getNombre());
    }
}
