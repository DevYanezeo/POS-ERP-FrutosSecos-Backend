package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.CategoriaEntity;
import com.erp.p03.repositories.CategoriaRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("CategoriaController Integration Tests")
class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/categorias lista todas")
    void testGetAllCategorias() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/categorias/{id} obtiene categoría")
    void testGetCategoriaById() throws Exception {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Frutos Secos");
        CategoriaEntity guardada = categoriaRepository.save(categoria);

        mockMvc.perform(get("/api/categorias/" + guardada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", equalTo("Frutos Secos")));
    }

    @Test
    @DisplayName("GET /api/categorias/999 retorna 404")
    void testGetCategoriaNotFound() throws Exception {
        mockMvc.perform(get("/api/categorias/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/categorias crea categoría")
    void testCreateCategoria() throws Exception {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Especias");
        categoria.setDescripcion("Especias variadas");

        mockMvc.perform(post("/api/categorias")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} actualiza categoría")
    void testUpdateCategoria() throws Exception {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Original");
        CategoriaEntity guardada = categoriaRepository.save(categoria);

        CategoriaEntity actualizada = new CategoriaEntity();
        actualizada.setNombre("Actualizada");

        mockMvc.perform(put("/api/categorias/" + guardada.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} elimina categoría")
    void testDeleteCategoria() throws Exception {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("A Eliminar");
        CategoriaEntity guardada = categoriaRepository.save(categoria);

        mockMvc.perform(delete("/api/categorias/" + guardada.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/categorias/buscar busca por nombre")
    void testSearchCategoria() throws Exception {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre("Legumbres");
        categoriaRepository.save(categoria);

        mockMvc.perform(get("/api/categorias/buscar")
                .param("nombre", "Legumbres"))
                .andExpect(status().isOk());
    }
}
