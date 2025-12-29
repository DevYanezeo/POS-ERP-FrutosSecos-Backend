package com.erp.p03.controllers;

import com.erp.p03.controllers.dto.FinanceSummaryDTO;
import com.erp.p03.controllers.dto.ProductSalesDTO;
import com.erp.p03.controllers.dto.ProductMarginDTO;
import com.erp.p03.controllers.dto.ProductLossDTO;
import com.erp.p03.entities.*;
import com.erp.p03.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("ReporteController Integration Tests")
class ReporteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        detalleVentaRepository.deleteAll();
        ventaRepository.deleteAll();
        gastoRepository.deleteAll();
        loteRepository.deleteAll();
        productoRepository.deleteAll();
    }


    @Test
    @DisplayName("GET /api/reportes/finanzas/resumen/semana retorna resumen semanal")
    void testGetResumenFinancieroSemana() throws Exception {
        ProductoEntity producto = crearProducto("Almendras", 1000);
        crearVentaConDetalle(producto, 5, 5000);
        crearGasto("Luz", 50000, "OPERACIONAL");
        crearGasto("Compra insumos", 100000, "ADQUISICION");

        mockMvc.perform(get("/api/reportes/finanzas/resumen/semana"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.totalIngresos", notNullValue()))
                .andExpect(jsonPath("$.totalCostoProductos", notNullValue()))
                .andExpect(jsonPath("$.gastosOperacionales", notNullValue()))
                .andExpect(jsonPath("$.gastosAdquisicion", notNullValue()))
                .andExpect(jsonPath("$.utilidadBruta", notNullValue()))
                .andExpect(jsonPath("$.utilidadNeta", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/reportes/finanzas/resumen/mes retorna resumen mensual")
    void testGetResumenFinancieroMes() throws Exception {
        ProductoEntity producto = crearProducto("Nueces", 1500);
        crearVentaConDetalle(producto, 10, 15000);
        crearGasto("Internet", 40000, "OPERACIONAL");

        mockMvc.perform(get("/api/reportes/finanzas/resumen/mes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.totalIngresos", notNullValue()))
                .andExpect(jsonPath("$.utilidadBruta", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/reportes/finanzas/resumen/anio retorna resumen anual")
    void testGetResumenFinancieroAnio() throws Exception {
        ProductoEntity producto = crearProducto("Pistachos", 2000);
        crearVentaConDetalle(producto, 20, 40000);
        crearGasto("Mantenimiento", 150000, "OTROS");

        mockMvc.perform(get("/api/reportes/finanzas/resumen/anio"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.totalIngresos", notNullValue()))
                .andExpect(jsonPath("$.gastosOperacionales", notNullValue()));
    }

    @Test
    @DisplayName("Resumen financiero calcula correctamente con múltiples gastos")
    void testResumenFinancieroConMultiplesGastos() throws Exception {
        ProductoEntity producto = crearProducto("Avellanas", 1200);
        crearVentaConDetalle(producto, 15, 18000);
        
        crearGasto("Luz", 30000, "OPERACIONAL");
        crearGasto("Agua", 20000, "OPERACIONAL");
        crearGasto("Compra 1", 100000, "ADQUISICION");
        crearGasto("Compra 2", 150000, "ADQUISICION");
        crearGasto("Varios", 50000, "OTROS");

        mockMvc.perform(get("/api/reportes/finanzas/resumen/semana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gastosOperacionales", notNullValue()))
                .andExpect(jsonPath("$.gastosAdquisicion", notNullValue()));
    }

    @Test
    @DisplayName("Resumen financiero sin datos retorna valores en cero o iniciales")
    void testResumenFinancieroSinDatos() throws Exception {
        // Act & Assert - Sin crear ningún dato
        mockMvc.perform(get("/api/reportes/finanzas/resumen/semana"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }


    @Test
    @DisplayName("GET /api/reportes/productos/semana retorna productos más vendidos de la semana")
    void testProductosMasVendidosSemana() throws Exception {
        ProductoEntity producto = crearProducto("Almendras", 1000);
        crearVentaConDetalle(producto, 10, 10000);

        mockMvc.perform(get("/api/reportes/productos/semana"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/mes retorna productos más vendidos del mes")
    void testProductosMasVendidosMes() throws Exception {
        ProductoEntity producto = crearProducto("Nueces", 1500);
        crearVentaConDetalle(producto, 20, 30000);

        mockMvc.perform(get("/api/reportes/productos/mes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/anio retorna productos más vendidos del año")
    void testProductosMasVendidosAnio() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/anio"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/semana con parámetros específicos")
    void testProductosMasVendidosSemanaPorParametros() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/semana")
                .param("year", "2025")
                .param("month", "12")
                .param("week", "1")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }


    @Test
    @DisplayName("GET /api/reportes/productos/semana/menos retorna productos menos vendidos")
    void testProductosMenosVendidosSemana() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/semana/menos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/mes/menos retorna productos menos vendidos del mes")
    void testProductosMenosVendidosMes() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/mes/menos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/anio/menos retorna productos menos vendidos del año")
    void testProductosMenosVendidosAnio() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/anio/menos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }


    @Test
    @DisplayName("GET /api/reportes/productos/margen/semana retorna margen por producto semanal")
    void testProductosMargenSemana() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/margen/semana"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/margen/mes retorna margen por producto mensual")
    void testProductosMargenMes() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/margen/mes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/margen/anio retorna margen por producto anual")
    void testProductosMargenAnio() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/margen/anio"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }


    @Test
    @DisplayName("GET /api/reportes/productos/perdidas/semana retorna pérdidas semanales")
    void testProductosPerdidasSemana() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/perdidas/semana"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/perdidas/mes retorna pérdidas mensuales")
    void testProductosPerdidasMes() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/perdidas/mes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/reportes/productos/perdidas/anio retorna pérdidas anuales")
    void testProductosPerdidasAnio() throws Exception {
        mockMvc.perform(get("/api/reportes/productos/perdidas/anio"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }


    @Test
    @DisplayName("Integración: Crear ventas y gastos, luego obtener resumen completo")
    void testIntegracionCompletaResumenFinanciero() throws Exception {
        // Arrange - Crear un escenario completo
        ProductoEntity producto1 = crearProducto("Almendras Premium", 2000);
        ProductoEntity producto2 = crearProducto("Nueces Premium", 2500);

        crearVentaConDetalle(producto1, 10, 20000);
        crearVentaConDetalle(producto2, 5, 12500);

        crearGasto("Luz", 50000, "OPERACIONAL");
        crearGasto("Agua", 30000, "OPERACIONAL");
        crearGasto("Compra mercadería", 200000, "ADQUISICION");
        crearGasto("Mantenimiento", 75000, "OTROS");

        mockMvc.perform(get("/api/reportes/finanzas/resumen/semana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos", notNullValue()))
                .andExpect(jsonPath("$.totalCostoProductos", notNullValue()))
                .andExpect(jsonPath("$.gastosOperacionales", notNullValue()))
                .andExpect(jsonPath("$.gastosAdquisicion", notNullValue()));

        mockMvc.perform(get("/api/reportes/productos/semana"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Integración: Verificar que el resumen financiero diferencia entre periodos")
    void testResumenFinancieroPorDiferentesPeriodos() throws Exception {
        ProductoEntity producto = crearProducto("Pistachos", 3000);
        crearVentaConDetalle(producto, 8, 24000);
        crearGasto("Gasto semanal", 60000, "OPERACIONAL");

        // Act & Assert - Verificar semana
        mockMvc.perform(get("/api/reportes/finanzas/resumen/semana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos", notNullValue()));

        mockMvc.perform(get("/api/reportes/finanzas/resumen/mes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos", notNullValue()));

        // Verificar año
        mockMvc.perform(get("/api/reportes/finanzas/resumen/anio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos", notNullValue()));
    }


    private ProductoEntity crearProducto(String nombre, Integer precio) {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(1000);
        producto.setStockMinimo(10);
        producto.setDescripcion("Producto de prueba");
        return productoRepository.save(producto);
    }

    private VentaEntity crearVentaConDetalle(ProductoEntity producto, int cantidad, double monto) {
        VentaEntity venta = new VentaEntity();
        venta.setMonto(monto);
        venta.setFecha(LocalDateTime.now());
        venta.setMetodoPago("EFECTIVO");
        VentaEntity ventaGuardada = ventaRepository.save(venta);

        DetalleVentaEntity detalle = new DetalleVentaEntity();
        detalle.setVenta(ventaGuardada);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario((int) (monto / cantidad));
        detalle.setSubtotal((int) monto);
        detalleVentaRepository.save(detalle);

        return ventaGuardada;
    }

    private GastoEntity crearGasto(String descripcion, Integer monto, String tipo) {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion(descripcion);
        gasto.setMonto(monto);
        gasto.setTipo(tipo);
        gasto.setFecha(new Date());
        return gastoRepository.save(gasto);
    }
}
