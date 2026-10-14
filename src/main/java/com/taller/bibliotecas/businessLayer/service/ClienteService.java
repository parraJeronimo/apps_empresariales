package com.taller.bibliotecas.businessLayer.service;

import com.taller.bibliotecas.businessLayer.dto.ClienteCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteUpdateDTO;

import java.util.List;

public interface ClienteService {

    /** RF-09: Registrar un cliente (documento y email unicos) */
    ClienteDTO createCliente(ClienteCreateDTO createDTO);

    /** RF-10: Consultar un cliente */
    ClienteDTO getClienteById(Long id);

    ClienteDTO getClienteByDocumento(String documento);

    List<ClienteDTO> getAllClientes();

    /** RF-10: Actualizar los datos de un cliente */
    ClienteDTO updateCliente(Long id, ClienteUpdateDTO updateDTO);
}
