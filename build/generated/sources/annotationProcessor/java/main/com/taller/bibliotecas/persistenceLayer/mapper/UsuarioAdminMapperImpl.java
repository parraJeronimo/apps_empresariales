package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.SedeEntity;
import com.taller.bibliotecas.persistenceLayer.entity.UsuarioAdminEntity;
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
public class UsuarioAdminMapperImpl implements UsuarioAdminMapper {

    @Override
    public UsuarioAdminDTO toDTO(UsuarioAdminEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UsuarioAdminDTO usuarioAdminDTO = new UsuarioAdminDTO();

        usuarioAdminDTO.setSedeId( entitySedeEntityId( entity ) );
        usuarioAdminDTO.setSedeNombre( entitySedeEntityNombre( entity ) );
        usuarioAdminDTO.setId( entity.getId() );
        usuarioAdminDTO.setNombre( entity.getNombre() );
        usuarioAdminDTO.setEmail( entity.getEmail() );
        usuarioAdminDTO.setRol( entity.getRol() );
        usuarioAdminDTO.setActivo( entity.getActivo() );
        usuarioAdminDTO.setCreatedAt( entity.getCreatedAt() );
        usuarioAdminDTO.setUpdatedAt( entity.getUpdatedAt() );

        return usuarioAdminDTO;
    }

    @Override
    public List<UsuarioAdminDTO> toDTOList(List<UsuarioAdminEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<UsuarioAdminDTO> list = new ArrayList<UsuarioAdminDTO>( entities.size() );
        for ( UsuarioAdminEntity usuarioAdminEntity : entities ) {
            list.add( toDTO( usuarioAdminEntity ) );
        }

        return list;
    }

    @Override
    public UsuarioAdminEntity toEntity(UsuarioAdminCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        UsuarioAdminEntity usuarioAdminEntity = new UsuarioAdminEntity();

        usuarioAdminEntity.setNombre( createDTO.getNombre() );
        usuarioAdminEntity.setEmail( createDTO.getEmail() );
        usuarioAdminEntity.setRol( createDTO.getRol() );

        return usuarioAdminEntity;
    }

    @Override
    public void updateEntityFromDTO(UsuarioAdminUpdateDTO updateDTO, UsuarioAdminEntity entity) {
        if ( updateDTO == null ) {
            return;
        }

        if ( updateDTO.getNombre() != null ) {
            entity.setNombre( updateDTO.getNombre() );
        }
        if ( updateDTO.getEmail() != null ) {
            entity.setEmail( updateDTO.getEmail() );
        }
        if ( updateDTO.getRol() != null ) {
            entity.setRol( updateDTO.getRol() );
        }
        if ( updateDTO.getActivo() != null ) {
            entity.setActivo( updateDTO.getActivo() );
        }
    }

    private Long entitySedeEntityId(UsuarioAdminEntity usuarioAdminEntity) {
        SedeEntity sedeEntity = usuarioAdminEntity.getSedeEntity();
        if ( sedeEntity == null ) {
            return null;
        }
        return sedeEntity.getId();
    }

    private String entitySedeEntityNombre(UsuarioAdminEntity usuarioAdminEntity) {
        SedeEntity sedeEntity = usuarioAdminEntity.getSedeEntity();
        if ( sedeEntity == null ) {
            return null;
        }
        return sedeEntity.getNombre();
    }
}
