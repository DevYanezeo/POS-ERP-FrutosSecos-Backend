package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.MovimientoStockEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.MovimientoStockRepository;
import com.erp.p03.repositories.ProductoRepository;
import com.erp.p03.controllers.dto.MovimientoStockRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MovimientoStockService Tests")
class MovimientoStockServiceTest {

    @Mock
    private MovimientoStockRepository movimientoStockRepository;

    @Mock
    private ProductoRepository productoRepository;

    private MovimientoStockService movimientoStockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movimientoStockService = new MovimientoStockService(movimientoStockRepository);
        movimientoStockService.productoRepository = productoRepository;
    }

    @Test
    @DisplayName("Registrar movimiento de ingreso")
    void testRegistrarMovimientoIngreso() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(50);
        request.setTipoMovimiento("INGRESO");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(movimientoStockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimientoStockEntity resultado = movimientoStockService.registrarMovimiento(request);

        assertEquals(150, producto.getStock());
    }

    @Test
    @DisplayName("Registrar movimiento de salida")
    void testRegistrarMovimientoSalida() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(30);
        request.setTipoMovimiento("SALIDA");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(movimientoStockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimientoStockEntity resultado = movimientoStockService.registrarMovimiento(request);

        assertEquals(70, producto.getStock());
    }

    @Test
    @DisplayName("Registrar movimiento de venta")
    void testRegistrarMovimientoVenta() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(25);
        request.setTipoMovimiento("VENTA");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(movimientoStockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimientoStockEntity resultado = movimientoStockService.registrarMovimiento(request);

        assertEquals(75, producto.getStock());
    }

    @Test
    @DisplayName("Registrar movimiento de merma")
    void testRegistrarMovimientoMerma() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(10);
        request.setTipoMovimiento("MERMA");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(movimientoStockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimientoStockEntity resultado = movimientoStockService.registrarMovimiento(request);

        assertEquals(90, producto.getStock());
    }

    @Test
    @DisplayName("Registrar movimiento de vencimiento")
    void testRegistrarMovimientoVencimiento() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(5);
        request.setTipoMovimiento("VENCIMIENTO");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(movimientoStockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimientoStockEntity resultado = movimientoStockService.registrarMovimiento(request);

        assertEquals(95, producto.getStock());
    }

    @Test
    @DisplayName("Registrar ajuste positivo")
    void testRegistrarAjustePositivo() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(20);
        request.setTipoMovimiento("AJUSTE_POSITIVO");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(movimientoStockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimientoStockEntity resultado = movimientoStockService.registrarMovimiento(request);

        assertEquals(120, producto.getStock());
    }

    @Test
    @DisplayName("Registrar ajuste negativo")
    void testRegistrarAjusteNegativo() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(15);
        request.setTipoMovimiento("AJUSTE_NEGATIVO");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(producto)).thenReturn(producto);
        when(movimientoStockRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MovimientoStockEntity resultado = movimientoStockService.registrarMovimiento(request);

        assertEquals(85, producto.getStock());
    }

    @Test
    @DisplayName("Stock insuficiente lanza excepción")
    void testStockInsuficienteLanzaExcepcion() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(10);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(20);
        request.setTipoMovimiento("SALIDA");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(RuntimeException.class, () -> {
            movimientoStockService.registrarMovimiento(request);
        });
    }

    @Test
    @DisplayName("Tipo de movimiento no soportado lanza excepción")
    void testTipoMovimientoNoSoportadoLanzaExcepcion() {
        ProductoEntity producto = new ProductoEntity();
        producto.setId(1);
        producto.setStock(100);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(1);
        request.setCantidad(10);
        request.setTipoMovimiento("TIPO_INVALIDO");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        assertThrows(RuntimeException.class, () -> {
            movimientoStockService.registrarMovimiento(request);
        });
    }

    @Test
    @DisplayName("Producto no encontrado lanza excepción")
    void testProductoNoEncontradoLanzaExcepcion() {
        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(999);
        request.setCantidad(10);
        request.setTipoMovimiento("INGRESO");

        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            movimientoStockService.registrarMovimiento(request);
        });
    }
}
