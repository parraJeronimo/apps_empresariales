package com.taller.bibliotecas.businessLayer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.taller.bibliotecas.businessLayer.dto.ClienteCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.impl.ClienteServiceImpl;
import com.taller.bibliotecas.persistenceLayer.dao.ClienteDAO;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Unit Tests (RF-09, RF-10)")
class ClienteServiceTest {

    @Mock
    private ClienteDAO clienteDao;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteDTO clienteDto(Long id, String nombre) {
        ClienteDTO cliente = new ClienteDTO();
        cliente.setId(id);
        cliente.setNombre(nombre);
        cliente.setDocumento("1094123456");
        cliente.setEmail("juan@gmail.com");
        cliente.setTelefono("3003003003");
        cliente.setDireccion("conjunto granada");
        return cliente;
    }

    private ClienteCreateDTO crearDto() {
        ClienteCreateDTO dto = new ClienteCreateDTO();
        dto.setNombre("Juan");
        dto.setDocumento("1094123456");
        dto.setEmail("juan@gmail.com");
        return dto;
    }

    // ---------- RF-09 registrar ----------

    @Test
    void testCreateCliente_Success() {
        ClienteCreateDTO dto = crearDto();
        when(clienteDao.existsByDocumento("1094123456")).thenReturn(false);
        when(clienteDao.existsByEmail("juan@gmail.com")).thenReturn(false);
        when(clienteDao.save(dto)).thenReturn(clienteDto(1L, "Juan"));

        ClienteDTO result = clienteService.createCliente(dto);

        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
        verify(clienteDao, times(1)).save(dto);
    }

    @Test
    void testCreateCliente_DocumentoDuplicado() {
        ClienteCreateDTO dto = crearDto();
        when(clienteDao.existsByDocumento("1094123456")).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> clienteService.createCliente(dto));

        assertEquals("Ya existe un cliente registrado con el documento: 1094123456", ex.getMessage());
        verify(clienteDao, never()).save(any());
    }

    @Test
    void testCreateCliente_EmailDuplicado() {
        ClienteCreateDTO dto = crearDto();
        when(clienteDao.existsByDocumento("1094123456")).thenReturn(false);
        when(clienteDao.existsByEmail("juan@gmail.com")).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> clienteService.createCliente(dto));

        assertEquals("Ya existe un cliente registrado con el email: juan@gmail.com", ex.getMessage());
        verify(clienteDao, never()).save(any());
    }

    // ---------- RF-10 consultar ----------

    @Test
    void testGetClientById_Found() {
        ClienteDTO cliente = clienteDto(2L, "Juan");
        when(clienteDao.findById(2L)).thenReturn(Optional.of(cliente));

        ClienteDTO result = clienteService.getClienteById(2L);

        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        verify(clienteDao, times(1)).findById(2L);
    }

    @Test
    void testGetClientById_NotFound() {
        when(clienteDao.findById(99L)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> clienteService.getClienteById(99L));

        assertEquals("Cliente no encontrado con ID: 99", ex.getMessage());
        verify(clienteDao, times(1)).findById(99L);
    }

    @Test
    void testGetClienteByDocumento_Found() {
        when(clienteDao.findByDocumento("1094123456")).thenReturn(Optional.of(clienteDto(1L, "Juan")));

        assertEquals(1L, clienteService.getClienteByDocumento("1094123456").getId());
    }

    @Test
    void testGetClienteByDocumento_NotFound() {
        when(clienteDao.findByDocumento("000")).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> clienteService.getClienteByDocumento("000"));

        assertEquals("Cliente no encontrado con documento: 000", ex.getMessage());
    }

    @Test
    void testGetAllClientes_ReturnsList() {
        when(clienteDao.findAll()).thenReturn(List.of(clienteDto(1L, "Juan"), clienteDto(2L, "Maria")));

        List<ClienteDTO> result = clienteService.getAllClientes();

        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getNombre());
        assertEquals("Maria", result.get(1).getNombre());
        verify(clienteDao, times(1)).findAll();
    }

    // ---------- RF-10 actualizar ----------

    @Test
    void testUpdateCliente_Success_SinCambiarEmail() {
        ClienteUpdateDTO dto = new ClienteUpdateDTO();
        dto.setTelefono("3110000000");
        ClienteDTO actualizado = clienteDto(1L, "Juan");
        actualizado.setTelefono("3110000000");
        when(clienteDao.update(1L, dto)).thenReturn(Optional.of(actualizado));

        ClienteDTO result = clienteService.updateCliente(1L, dto);

        assertEquals("3110000000", result.getTelefono());
        verify(clienteDao, never()).existsByEmailAndIdNot(any(), any());
    }

    @Test
    void testUpdateCliente_CambiaEmailDisponible() {
        ClienteUpdateDTO dto = new ClienteUpdateDTO();
        dto.setEmail("nuevo@gmail.com");
        when(clienteDao.existsByEmailAndIdNot("nuevo@gmail.com", 1L)).thenReturn(false);
        when(clienteDao.update(1L, dto)).thenReturn(Optional.of(clienteDto(1L, "Juan")));

        assertNotNull(clienteService.updateCliente(1L, dto));

        verify(clienteDao, times(1)).update(1L, dto);
    }

    @Test
    void testUpdateCliente_EmailDeOtroCliente() {
        ClienteUpdateDTO dto = new ClienteUpdateDTO();
        dto.setEmail("ocupado@gmail.com");
        when(clienteDao.existsByEmailAndIdNot("ocupado@gmail.com", 1L)).thenReturn(true);

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> clienteService.updateCliente(1L, dto));

        assertEquals("Ya existe otro cliente registrado con el email: ocupado@gmail.com", ex.getMessage());
        verify(clienteDao, never()).update(any(), any());
    }

    @Test
    void testUpdateCliente_NotFound() {
        ClienteUpdateDTO dto = new ClienteUpdateDTO();
        when(clienteDao.update(99L, dto)).thenReturn(Optional.empty());

        RecursoNoEncontradoException ex = assertThrows(RecursoNoEncontradoException.class,
                () -> clienteService.updateCliente(99L, dto));

        assertEquals("Cliente no encontrado con ID: 99", ex.getMessage());
    }
}
