package com.taller.bibliotecas.businessLayer.service.impl;

import com.taller.bibliotecas.businessLayer.dto.ClienteCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteUpdateDTO;
import com.taller.bibliotecas.businessLayer.service.ClienteService;
import com.taller.bibliotecas.persistenceLayer.dao.ClienteDAO;
import com.taller.bibliotecas.presentationLayer.exception.RecursoNoEncontradoException;
import com.taller.bibliotecas.presentationLayer.exception.SolicitudInvalidaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteDAO clienteDAO;

    @Override
    public ClienteDTO createCliente(ClienteCreateDTO createDTO) {
        log.info("Registrando nuevo cliente: {}", createDTO.getEmail());
        if (clienteDAO.existsByDocumento(createDTO.getDocumento())) {
            throw new SolicitudInvalidaException("Ya existe un cliente registrado con el documento: " + createDTO.getDocumento());
        }
        if (clienteDAO.existsByEmail(createDTO.getEmail())) {
            throw new SolicitudInvalidaException("Ya existe un cliente registrado con el email: " + createDTO.getEmail());
        }
        return clienteDAO.save(createDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO getClienteById(Long id) {
        return clienteDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO getClienteByDocumento(String documento) {
        return clienteDAO.findByDocumento(documento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con documento: " + documento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDTO> getAllClientes() {
        return clienteDAO.findAll();
    }

    @Override
    public ClienteDTO updateCliente(Long id, ClienteUpdateDTO updateDTO) {
        if (updateDTO.getEmail() != null && clienteDAO.existsByEmailAndIdNot(updateDTO.getEmail(), id)) {
            throw new SolicitudInvalidaException("Ya existe otro cliente registrado con el email: " + updateDTO.getEmail());
        }
        return clienteDAO.update(id, updateDTO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
    }
}
