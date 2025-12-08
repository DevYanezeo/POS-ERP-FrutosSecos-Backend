package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.ClienteFiadoEntity;
import com.erp.p03.repositories.ClienteFiadoRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("ClienteFiadoController Integration Tests")
class ClienteFiadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteFiadoRepository clienteFiadoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/clientesfiado lista clientes")
    void testGetAllClientes() throws Exception {
        mockMvc.perform(get("/api/clientesfiado"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/clientesfiado/activo filtra activos")
    void testGetClientesActivos() throws Exception {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("Test Activo");
        cliente.setActivo(true);
        clienteFiadoRepository.save(cliente);

        mockMvc.perform(get("/api/clientesfiado/activo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/clientesfiado/{id} obtiene cliente")
    void testGetClienteById() throws Exception {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("Juan Test");
        cliente.setTelefono("987654321");
        ClienteFiadoEntity guardado = clienteFiadoRepository.save(cliente);

        mockMvc.perform(get("/api/clientesfiado/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", equalTo("Juan Test")));
    }

    @Test
    @DisplayName("GET /api/clientesfiado/999 retorna 404")
    void testGetClienteNotFound() throws Exception {
        mockMvc.perform(get("/api/clientesfiado/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/clientesfiado/buscar por teléfono")
    void testSearchByTelefono() throws Exception {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("Pedro");
        cliente.setTelefono("912345678");
        clienteFiadoRepository.save(cliente);

        mockMvc.perform(get("/api/clientesfiado/buscar")
                .param("telefono", "912345678"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/clientesfiado crea cliente")
    void testCreateCliente() throws Exception {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("Carlos Test");
        cliente.setTelefono("555555555");
        cliente.setEmail("carlos@test.com");

        mockMvc.perform(post("/api/clientesfiado")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", equalTo("Carlos Test")));
    }

    @Test
    @DisplayName("PUT /api/clientesfiado/{id} actualiza cliente")
    void testUpdateCliente() throws Exception {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("Cliente Original");
        cliente.setTelefono("111111111");
        ClienteFiadoEntity guardado = clienteFiadoRepository.save(cliente);

        ClienteFiadoEntity actualizado = new ClienteFiadoEntity();
        actualizado.setNombre("Cliente Actualizado");

        mockMvc.perform(put("/api/clientesfiado/" + guardado.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/clientesfiado/{id} elimina cliente")
    void testDeleteCliente() throws Exception {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("A Eliminar");
        cliente.setTelefono("222222222");
        ClienteFiadoEntity guardado = clienteFiadoRepository.save(cliente);

        mockMvc.perform(delete("/api/clientesfiado/" + guardado.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/clientesfiado normaliza teléfono")
    void testCreateClienteNormalizaTelefono() throws Exception {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("Normalizado");
        cliente.setTelefono("  777777777  ");

        mockMvc.perform(post("/api/clientesfiado")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.telefono", equalTo("777777777")));
    }
}
