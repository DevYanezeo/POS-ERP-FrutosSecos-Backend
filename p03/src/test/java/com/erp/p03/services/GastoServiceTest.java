package com.erp.p03.services;

import com.erp.p03.entities.GastoEntity;
import com.erp.p03.entities.UsuarioEntity;
import com.erp.p03.entities.ProductoEntity;
import com.erp.p03.repositories.GastoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("GastoService Tests")
class GastoServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    private GastoService gastoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gastoService = new GastoService();
        gastoService.gastoRepository = gastoRepository;
    }

    @Test
    @DisplayName("Listar todos los gastos")
    void testListarTodos() {
        GastoEntity gasto1 = new GastoEntity();
        gasto1.setIdGasto(1L);
        gasto1.setDescripcion("Luz");
        gasto1.setMonto(50000);
        gasto1.setTipo("OPERACIONAL");
        gasto1.setFecha(new Date());

        GastoEntity gasto2 = new GastoEntity();
        gasto2.setIdGasto(2L);
        gasto2.setDescripcion("Compra mercadería");
        gasto2.setMonto(200000);
        gasto2.setTipo("ADQUISICION");
        gasto2.setFecha(new Date());

        List<GastoEntity> gastos = Arrays.asList(gasto1, gasto2);
        when(gastoRepository.findAll()).thenReturn(gastos);

        List<GastoEntity> result = gastoService.listarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Luz", result.get(0).getDescripcion());
        assertEquals("ADQUISICION", result.get(1).getTipo());
        verify(gastoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Guardar gasto con fecha establecida")
    void testGuardarGastoConFecha() {
        Date fecha = new Date();
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Mantenimiento");
        gasto.setMonto(75000);
        gasto.setTipo("OPERACIONAL");
        gasto.setFecha(fecha);

        when(gastoRepository.save(any(GastoEntity.class))).thenReturn(gasto);

        GastoEntity result = gastoService.guardarGasto(gasto);

        assertNotNull(result);
        assertEquals("Mantenimiento", result.getDescripcion());
        assertEquals(75000, result.getMonto());
        assertEquals("OPERACIONAL", result.getTipo());
        assertEquals(fecha, result.getFecha());
        verify(gastoRepository, times(1)).save(gasto);
    }

    @Test
    @DisplayName("Guardar gasto sin fecha - asigna fecha automáticamente")
    void testGuardarGastoSinFecha() {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Compra insumos");
        gasto.setMonto(100000);
        gasto.setTipo("ADQUISICION");
        gasto.setFecha(null);

        when(gastoRepository.save(any(GastoEntity.class))).thenAnswer(invocation -> {
            GastoEntity g = invocation.getArgument(0);
            return g;
        });

        GastoEntity result = gastoService.guardarGasto(gasto);

        assertNotNull(result);
        assertNotNull(result.getFecha());
        assertEquals("Compra insumos", result.getDescripcion());
        verify(gastoRepository, times(1)).save(gasto);
    }

    @Test
    @DisplayName("Guardar gasto tipo ADQUISICION")
    void testGuardarGastoAdquisicion() {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Compra almendras");
        gasto.setMonto(500000);
        gasto.setTipo("ADQUISICION");
        gasto.setFecha(new Date());

        when(gastoRepository.save(any(GastoEntity.class))).thenReturn(gasto);

        GastoEntity result = gastoService.guardarGasto(gasto);

        assertNotNull(result);
        assertEquals("ADQUISICION", result.getTipo());
        assertEquals(500000, result.getMonto());
        verify(gastoRepository, times(1)).save(gasto);
    }

    @Test
    @DisplayName("Guardar gasto tipo OPERACIONAL")
    void testGuardarGastoOperacional() {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Agua");
        gasto.setMonto(30000);
        gasto.setTipo("OPERACIONAL");
        gasto.setFecha(new Date());

        when(gastoRepository.save(any(GastoEntity.class))).thenReturn(gasto);

        GastoEntity result = gastoService.guardarGasto(gasto);

        assertNotNull(result);
        assertEquals("OPERACIONAL", result.getTipo());
        assertEquals(30000, result.getMonto());
        verify(gastoRepository, times(1)).save(gasto);
    }

    @Test
    @DisplayName("Guardar gasto tipo OTROS")
    void testGuardarGastoOtros() {
        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Reparación equipo");
        gasto.setMonto(120000);
        gasto.setTipo("OTROS");
        gasto.setFecha(new Date());

        when(gastoRepository.save(any(GastoEntity.class))).thenReturn(gasto);

        GastoEntity result = gastoService.guardarGasto(gasto);

        assertNotNull(result);
        assertEquals("OTROS", result.getTipo());
        assertEquals(120000, result.getMonto());
        verify(gastoRepository, times(1)).save(gasto);
    }

    @Test
    @DisplayName("Listar gastos por rango de fechas")
    void testListarPorRangoFecha() {
        Date inicio = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);
        Date fin = new Date();

        GastoEntity gasto1 = new GastoEntity();
        gasto1.setIdGasto(1L);
        gasto1.setDescripcion("Gasto en rango");
        gasto1.setMonto(40000);
        gasto1.setTipo("OPERACIONAL");
        gasto1.setFecha(new Date());

        List<GastoEntity> gastos = Arrays.asList(gasto1);
        when(gastoRepository.findByFechaBetween(inicio, fin)).thenReturn(gastos);

        List<GastoEntity> result = gastoService.listarPorRangoFecha(inicio, fin);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gasto en rango", result.get(0).getDescripcion());
        verify(gastoRepository, times(1)).findByFechaBetween(inicio, fin);
    }

    @Test
    @DisplayName("Listar gastos por rango de fechas vacío")
    void testListarPorRangoFechaVacio() {
        Date inicio = new Date(System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000L);
        Date fin = new Date(System.currentTimeMillis() - 364 * 24 * 60 * 60 * 1000L);

        when(gastoRepository.findByFechaBetween(inicio, fin)).thenReturn(Arrays.asList());

        List<GastoEntity> result = gastoService.listarPorRangoFecha(inicio, fin);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(gastoRepository, times(1)).findByFechaBetween(inicio, fin);
    }

    @Test
    @DisplayName("Eliminar gasto por ID")
    void testEliminarGasto() {
        Long idGasto = 1L;
        doNothing().when(gastoRepository).deleteById(idGasto);

        gastoService.eliminarGasto(idGasto);

        verify(gastoRepository, times(1)).deleteById(idGasto);
    }

    @Test
    @DisplayName("Guardar gasto con usuario")
    void testGuardarGastoConUsuario() {
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Admin");

        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Gasto con usuario");
        gasto.setMonto(60000);
        gasto.setTipo("OTROS");
        gasto.setFecha(new Date());
        gasto.setUsuario(usuario);

        when(gastoRepository.save(any(GastoEntity.class))).thenReturn(gasto);

        GastoEntity result = gastoService.guardarGasto(gasto);

        assertNotNull(result);
        assertNotNull(result.getUsuario());
        assertEquals("Admin", result.getUsuario().getNombre());
        verify(gastoRepository, times(1)).save(gasto);
    }

    @Test
    @DisplayName("Guardar gasto con producto asociado")
    void testGuardarGastoConProducto() {
        ProductoEntity producto = new ProductoEntity();
        producto.setIdProducto(1);
        producto.setNombre("Nueces");

        GastoEntity gasto = new GastoEntity();
        gasto.setDescripcion("Merma de producto");
        gasto.setMonto(25000);
        gasto.setTipo("OTROS");
        gasto.setFecha(new Date());
        gasto.setProducto(producto);

        when(gastoRepository.save(any(GastoEntity.class))).thenReturn(gasto);

        GastoEntity result = gastoService.guardarGasto(gasto);

        assertNotNull(result);
        assertNotNull(result.getProducto());
        assertEquals("Nueces", result.getProducto().getNombre());
        verify(gastoRepository, times(1)).save(gasto);
    }
}
