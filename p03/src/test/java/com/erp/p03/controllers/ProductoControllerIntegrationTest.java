package com.erp.p03.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.ProductoRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("ProductoController Integration Tests")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/productos/ retorna 200 OK")
    void testGetAllProductos() throws Exception {
        mockMvc.perform(get("/api/productos/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/productos/{id} retorna 200 OK con datos")
    void testGetProductoById() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Almendra Test");
        producto.setStock(100);
        ProductoEntity guardado = productoRepository.save(producto);

        mockMvc.perform(get("/api/productos/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", equalTo("Almendra Test")))
                .andExpect(jsonPath("$.stock", equalTo(100)));
    }

    @Test
    @DisplayName("GET /api/productos/999 retorna 404 Not Found")
    void testGetProductoNotFound() throws Exception {
        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/productos/crear retorna 201 Created")
    void testCreateProducto() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Producto Test");
        producto.setStock(50);

        mockMvc.perform(post("/api/productos/crear")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", equalTo("Producto Test")));
    }

    @Test
    @DisplayName("GET /api/productos/buscar realiza búsquedas correctas")
    void testSearchProductos() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Avellana Búsqueda");
        productoRepository.save(producto);

        mockMvc.perform(get("/api/productos/buscar")
                .param("nombre", "Avellana"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("GET /api/productos/estado/{estado} filtra correctamente")
    void testFilterByEstado() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Test Disponible");
        producto.setEstado("Disponible");
        productoRepository.save(producto);

        mockMvc.perform(get("/api/productos/estado/Disponible"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("PUT /api/productos/{id} actualiza correctamente")
    void testUpdateProducto() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("Producto Original");
        producto.setStock(100);
        ProductoEntity guardado = productoRepository.save(producto);

        ProductoEntity actualizado = new ProductoEntity();
        actualizado.setNombre("Producto Actualizado");
        actualizado.setStock(150);

        mockMvc.perform(put("/api/productos/" + guardado.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} elimina correctamente")
    void testDeleteProducto() throws Exception {
        ProductoEntity producto = new ProductoEntity();
        producto.setNombre("A Eliminar");
        ProductoEntity guardado = productoRepository.save(producto);

        mockMvc.perform(delete("/api/productos/" + guardado.getId()))
                .andExpect(status().isNoContent());
    }
}
