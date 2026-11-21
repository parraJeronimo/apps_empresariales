package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.ClienteCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteUpdateDTO;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteService {

    /** RF-09: Registrar un cliente (documento y email unicos) */
    ClienteDTO createCliente(ClienteCreateDTO createDTO);

    /** RF-10: Consultar un cliente */
    ClienteDTO getClienteById(Long id);

    ClienteDTO getClienteByDocumento(String documento);

    List<ClienteDTO> getAllClientes();

    /** Igual que getAllClientes() pero devuelve solo la pagina pedida (el controlador la expone por headers). */
    Page<ClienteDTO> getAllClientes(Pageable pageable);

    /** RF-10: Actualizar los datos de un cliente */
    ClienteDTO updateCliente(Long id, ClienteUpdateDTO updateDTO);
}
