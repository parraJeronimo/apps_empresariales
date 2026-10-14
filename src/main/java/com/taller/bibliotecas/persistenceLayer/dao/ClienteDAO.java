package com.taller.bibliotecas.persistenceLayer.dao;

import com.taller.bibliotecas.businessLayer.dto.ClienteCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.ClienteEntity;
import com.taller.bibliotecas.persistenceLayer.mapper.ClienteMapper;
import com.taller.bibliotecas.persistenceLayer.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ClienteDAO {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteDTO save(ClienteCreateDTO createDTO) {
        ClienteEntity entity = clienteMapper.toEntity(createDTO);
        return clienteMapper.toDTO(clienteRepository.save(entity));
    }

    public Optional<ClienteDTO> findById(Long id) {
        return clienteRepository.findById(id).map(clienteMapper::toDTO);
    }

    public Optional<ClienteDTO> findByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento).map(clienteMapper::toDTO);
    }

    public List<ClienteDTO> findAll() {
        return clienteMapper.toDTOList(clienteRepository.findAll());
    }

    public Optional<ClienteDTO> update(Long id, ClienteUpdateDTO updateDTO) {
        return clienteRepository.findById(id).map(existing -> {
            clienteMapper.updateEntityFromDTO(updateDTO, existing);
            return clienteMapper.toDTO(clienteRepository.save(existing));
        });
    }

    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return clienteRepository.existsByEmailAndIdNot(email, id);
    }

    public boolean existsByDocumento(String documento) {
        return clienteRepository.existsByDocumento(documento);
    }

    public long count() {
        return clienteRepository.count();
    }
}
