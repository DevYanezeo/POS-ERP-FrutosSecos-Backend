package com.erp.p03.services;

import com.erp.p03.controllers.dto.ProductSalesDTO;
import com.erp.p03.controllers.dto.ProductMarginDTO;
import com.erp.p03.controllers.dto.ProductLossDTO;
import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("QA Tests for Jehison and Isidora Changes")
class QATest {

    @Mock private VentaRepository ventaRepository;
    @Mock private DetalleVentaRepository detalleVentaRepository;
    @Mock private MovimientoStockService movimientoStockService;
    @Mock private ProductoService productoService;
    @Mock private LoteService loteService;
    @Mock private FeriadoService feriadoService;
    @Mock private GastoService gastoService;
    @Mock private PagoRepository pagoRepository;
    @Mock private ClienteFiadoService clienteFiadoService;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private LoteRepository loteRepository;
    @Mock private ProductoRepository productoRepository;

    private VentaService ventaService;
    private CategoriaService categoriaService;
    private LoteService realLoteService;

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
        categoriaService = new CategoriaService(categoriaRepository);
        realLoteService = new LoteService(loteRepository, productoRepository);
    }

    @Test
    @DisplayName("QA - Jehison R2: Productos más vendidos")
    void testProductosMasVendidos() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        Object[] row1 = new Object[]{BigInteger.valueOf(1), "Producto A", BigDecimal.valueOf(10), BigDecimal.valueOf(1000)};
        List<Object[]> mockRows = Arrays.asList(row1);
        
        when(detalleVentaRepository.findProductSalesBetweenDates(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(mockRows);

        List<ProductSalesDTO> result = ventaService.productosMasVendidosEntre(start, end, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto A", result.get(0).getNombre());
        assertEquals(10, result.get(0).getTotalCantidad());
    }

    @Test
    @DisplayName("QA - Jehison R2: Productos menos vendidos")
    void testProductosMenosVendidos() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        Object[] row1 = new Object[]{1, "Producto B", 2, 200};
        List<Object[]> mockRows = Arrays.asList(row1);
        
        when(detalleVentaRepository.findLeastProductSalesBetweenDates(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(mockRows);

        List<ProductSalesDTO> result = ventaService.productosMenosVendidosEntre(start, end, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto B", result.get(0).getNombre());
        assertEquals(2, result.get(0).getTotalCantidad());
    }

    @Test
    @DisplayName("QA - Jehison R3: Margen de ganancias")
    void testProductosMargen() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        Object[] row1 = new Object[]{1, "Producto C", 5, 5000, 3000};
        List<Object[]> mockRows = Arrays.asList(row1);
        
        when(detalleVentaRepository.findProductMarginBetweenDates(any(Timestamp.class), any(Timestamp.class)))
                .thenReturn(mockRows);
        
        ProductoEntity mockProd = new ProductoEntity();
        mockProd.setIdProducto(1);
        mockProd.setPrecio(1000);
        when(productoService.findById(1)).thenReturn(Optional.of(mockProd));

        List<ProductMarginDTO> result = ventaService.productosMargenEntre(start, end, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto C", result.get(0).getNombre());
        assertEquals(2000, result.get(0).getMargen()); // 5000 - 3000
    }

    @Test
    @DisplayName("QA - Jehison R4: Pérdidas por vencimiento")
    void testProductosPerdidas() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        LoteEntity lote = new LoteEntity();
        lote.setIdLote(1);
        lote.setCantidad(5);
        lote.setCosto(100);
        ProductoEntity prod = new ProductoEntity();
        prod.setIdProducto(1);
        prod.setNombre("Producto D");
        lote.setProducto(prod);
        
        when(loteService.findLotesByFechaVencimientoBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(lote));

        List<ProductLossDTO> result = ventaService.productosPerdidasEntre(start, end, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto D", result.get(0).getNombre());
        assertEquals(500, result.get(0).getTotalPerdida()); // 5 * 100
    }

    @Test
    @DisplayName("QA - Isidora: Crear Categoría")
    void testCrearCategoria() {
        CategoriaEntity cat = new CategoriaEntity();
        cat.setNombre("Nueva Categoria");
        
        when(categoriaRepository.save(any(CategoriaEntity.class))).thenReturn(cat);
        
        CategoriaEntity result = categoriaService.crearCategoria(cat);
        
        assertNotNull(result);
        assertEquals("Nueva Categoria", result.getNombre());
        verify(categoriaRepository, times(1)).save(cat);
    }

    @Test
    @DisplayName("QA - Isidora: Listar todos los códigos de lote")
    void testFindAllCodigosLote() {
        LoteEntity l1 = new LoteEntity();
        l1.setCodigoLote("COD1");
        LoteEntity l2 = new LoteEntity();
        l2.setCodigoLote("COD2");
        
        when(loteRepository.findAll()).thenReturn(Arrays.asList(l1, l2));
        
        List<String> result = realLoteService.findAllCodigosLote();
        
        assertEquals(2, result.size());
        assertTrue(result.contains("COD1"));
        assertTrue(result.contains("COD2"));
    }
}
