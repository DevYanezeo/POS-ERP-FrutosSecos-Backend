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
import com.erp.p03.repositories.PagoRepository;
import com.erp.p03.controllers.dto.VentaRequest;
import com.erp.p03.controllers.dto.DetalleVentaRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("VentaService Tests")
class VentaServiceTest {

    @Mock private VentaRepository ventaRepository;
    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private MovimientoStockService movimientoStockService;
    @Mock private ProductoService productoService;
    @Mock private LoteService loteService;
    @Mock private FeriadoService feriadoService;
    @Mock private GastoService gastoService;
    @Mock private PagoRepository pagoRepository;
    @Mock private ClienteFiadoService clienteFiadoService;

    private VentaService ventaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ventaService = new VentaService(
            ventaRepository,
            detalleVentaRepository,
            movimientoStockService,
            productoService,
            loteService,
            feriadoService,
            gastoService,
            pagoRepository,
            clienteFiadoService
        );
    }

    @Test
    @DisplayName("Obtener venta por ID")
    void testGetVentaById() {
        VentaEntity venta = new VentaEntity();
        venta.setIdVenta(1);
        venta.setTotal(1000);

        when(ventaRepository.findById(1)).thenReturn(Optional.of(venta));

        VentaEntity resultado = ventaService.findById(1);

        assertNotNull(resultado);
        assertEquals(1000, resultado.getTotal());
    }

    @Test
    @DisplayName("Obtener todas las ventas")
    void testGetAllVentas() {
        VentaEntity v1 = new VentaEntity();
        v1.setIdVenta(1);
        VentaEntity v2 = new VentaEntity();
        v2.setIdVenta(2);

        when(ventaRepository.findAll()).thenReturn(Arrays.asList(v1, v2));

        List<VentaEntity> resultado = ventaService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Confirmar venta - Flujo básico")
    void testConfirmarVenta() {
        VentaRequest request = new VentaRequest();
        request.setUsuarioId(1);
        request.setTotal(1000);
        
        DetalleVentaRequest dreq = new DetalleVentaRequest();
        dreq.setProductoId(1);
        dreq.setCantidad(2);
        dreq.setPrecioUnitario(500);
        request.setDetalles(Arrays.asList(dreq));

        VentaEntity savedVenta = new VentaEntity();
        savedVenta.setIdVenta(100);
        when(ventaRepository.save(any(VentaEntity.class))).thenReturn(savedVenta);
        
        ProductoEntity producto = new ProductoEntity();
        producto.setIdProducto(1);
        producto.setLotes(new ArrayList<>());
        when(productoService.findById(1)).thenReturn(Optional.of(producto));

        // Debería fallar porque no hay lotes
        assertThrows(RuntimeException.class, () -> {
            ventaService.confirmarVenta(request);
        });
    }

    @Test
    @DisplayName("Listar fiados pendientes")
    void testListarFiadosPendientes() {
        VentaEntity v1 = new VentaEntity();
        v1.setFiado(true);
        v1.setSaldoPendiente(500);

        when(ventaRepository.findByFiadoTrueAndSaldoPendienteGreaterThan(0))
            .thenReturn(Arrays.asList(v1));

        List<VentaEntity> result = ventaService.listarFiados(true);

        assertEquals(1, result.size());
        assertEquals(500, result.get(0).getSaldoPendiente());
    }
}
