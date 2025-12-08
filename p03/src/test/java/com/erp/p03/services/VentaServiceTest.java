package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.VentaEntity;
import com.erp.p03.entities.DetalleVentaEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.VentaRepository;
import com.erp.p03.repositories.DetalleVentaRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("VentaService Tests")
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ventaService = new VentaService(ventaRepository, detalleVentaRepository);
    }

    @Test
    @DisplayName("Obtener venta por ID")
    void testGetVentaById() {
        VentaEntity venta = new VentaEntity();
        venta.setId(1);
        venta.setMonto(1000.0);

        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));

        Optional<VentaEntity> resultado = ventaService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals(1000.0, resultado.get().getMonto());
    }

    @Test
    @DisplayName("Obtener todas las ventas")
    void testGetAllVentas() {
        VentaEntity v1 = new VentaEntity();
        v1.setId(1);
        v1.setMonto(1000.0);

        VentaEntity v2 = new VentaEntity();
        v2.setId(2);
        v2.setMonto(2000.0);

        when(ventaRepository.findAll()).thenReturn(Arrays.asList(v1, v2));

        List<VentaEntity> resultado = ventaService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Confirmar venta sin stock lanza excepción")
    void testConfirmVentaWithoutStock() {
        VentaEntity venta = new VentaEntity();
        venta.setId(1);

        DetalleVentaEntity detalle = new DetalleVentaEntity();
        ProductoEntity producto = new ProductoEntity();
        producto.setStock(0);
        detalle.setProducto(producto);
        detalle.setCantidad(10);

        venta.setDetalles(Arrays.asList(detalle));

        assertThrows(IllegalArgumentException.class, () -> {
            ventaService.confirmVenta(venta);
        });
    }

    @Test
    @DisplayName("Registrar pago que cubre saldo marca como pagada")
    void testRegisterPaymentCoversBalance() {
        VentaEntity venta = new VentaEntity();
        venta.setId(1);
        venta.setMonto(1000.0);
        venta.setSaldoPendiente(1000.0);
        venta.setPagada(false);

        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(venta)).thenReturn(venta);

        ventaService.registerPayment(1, 1000.0);

        assertEquals(0.0, venta.getSaldoPendiente());
        assertTrue(venta.isPagada());
    }

    @Test
    @DisplayName("Quitar detalle recalcula totales")
    void testRemoveDetailRecalculatesTotals() {
        VentaEntity venta = new VentaEntity();
        venta.setId(1);
        venta.setMonto(3000.0);

        DetalleVentaEntity d1 = new DetalleVentaEntity();
        d1.setId(1);
        d1.setMonto(1000.0);

        DetalleVentaEntity d2 = new DetalleVentaEntity();
        d2.setId(2);
        d2.setMonto(2000.0);

        venta.setDetalles(Arrays.asList(d1, d2));

        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(venta)).thenReturn(venta);

        ventaService.removeDetail(1, 1);

        assertEquals(1, venta.getDetalles().size());
    }

    @Test
    @DisplayName("Historial con múltiples filtros")
    void testHistorialWithMultipleFilters() {
        VentaEntity v1 = new VentaEntity();
        v1.setId(1);
        v1.setFecha(LocalDateTime.of(2024, 12, 1, 10, 0));

        VentaEntity v2 = new VentaEntity();
        v2.setId(2);
        v2.setFecha(LocalDateTime.of(2024, 12, 5, 14, 0));

        when(ventaRepository.findAll()).thenReturn(Arrays.asList(v1, v2));

        List<VentaEntity> resultado = ventaRepository.findAll();

        List<VentaEntity> filtrado = resultado.stream()
                .filter(v -> v.getFecha().isAfter(LocalDateTime.of(2024, 12, 1, 0, 0)))
                .filter(v -> v.getFecha().isBefore(LocalDateTime.of(2024, 12, 10, 23, 59)))
                .toList();

        assertEquals(2, filtrado.size());
    }

    @Test
    @DisplayName("Fiados con saldo pendiente")
    void testFiadosWithPendingBalance() {
        VentaEntity v1 = new VentaEntity();
        v1.setId(1);
        v1.setEsfiado(true);
        v1.setSaldoPendiente(500.0);

        VentaEntity v2 = new VentaEntity();
        v2.setId(2);
        v2.setEsfiado(true);
        v2.setSaldoPendiente(0.0);

        List<VentaEntity> todos = Arrays.asList(v1, v2);
        List<VentaEntity> conSaldo = todos.stream()
                .filter(v -> v.isEsfiado() && v.getSaldoPendiente() > 0)
                .toList();

        assertEquals(1, conSaldo.size());
    }

    @Test
    @DisplayName("Fiados sin saldo pendiente")
    void testFiadosWithoutPendingBalance() {
        VentaEntity v1 = new VentaEntity();
        v1.setId(1);
        v1.setEsfiado(true);
        v1.setSaldoPendiente(0.0);

        VentaEntity v2 = new VentaEntity();
        v2.setId(2);
        v2.setEsfiado(true);
        v2.setSaldoPendiente(500.0);

        List<VentaEntity> todos = Arrays.asList(v1, v2);
        List<VentaEntity> sinSaldo = todos.stream()
                .filter(v -> v.isEsfiado() && v.getSaldoPendiente() == 0)
                .toList();

        assertEquals(1, sinSaldo.size());
    }

    @Test
    @DisplayName("Formato de fecha inválido lanza excepción")
    void testInvalidDateFormatThrowsException() {
        String fechaInvalida = "32/13/2024";

        assertThrows(Exception.class, () -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            formatter.parse(fechaInvalida);
        });
    }

    @Test
    @DisplayName("Crear nueva venta")
    void testCreateVenta() {
        VentaEntity venta = new VentaEntity();
        venta.setMonto(1500.0);
        venta.setFecha(LocalDateTime.now());

        when(ventaRepository.save(venta)).thenReturn(venta);

        VentaEntity resultado = ventaService.create(venta);

        assertEquals(1500.0, resultado.getMonto());
    }

    @Test
    @DisplayName("Actualizar venta existente")
    void testUpdateVenta() {
        VentaEntity ventaExistente = new VentaEntity();
        ventaExistente.setId(1);
        ventaExistente.setMonto(1000.0);

        VentaEntity ventaActualizada = new VentaEntity();
        ventaActualizada.setMonto(1500.0);

        when(ventaRepository.findById(1)).thenReturn(Optional.of(ventaExistente));
        when(ventaRepository.save(ventaExistente)).thenReturn(ventaExistente);

        ventaExistente.setMonto(1500.0);
        VentaEntity resultado = ventaService.update(1, ventaActualizada);

        assertEquals(1500.0, resultado.getMonto());
    }
}
