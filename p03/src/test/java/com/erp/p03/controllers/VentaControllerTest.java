package com.erp.p03.controllers;

import com.erp.p03.entities.VentaEntity;
import com.erp.p03.controllers.dto.VentaRequest;
import com.erp.p03.services.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import org.mockito.Mockito;

@WebMvcTest(VentaController.class)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VentaService ventaService; // inyectado desde TestConfiguration (mock)

    @TestConfiguration
    static class Config {
        @Bean
        public VentaService ventaService() {
            return Mockito.mock(VentaService.class);
        }

        // Mock JwtUtil requerido por JwtAuthenticationFilter
        @Bean
        public com.erp.p03.auth.JwtUtil jwtUtil() {
            return Mockito.mock(com.erp.p03.auth.JwtUtil.class);
        }

        // Mock JwtAuthenticationFilter para satisfacer la creación del bean en contexto de seguridad
        @Bean
        public com.erp.p03.auth.JwtAuthenticationFilter jwtAuthenticationFilter(com.erp.p03.auth.JwtUtil jwtUtil) {
            return Mockito.mock(com.erp.p03.auth.JwtAuthenticationFilter.class);
        }
    }

    @Test
    void historialSinParametros_devuelveLista() throws Exception {
        VentaEntity v = new VentaEntity(1, LocalDateTime.now(), 5, 1000, 190, 1190, "EFECTIVO");
        when(ventaService.historialVentas(null, null, null)).thenReturn(List.of(v));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/ventas/historial")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idVenta").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].usuarioId").value(5));
    }

    @Test
    void confirmarVenta_post_devuelveCreated() throws Exception {
        // Preparar request
        VentaRequest req = new VentaRequest();
        req.setUsuarioId(5);
        req.setMetodoPago("EFECTIVO");
        req.setSubtotal(1000);
        req.setIva(190);
        req.setTotal(1190);
        // detalles puede ser null para este test de controller (no se valida internamente por Mock)

        VentaEntity saved = new VentaEntity(10, LocalDateTime.now(), 5, 1000, 190, 1190, "EFECTIVO");
        when(ventaService.confirmarVenta(org.mockito.ArgumentMatchers.any(VentaRequest.class))).thenReturn(saved);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/ventas/confirmar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.idVenta").value(10))
                .andExpect(MockMvcResultMatchers.jsonPath("$.usuarioId").value(5));
    }

}
