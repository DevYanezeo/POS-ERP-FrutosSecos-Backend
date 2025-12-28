package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.controllers.dto.MovimientoStockRequest;
import com.erp.p03.repositories.ProductoRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("MovimientoStockController Integration Tests")
class MovimientoStockControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/movimientos registra ingreso")
    void testRegistrarIngreso() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test");
        ProductoEntity guardado = productoRepository.save(producto);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(guardado.getId());
        request.setCantidad(50);
        request.setTipoMovimiento("INGRESO");

        mockMvc.perform(post("/api/movimientos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/movimientos registra salida")
    void testRegistrarSalida() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test");
        producto.setStock(100);
        ProductoEntity guardado = productoRepository.save(producto);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(guardado.getId());
        request.setCantidad(30);
        request.setTipoMovimiento("SALIDA");

        mockMvc.perform(post("/api/movimientos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/movimientos registra venta")
    void testRegistrarVenta() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test");
        producto.setStock(100);
        ProductoEntity guardado = productoRepository.save(producto);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(guardado.getId());
        request.setCantidad(25);
        request.setTipoMovimiento("VENTA");

        mockMvc.perform(post("/api/movimientos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/movimientos registra merma")
    void testRegistrarMerma() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test");
        producto.setStock(100);
        ProductoEntity guardado = productoRepository.save(producto);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(guardado.getId());
        request.setCantidad(10);
        request.setTipoMovimiento("MERMA");

        mockMvc.perform(post("/api/movimientos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/movimientos registra ajuste positivo")
    void testRegistrarAjustePositivo() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test");
        producto.setStock(100);
        ProductoEntity guardado = productoRepository.save(producto);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(guardado.getId());
        request.setCantidad(20);
        request.setTipoMovimiento("AJUSTE_POSITIVO");

        mockMvc.perform(post("/api/movimientos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/movimientos stock insuficiente retorna error")
    void testStockInsuficiente() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test");
        producto.setStock(10);
        ProductoEntity guardado = productoRepository.save(producto);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(guardado.getId());
        request.setCantidad(50);
        request.setTipoMovimiento("SALIDA");

        mockMvc.perform(post("/api/movimientos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/movimientos tipo inválido retorna error")
    void testTipoMovimientoInvalido() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test");
        ProductoEntity guardado = productoRepository.save(producto);

        MovimientoStockRequest request = new MovimientoStockRequest();
        request.setProductoId(guardado.getId());
        request.setCantidad(10);
        request.setTipoMovimiento("TIPO_INVALIDO");

        mockMvc.perform(post("/api/movimientos")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/movimientos/historial lista movimientos")
    void testGetHistorialMovimientos() throws Exception {
        mockMvc.perform(get("/api/movimientos/historial"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
