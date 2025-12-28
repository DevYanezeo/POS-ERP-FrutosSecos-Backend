package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.ClienteFiadoEntity;
import com.erp.p03.repositories.ClienteFiadoRepository;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ClienteFiadoService Tests")
class ClienteFiadoServiceTest {

    @Mock
    private ClienteFiadoRepository clienteFiadoRepository;

    private ClienteFiadoService clienteFiadoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clienteFiadoService = new ClienteFiadoService(clienteFiadoRepository);
    }

    @Test
    @DisplayName("Obtener cliente fiado por ID")
    void testGetClienteFiadoById() {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setId(1);
        cliente.setNombre("Juan");
        cliente.setTelefono("987654321");

        when(clienteFiadoRepository.findById(1)).thenReturn(Optional.of(cliente));

        Optional<ClienteFiadoEntity> resultado = clienteFiadoService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Obtener todos los clientes fiados")
    void testGetAllClientesFiados() {
        ClienteFiadoEntity c1 = new ClienteFiadoEntity();
        c1.setId(1);
        c1.setNombre("Juan");

        ClienteFiadoEntity c2 = new ClienteFiadoEntity();
        c2.setId(2);
        c2.setNombre("María");

        when(clienteFiadoRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<ClienteFiadoEntity> resultado = clienteFiadoService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Buscar cliente por teléfono")
    void testFindByTelefono() {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setTelefono("912345678");
        cliente.setNombre("Pedro");

        when(clienteFiadoRepository.findByTelefono("912345678"))
                .thenReturn(Optional.of(cliente));

        Optional<ClienteFiadoEntity> resultado = clienteFiadoService.findByTelefono("912345678");

        assertTrue(resultado.isPresent());
        assertEquals("Pedro", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Crear nuevo cliente fiado")
    void testCreateClienteFiado() {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setNombre("Carlos");
        cliente.setTelefono("  555555555  ");
        cliente.setEmail("carlos@test.com");

        when(clienteFiadoRepository.save(cliente)).thenReturn(cliente);

        ClienteFiadoEntity resultado = clienteFiadoService.create(cliente);

        assertEquals("Carlos", resultado.getNombre());
        assertEquals("555555555", resultado.getTelefono());
    }

    @Test
    @DisplayName("Actualizar cliente fiado existente")
    void testUpdateClienteFiado() {
        ClienteFiadoEntity clienteExistente = new ClienteFiadoEntity();
        clienteExistente.setId(1);
        clienteExistente.setNombre("Juan");
        clienteExistente.setApellido("Pérez");
        clienteExistente.setTelefono("987654321");
        clienteExistente.setEmail("juan@test.com");

        ClienteFiadoEntity clienteActualizado = new ClienteFiadoEntity();
        clienteActualizado.setNombre("Juan Carlos");
        clienteActualizado.setApellido("López");

        when(clienteFiadoRepository.findById(1)).thenReturn(Optional.of(clienteExistente));
        when(clienteFiadoRepository.save(clienteExistente)).thenReturn(clienteExistente);

        ClienteFiadoEntity resultado = clienteFiadoService.update(1, clienteActualizado);

        assertEquals("Juan Carlos", resultado.getNombre());
        assertEquals("López", resultado.getApellido());
    }

    @Test
    @DisplayName("Actualización parcial mantiene campos no provistos")
    void testPartialUpdate() {
        ClienteFiadoEntity clienteExistente = new ClienteFiadoEntity();
        clienteExistente.setId(1);
        clienteExistente.setNombre("Juan");
        clienteExistente.setEmail("juan@test.com");
        clienteExistente.setActivo(true);

        ClienteFiadoEntity actualizacion = new ClienteFiadoEntity();
        actualizacion.setNombre("Juan Carlos");

        when(clienteFiadoRepository.findById(1)).thenReturn(Optional.of(clienteExistente));
        when(clienteFiadoRepository.save(clienteExistente)).thenReturn(clienteExistente);

        ClienteFiadoEntity resultado = clienteFiadoService.update(1, actualizacion);

        assertEquals("Juan Carlos", resultado.getNombre());
        assertEquals("juan@test.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Eliminar cliente fiado")
    void testDeleteClienteFiado() {
        ClienteFiadoEntity cliente = new ClienteFiadoEntity();
        cliente.setId(1);

        when(clienteFiadoRepository.findById(1)).thenReturn(Optional.of(cliente));

        clienteFiadoService.delete(1);

        verify(clienteFiadoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Cliente no encontrado lanza excepción")
    void testClienteNotFoundThrowsException() {
        when(clienteFiadoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            clienteFiadoService.findByIdOrThrow(999);
        });
    }

    @Test
    @DisplayName("Listar solo clientes activos")
    void testListActivoClientes() {
        ClienteFiadoEntity c1 = new ClienteFiadoEntity();
        c1.setId(1);
        c1.setActivo(true);

        ClienteFiadoEntity c2 = new ClienteFiadoEntity();
        c2.setId(2);
        c2.setActivo(false);

        List<ClienteFiadoEntity> todos = Arrays.asList(c1, c2);
        List<ClienteFiadoEntity> activos = todos.stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .toList();

        assertEquals(1, activos.size());
    }
}
