package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.ClienteCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteDTO;
import com.taller.bibliotecas.businessLayer.dto.ClienteUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.ClienteEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-10T15:08:18-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 21.0.12 (Oracle Corporation)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public ClienteDTO toDTO(ClienteEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ClienteDTO clienteDTO = new ClienteDTO();

        clienteDTO.setId( entity.getId() );
        clienteDTO.setNombre( entity.getNombre() );
        clienteDTO.setDocumento( entity.getDocumento() );
        clienteDTO.setEmail( entity.getEmail() );
        clienteDTO.setTelefono( entity.getTelefono() );
        clienteDTO.setDireccion( entity.getDireccion() );
        clienteDTO.setCreatedAt( entity.getCreatedAt() );
        clienteDTO.setUpdatedAt( entity.getUpdatedAt() );

        return clienteDTO;
    }

    @Override
    public List<ClienteDTO> toDTOList(List<ClienteEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ClienteDTO> list = new ArrayList<ClienteDTO>( entities.size() );
        for ( ClienteEntity clienteEntity : entities ) {
            list.add( toDTO( clienteEntity ) );
        }

        return list;
    }

    @Override
    public ClienteEntity toEntity(ClienteCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        ClienteEntity clienteEntity = new ClienteEntity();

        clienteEntity.setNombre( createDTO.getNombre() );
        clienteEntity.setDocumento( createDTO.getDocumento() );
        clienteEntity.setEmail( createDTO.getEmail() );
        clienteEntity.setTelefono( createDTO.getTelefono() );
        clienteEntity.setDireccion( createDTO.getDireccion() );

        return clienteEntity;
    }

    @Override
    public void updateEntityFromDTO(ClienteUpdateDTO updateDTO, ClienteEntity entity) {
        if ( updateDTO == null ) {
            return;
        }

        if ( updateDTO.getNombre() != null ) {
            entity.setNombre( updateDTO.getNombre() );
        }
        if ( updateDTO.getEmail() != null ) {
            entity.setEmail( updateDTO.getEmail() );
        }
        if ( updateDTO.getTelefono() != null ) {
            entity.setTelefono( updateDTO.getTelefono() );
        }
        if ( updateDTO.getDireccion() != null ) {
            entity.setDireccion( updateDTO.getDireccion() );
        }
    }
}
