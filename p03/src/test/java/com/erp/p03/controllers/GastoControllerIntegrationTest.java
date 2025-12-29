package com.erp.p03.controllers;

import com.erp.p03.entities.GastoEntity;
import com.erp.p03.repositories.GastoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("GastoController Integration Tests")
class GastoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        gastoRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/gastos retorna 200 OK con lista vacía")
    void testGetAllGastosVacio() throws Exception {
        mockMvc.perform(get("/api/gastos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/gastos retorna lista de gastos")
    void testGetAllGastos() throws Exception {
        GastoEntity gasto1 = new GastoEntity();
        gasto1.setDescripcion("Luz");
        gasto1.setMonto(50000);
        gasto1.setTipo("OPERACIONAL");
        gasto1.setFecha(new Date());
        gastoRepository.save(gasto1);

        GastoEntity gasto2 = new GastoEntity();
        gasto2.setDescripcion("Compra almendras");
        gasto2.setMonto(200000);
        gasto2.setTipo("ADQUISICION");
        gasto2.setFecha(new Date());
        gastoRepository.save(gasto2);

        mockMvc.perform(get("/api/gastos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].descripcion", anyOf(is("Luz"), is("Compra almendras"))))
                .andExpect(jsonPath("$[0].tipo", anyOf(is("OPERACIONAL"), is("ADQUISICION"))));
    }

    @Test
    @DisplayName("POST /api/gastos crea gasto tipo OPERACIONAL")
    void testCrearGastoOperacional() throws Exception {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Agua");
        gasto.setMonto(30000);
        gasto.setTipo("OPERACIONAL");
        gasto.setFecha(new Date());

        mockMvc.perform(post("/api/gastos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gasto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Agua")))
                .andExpect(jsonPath("$.monto", is(30000)))
                .andExpect(jsonPath("$.tipo", is("OPERACIONAL")))
                .andExpect(jsonPath("$.idGasto", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/gastos crea gasto tipo ADQUISICION")
    void testCrearGastoAdquisicion() throws Exception {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Compra nueces");
        gasto.setMonto(500000);
        gasto.setTipo("ADQUISICION");
        gasto.setFecha(new Date());

        mockMvc.perform(post("/api/gastos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gasto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Compra nueces")))
                .andExpect(jsonPath("$.monto", is(500000)))
                .andExpect(jsonPath("$.tipo", is("ADQUISICION")))
                .andExpect(jsonPath("$.idGasto", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/gastos crea gasto tipo OTROS")
    void testCrearGastoOtros() throws Exception {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Reparación");
        gasto.setMonto(120000);
        gasto.setTipo("OTROS");
        gasto.setFecha(new Date());

        mockMvc.perform(post("/api/gastos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gasto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Reparación")))
                .andExpect(jsonPath("$.monto", is(120000)))
                .andExpect(jsonPath("$.tipo", is("OTROS")))
                .andExpect(jsonPath("$.idGasto", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/gastos crea gasto sin fecha - asigna fecha automáticamente")
    void testCrearGastoSinFecha() throws Exception {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Gasto sin fecha");
        gasto.setMonto(80000);
        gasto.setTipo("OPERACIONAL");

        mockMvc.perform(post("/api/gastos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gasto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion", is("Gasto sin fecha")))
                .andExpect(jsonPath("$.fecha", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/gastos/rango retorna gastos en rango de fechas")
    void testGetGastosPorRango() throws Exception {
        Date hoy = new Date();
        Date hace7Dias = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);
        Date hace14Dias = new Date(System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000L);

        GastoEntity gasto1 = new GastoEntity();
        gasto1.setDescripcion("Gasto reciente");
        gasto1.setMonto(40000);
        gasto1.setTipo("OPERACIONAL");
        gasto1.setFecha(hoy);
        gastoRepository.save(gasto1);

        GastoEntity gasto2 = new GastoEntity();
        gasto2.setDescripcion("Gasto antiguo");
        gasto2.setMonto(60000);
        gasto2.setTipo("OPERACIONAL");
        gasto2.setFecha(hace14Dias);
        gastoRepository.save(gasto2);

        mockMvc.perform(get("/api/gastos/rango")
                .param("inicio", dateFormat.format(hace7Dias))
                .param("fin", dateFormat.format(hoy)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descripcion", is("Gasto reciente")));
    }

    @Test
    @DisplayName("GET /api/gastos/rango sin resultados retorna lista vacía")
    void testGetGastosPorRangoVacio() throws Exception {
        Date hace30Dias = new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L);
        Date hace29Dias = new Date(System.currentTimeMillis() - 29 * 24 * 60 * 60 * 1000L);

        mockMvc.perform(get("/api/gastos/rango")
                .param("inicio", dateFormat.format(hace30Dias))
                .param("fin", dateFormat.format(hace29Dias)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("DELETE /api/gastos/{id} elimina gasto existente")
    void testEliminarGasto() throws Exception {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Gasto a eliminar");
        gasto.setMonto(100000);
        gasto.setTipo("OTROS");
        gasto.setFecha(new Date());
        GastoEntity guardado = gastoRepository.save(gasto);

        mockMvc.perform(delete("/api/gastos/" + guardado.getIdGasto()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/gastos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/gastos crea múltiples gastos de diferentes tipos")
    void testCrearMultiplesGastosDiferentesTipos() throws Exception {
        GastoEntity gastoOp = new GastoEntity();
        gastoOp.setDescripcion("Internet");
        gastoOp.setMonto(45000);
        gastoOp.setTipo("OPERACIONAL");
        gastoOp.setFecha(new Date());

        mockMvc.perform(post("/api/gastos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gastoOp)))
                .andExpect(status().isOk());

        GastoEntity gastoAdq = new GastoEntity();
        gastoAdq.setDescripcion("Compra pistachos");
        gastoAdq.setMonto(350000);
        gastoAdq.setTipo("ADQUISICION");
        gastoAdq.setFecha(new Date());

        mockMvc.perform(post("/api/gastos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gastoAdq)))
                .andExpect(status().isOk());

        GastoEntity gastoOtro = new GastoEntity();
        gastoOtro.setDescripcion("Capacitación");
        gastoOtro.setMonto(95000);
        gastoOtro.setTipo("OTROS");
        gastoOtro.setFecha(new Date());

        mockMvc.perform(post("/api/gastos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(gastoOtro)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/gastos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @DisplayName("GET /api/gastos/rango filtra correctamente por tipo")
    void testGetGastosPorRangoYVerificarTipos() throws Exception {
        Date hoy = new Date();
        Date hace7Dias = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);

        GastoEntity gastoOp = new GastoEntity();
        gastoOp.setDescripcion("Luz");
        gastoOp.setMonto(50000);
        gastoOp.setTipo("OPERACIONAL");
        gastoOp.setFecha(hoy);
        gastoRepository.save(gastoOp);

        GastoEntity gastoAdq = new GastoEntity();
        gastoAdq.setDescripcion("Compra almendras");
        gastoAdq.setMonto(200000);
        gastoAdq.setTipo("ADQUISICION");
        gastoAdq.setFecha(hoy);
        gastoRepository.save(gastoAdq);

        mockMvc.perform(get("/api/gastos/rango")
                .param("inicio", dateFormat.format(hace7Dias))
                .param("fin", dateFormat.format(hoy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.tipo == 'OPERACIONAL')]", hasSize(1)))
                .andExpect(jsonPath("$[?(@.tipo == 'ADQUISICION')]", hasSize(1)));
    }
}
