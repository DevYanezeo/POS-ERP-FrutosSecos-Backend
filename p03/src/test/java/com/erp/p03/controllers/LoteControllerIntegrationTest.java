package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.LoteEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.LoteRepository;
import com.erp.p03.repositories.ProductoRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("LoteController Integration Tests")
class LoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/lote/crear crea lote")
    void testCreateLote() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Almendra");
        ProductoEntity productoGuardado = productoRepository.save(producto);

        LoteEntity lote = new LoteEntity();
        lote.setNumeroLote("LOTE-001");
        lote.setCantidad(100);
        lote.setFechaVencimiento(LocalDate.of(2025, 12, 31));
        lote.setProducto(productoGuardado);

        mockMvc.perform(post("/api/lote/crear")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(lote)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/lote/producto/{id} lista lotes por producto")
    void testListarLotesPorProducto() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Avellana");
        ProductoEntity productoGuardado = productoRepository.save(producto);

        LoteEntity lote = new LoteEntity();
        lote.setNumeroLote("LOTE-002");
        lote.setCantidad(50);
        lote.setFechaVencimiento(LocalDate.of(2025, 12, 31));
        lote.setProducto(productoGuardado);
        loteRepository.save(lote);

        mockMvc.perform(get("/api/lote/producto/" + productoGuardado.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/lote/vencimiento lista próximos a vencer")
    void testVencimientosProximos() throws Exception {
        mockMvc.perform(get("/api/lote/vencimiento")
                .param("dias", "30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/lote/alertas genera alertas vencimiento")
    void testAlertasVencimiento() throws Exception {
        mockMvc.perform(get("/api/lote/alertas")
                .param("dias", "30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/lote/vencimiento default 30 días")
    void testVencimientosProximosDefault() throws Exception {
        mockMvc.perform(get("/api/lote/vencimiento"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/lote/alertas default 30 días")
    void testAlertasVencimientoDefault() throws Exception {
        mockMvc.perform(get("/api/lote/alertas"))
                .andExpect(status().isOk());
    }
}
