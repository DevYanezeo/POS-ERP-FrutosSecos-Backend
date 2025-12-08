package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.VentaEntity;
import com.erp.p03.repositories.VentaRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("VentaController Integration Tests")
class VentaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/ventas/ retorna 200 OK")
    void testGetAllVentas() throws Exception {
        mockMvc.perform(get("/api/ventas/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/ventas/{id} retorna 200 OK con datos")
    void testGetVentaById() throws Exception {
        VentaEntity venta = new VentaEntity();
        venta.setMonto(1000.0);
        venta.setFecha(LocalDateTime.now());
        VentaEntity guardada = ventaRepository.save(venta);

        mockMvc.perform(get("/api/ventas/" + guardada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto", equalTo(1000.0)));
    }

    @Test
    @DisplayName("GET /api/ventas/{id} inexistente retorna 404")
    void testGetVentaNotFound() throws Exception {
        mockMvc.perform(get("/api/ventas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/ventas/historial lista completa")
    void testGetHistorialVentas() throws Exception {
        VentaEntity venta = new VentaEntity();
        venta.setMonto(1500.0);
        venta.setFecha(LocalDateTime.now());
        ventaRepository.save(venta);

        mockMvc.perform(get("/api/ventas/historial"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/ventas/historial filtra por fecha")
    void testHistorialFilteredByDate() throws Exception {
        mockMvc.perform(get("/api/ventas/historial")
                .param("fechaInicio", "2024-01-01")
                .param("fechaFin", "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/ventas/historial filtra por usuario")
    void testHistorialFilteredByUsuario() throws Exception {
        mockMvc.perform(get("/api/ventas/historial")
                .param("usuarioId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/ventas/fiados lista fiados")
    void testGetVentasFiados() throws Exception {
        VentaEntity venta = new VentaEntity();
        venta.setEsfiado(true);
        venta.setMonto(2000.0);
        ventaRepository.save(venta);

        mockMvc.perform(get("/api/ventas/fiados"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("POST /api/ventas/crear crea nueva venta")
    void testCreateVenta() throws Exception {
        VentaEntity venta = new VentaEntity();
        venta.setMonto(1200.0);
        venta.setFecha(LocalDateTime.now());

        mockMvc.perform(post("/api/ventas/crear")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /api/ventas/{id} actualiza venta")
    void testUpdateVenta() throws Exception {
        VentaEntity venta = new VentaEntity();
        venta.setMonto(1000.0);
        venta.setFecha(LocalDateTime.now());
        VentaEntity guardada = ventaRepository.save(venta);

        VentaEntity actualizada = new VentaEntity();
        actualizada.setMonto(1500.0);

        mockMvc.perform(put("/api/ventas/" + guardada.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/ventas/{id} elimina venta")
    void testDeleteVenta() throws Exception {
        VentaEntity venta = new VentaEntity();
        venta.setMonto(500.0);
        venta.setFecha(LocalDateTime.now());
        VentaEntity guardada = ventaRepository.save(venta);

        mockMvc.perform(delete("/api/ventas/" + guardada.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/ventas/fiados/pendientes lista fiados con saldo")
    void testGetFiadosPendientes() throws Exception {
        mockMvc.perform(get("/api/ventas/fiados/pendientes"))
                .andExpect(status().isOk());
    }
}
