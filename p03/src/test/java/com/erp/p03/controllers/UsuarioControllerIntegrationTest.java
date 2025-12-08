package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.UsuarioEntity;
import com.erp.p03.repositories.UsuarioRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("UsuarioController Integration Tests")
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/usuarios/ retorna 200 OK")
    void testGetAllUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} retorna 200 OK con datos")
    void testGetUsuarioById() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("Juan Test");
        usuario.setEmail("juan@test.com");
        usuario.setRut("12345678-9");
        UsuarioEntity guardado = usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/usuarios/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", equalTo("Juan Test")))
                .andExpect(jsonPath("$.email", equalTo("juan@test.com")));
    }

    @Test
    @DisplayName("GET /api/usuarios/999 retorna 404 Not Found")
    void testGetUsuarioNotFound() throws Exception {
        mockMvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/usuarios/crear retorna 201 Created")
    void testCreateUsuario() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("Pedro Test");
        usuario.setEmail("pedro@test.com");
        usuario.setRut("98765432-1");

        mockMvc.perform(post("/api/usuarios/crear")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", equalTo("Pedro Test")));
    }

    @Test
    @DisplayName("POST con email duplicado retorna 400 Bad Request")
    void testCreateUsuarioWithDuplicateEmail() throws Exception {
        UsuarioEntity usuario1 = new UsuarioEntity();
        usuario1.setNombre("Usuario 1");
        usuario1.setEmail("duplicado@test.com");
        usuario1.setRut("11111111-1");
        usuarioRepository.save(usuario1);

        UsuarioEntity usuario2 = new UsuarioEntity();
        usuario2.setNombre("Usuario 2");
        usuario2.setEmail("duplicado@test.com");
        usuario2.setRut("22222222-2");

        mockMvc.perform(post("/api/usuarios/crear")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(usuario2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} retorna 200 OK")
    void testUpdateUsuario() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("Usuario Original");
        usuario.setEmail("original@test.com");
        usuario.setRut("55555555-5");
        UsuarioEntity guardado = usuarioRepository.save(usuario);

        UsuarioEntity actualizado = new UsuarioEntity();
        actualizado.setNombre("Usuario Actualizado");
        actualizado.setEmail("actualizado@test.com");

        mockMvc.perform(put("/api/usuarios/" + guardado.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} retorna 204 No Content")
    void testDeleteUsuario() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("A Eliminar");
        usuario.setEmail("eliminar@test.com");
        usuario.setRut("33333333-3");
        UsuarioEntity guardado = usuarioRepository.save(usuario);

        mockMvc.perform(delete("/api/usuarios/" + guardado.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE usuario inexistente retorna 404 Not Found")
    void testDeleteUsuarioNotFound() throws Exception {
        mockMvc.perform(delete("/api/usuarios/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST con RUT duplicado retorna 400 Bad Request")
    void testCreateUsuarioWithDuplicateRut() throws Exception {
        UsuarioEntity usuario1 = new UsuarioEntity();
        usuario1.setNombre("Usuario A");
        usuario1.setEmail("a@test.com");
        usuario1.setRut("44444444-4");
        usuarioRepository.save(usuario1);

        UsuarioEntity usuario2 = new UsuarioEntity();
        usuario2.setNombre("Usuario B");
        usuario2.setEmail("b@test.com");
        usuario2.setRut("44444444-4");

        mockMvc.perform(post("/api/usuarios/crear")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(usuario2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/usuarios/buscar busca por email")
    void testSearchUsuarioByEmail() throws Exception {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("Search Test");
        usuario.setEmail("search@test.com");
        usuario.setRut("66666666-6");
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/usuarios/buscar")
                .param("email", "search@test.com"))
                .andExpect(status().isOk());
    }
}
