package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.SedeCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeDTO;
import com.taller.bibliotecas.businessLayer.dto.SedeUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.SedeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-10T15:08:17-0500",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 21.0.12 (Oracle Corporation)"
)
@Component
public class SedeMapperImpl implements SedeMapper {

    @Override
    public SedeDTO toDTO(SedeEntity entity) {
        if ( entity == null ) {
            return null;
        }

        SedeDTO sedeDTO = new SedeDTO();

        sedeDTO.setId( entity.getId() );
        sedeDTO.setNombre( entity.getNombre() );
        sedeDTO.setDireccion( entity.getDireccion() );
        sedeDTO.setCiudad( entity.getCiudad() );
        sedeDTO.setTelefono( entity.getTelefono() );
        sedeDTO.setActiva( entity.getActiva() );
        sedeDTO.setCreatedAt( entity.getCreatedAt() );
        sedeDTO.setUpdatedAt( entity.getUpdatedAt() );

        return sedeDTO;
    }

    @Override
    public List<SedeDTO> toDTOList(List<SedeEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<SedeDTO> list = new ArrayList<SedeDTO>( entities.size() );
        for ( SedeEntity sedeEntity : entities ) {
            list.add( toDTO( sedeEntity ) );
        }

        return list;
    }

    @Override
    public SedeEntity toEntity(SedeCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        SedeEntity sedeEntity = new SedeEntity();

        sedeEntity.setNombre( createDTO.getNombre() );
        sedeEntity.setDireccion( createDTO.getDireccion() );
        sedeEntity.setCiudad( createDTO.getCiudad() );
        sedeEntity.setTelefono( createDTO.getTelefono() );

        return sedeEntity;
    }

    @Override
    public void updateEntityFromDTO(SedeUpdateDTO updateDTO, SedeEntity entity) {
        if ( updateDTO == null ) {
            return;
        }

        if ( updateDTO.getNombre() != null ) {
            entity.setNombre( updateDTO.getNombre() );
        }
        if ( updateDTO.getDireccion() != null ) {
            entity.setDireccion( updateDTO.getDireccion() );
        }
        if ( updateDTO.getCiudad() != null ) {
            entity.setCiudad( updateDTO.getCiudad() );
        }
        if ( updateDTO.getTelefono() != null ) {
            entity.setTelefono( updateDTO.getTelefono() );
        }
    }
}
