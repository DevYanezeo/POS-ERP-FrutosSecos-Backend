package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.FeriadoEntity;
import com.erp.p03.repositories.FeriadoRepository;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("FeriadoController Integration Tests")
class FeriadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FeriadoRepository feriadoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/feriados lista todos")
    void testGetAllFeriados() throws Exception {
        mockMvc.perform(get("/api/feriados"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/feriados/{id} obtiene feriado")
    void testGetFeriadoById() throws Exception {
        FeriadoEntity feriado = new FeriadoEntity();
        feriado.setFecha(LocalDate.of(2024, 12, 25));
        feriado.setNombre("Navidad");
        FeriadoEntity guardado = feriadoRepository.save(feriado);

        mockMvc.perform(get("/api/feriados/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", equalTo("Navidad")));
    }

    @Test
    @DisplayName("GET /api/feriados/999 retorna 404")
    void testGetFeriadoNotFound() throws Exception {
        mockMvc.perform(get("/api/feriados/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/feriados crea feriado")
    void testCreateFeriado() throws Exception {
        FeriadoEntity feriado = new FeriadoEntity();
        feriado.setFecha(LocalDate.of(2024, 9, 18));
        feriado.setNombre("Fiestas Patrias");
        feriado.setActivo(true);

        mockMvc.perform(post("/api/feriados")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(feriado)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /api/feriados/{id} actualiza feriado")
    void testUpdateFeriado() throws Exception {
        FeriadoEntity feriado = new FeriadoEntity();
        feriado.setFecha(LocalDate.of(2024, 1, 1));
        feriado.setNombre("Año Nuevo");
        FeriadoEntity guardado = feriadoRepository.save(feriado);

        FeriadoEntity actualizado = new FeriadoEntity();
        actualizado.setNombre("Año Nuevo 2024");

        mockMvc.perform(put("/api/feriados/" + guardado.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/feriados/{id} elimina feriado")
    void testDeleteFeriado() throws Exception {
        FeriadoEntity feriado = new FeriadoEntity();
        feriado.setFecha(LocalDate.of(2024, 5, 1));
        feriado.setNombre("Día del Trabajo");
        FeriadoEntity guardado = feriadoRepository.save(feriado);

        mockMvc.perform(delete("/api/feriados/" + guardado.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/feriados/rango busca entre fechas")
    void testSearchByDateRange() throws Exception {
        mockMvc.perform(get("/api/feriados/rango")
                .param("desde", "2024-01-01")
                .param("hasta", "2024-12-31"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/feriados/fecha busca por fecha exacta")
    void testSearchByExactDate() throws Exception {
        mockMvc.perform(get("/api/feriados/fecha")
                .param("fecha", "2024-12-25"))
                .andExpect(status().isOk());
    }
}
