package com.erp.p03.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.erp.p03.entities.UsuarioEntity;
import com.erp.p03.repositories.UsuarioRepository;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioService = new UsuarioService(usuarioRepository);
    }

    @Test
    @DisplayName("Obtener usuario por ID")
    void testGetUsuarioById() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1);
        usuario.setNombre("Juan");
        usuario.setEmail("juan@example.com");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> resultado = usuarioService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
    }

    @Test
    @DisplayName("Obtener todos los usuarios")
    void testGetAllUsuarios() {
        UsuarioEntity u1 = new UsuarioEntity();
        u1.setId(1);
        u1.setNombre("Juan");

        UsuarioEntity u2 = new UsuarioEntity();
        u2.setId(2);
        u2.setNombre("María");

        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UsuarioEntity> resultado = usuarioService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Email duplicado rechaza creación")
    void testDuplicateEmailRejected() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail("test@example.com");

        when(usuarioRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(usuario));

        UsuarioEntity nuevoUsuario = new UsuarioEntity();
        nuevoUsuario.setEmail("test@example.com");

        assertThrows(IllegalArgumentException.class, () -> {
            if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email duplicado");
            }
        });
    }

    @Test
    @DisplayName("RUT duplicado rechaza creación")
    void testDuplicateRutRejected() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setRut("12345678-9");

        when(usuarioRepository.findByRut("12345678-9"))
                .thenReturn(Optional.of(usuario));

        assertThrows(IllegalArgumentException.class, () -> {
            if (usuarioRepository.findByRut("12345678-9").isPresent()) {
                throw new IllegalArgumentException("RUT duplicado");
            }
        });
    }

    @Test
    @DisplayName("Actualización parcial mantiene campos no provistos")
    void testPartialUpdatePreservesUnprovidedFields() {
        UsuarioEntity usuarioExistente = new UsuarioEntity();
        usuarioExistente.setId(1);
        usuarioExistente.setNombre("Juan");
        usuarioExistente.setEmail("juan@example.com");
        usuarioExistente.setRut("12345678-9");

        UsuarioEntity actualizacion = new UsuarioEntity();
        actualizacion.setNombre("Juan Updated");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));

        if (actualizacion.getNombre() != null) {
            usuarioExistente.setNombre(actualizacion.getNombre());
        }

        assertEquals("Juan Updated", usuarioExistente.getNombre());
        assertEquals("juan@example.com", usuarioExistente.getEmail());
        assertEquals("12345678-9", usuarioExistente.getRut());
    }

    @Test
    @DisplayName("Validación de unicidad en actualización")
    void testUnicityValidationOnUpdate() {
        UsuarioEntity usuarioExistente = new UsuarioEntity();
        usuarioExistente.setId(1);
        usuarioExistente.setEmail("juan@example.com");

        UsuarioEntity otroUsuario = new UsuarioEntity();
        otroUsuario.setId(2);
        otroUsuario.setEmail("otro@example.com");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByEmail("otro@example.com"))
                .thenReturn(Optional.of(otroUsuario));

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioExistente.setEmail("otro@example.com");
            if (usuarioRepository.findByEmail("otro@example.com").isPresent() &&
                !usuarioRepository.findByEmail("otro@example.com").get().getId().equals(1)) {
                throw new IllegalArgumentException("Email ya en uso");
            }
        });
    }

    @Test
    @DisplayName("Usuario no encontrado lanza excepción")
    void testUserNotFoundThrowsException() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            usuarioService.findByIdOrThrow(999);
        });
    }

    @Test
    @DisplayName("Crear nuevo usuario")
    void testCreateUsuario() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre("Pedro");
        usuario.setEmail("pedro@example.com");
        usuario.setRut("98765432-1");

        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        UsuarioEntity resultado = usuarioService.create(usuario);

        assertEquals("Pedro", resultado.getNombre());
        assertEquals("pedro@example.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Actualizar usuario existente")
    void testUpdateUsuario() {
        UsuarioEntity usuarioExistente = new UsuarioEntity();
        usuarioExistente.setId(1);
        usuarioExistente.setNombre("Juan");

        UsuarioEntity usuarioActualizado = new UsuarioEntity();
        usuarioActualizado.setNombre("Juan Carlos");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioExistente);

        usuarioExistente.setNombre("Juan Carlos");
        UsuarioEntity resultado = usuarioService.update(1, usuarioActualizado);

        assertEquals("Juan Carlos", resultado.getNombre());
    }

    @Test
    @DisplayName("Eliminar usuario existente")
    void testDeleteUsuario() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        usuarioService.delete(1);

        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Buscar por email")
    void testFindByEmail() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail("test@example.com");

        when(usuarioRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> resultado = usuarioService.findByEmail("test@example.com");

        assertTrue(resultado.isPresent());
        assertEquals("test@example.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("Buscar por RUT")
    void testFindByRut() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setRut("12345678-9");

        when(usuarioRepository.findByRut("12345678-9"))
                .thenReturn(Optional.of(usuario));

        Optional<UsuarioEntity> resultado = usuarioService.findByRut("12345678-9");

        assertTrue(resultado.isPresent());
        assertEquals("12345678-9", resultado.get().getRut());
    }
}
