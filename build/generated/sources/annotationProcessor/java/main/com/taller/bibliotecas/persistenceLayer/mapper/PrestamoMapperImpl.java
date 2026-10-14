package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.ClienteEntity;
import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
import com.taller.bibliotecas.persistenceLayer.entity.PrestamoEntity;
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
public class PrestamoMapperImpl implements PrestamoMapper {

    @Override
    public PrestamoDTO toDTO(PrestamoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        PrestamoDTO prestamoDTO = new PrestamoDTO();

        prestamoDTO.setEjemplarId( entityEjemplarEntityId( entity ) );
        prestamoDTO.setEjemplarCodigo( entityEjemplarEntityCodigo( entity ) );
        prestamoDTO.setLibroId( entityEjemplarEntityLibroEntityId( entity ) );
        prestamoDTO.setLibroTitulo( entityEjemplarEntityLibroEntityTitulo( entity ) );
        prestamoDTO.setClienteId( entityClienteEntityId( entity ) );
        prestamoDTO.setClienteNombre( entityClienteEntityNombre( entity ) );
        prestamoDTO.setSedePrestamoId( entitySedePrestamoEntityId( entity ) );
        prestamoDTO.setSedePrestamoNombre( entitySedePrestamoEntityNombre( entity ) );
        prestamoDTO.setSedeDevolucionId( entitySedeDevolucionEntityId( entity ) );
        prestamoDTO.setSedeDevolucionNombre( entitySedeDevolucionEntityNombre( entity ) );
        prestamoDTO.setId( entity.getId() );
        prestamoDTO.setFechaInicio( entity.getFechaInicio() );
        prestamoDTO.setFechaLimite( entity.getFechaLimite() );
        prestamoDTO.setFechaDevolucion( entity.getFechaDevolucion() );
        prestamoDTO.setEstado( entity.getEstado() );
        prestamoDTO.setDevueltoConRetraso( entity.getDevueltoConRetraso() );
        prestamoDTO.setDiasRetraso( entity.getDiasRetraso() );
        prestamoDTO.setCreatedAt( entity.getCreatedAt() );
        prestamoDTO.setUpdatedAt( entity.getUpdatedAt() );

        return prestamoDTO;
    }

    @Override
    public List<PrestamoDTO> toDTOList(List<PrestamoEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<PrestamoDTO> list = new ArrayList<PrestamoDTO>( entities.size() );
        for ( PrestamoEntity prestamoEntity : entities ) {
            list.add( toDTO( prestamoEntity ) );
        }

        return list;
    }

    private Long entityEjemplarEntityId(PrestamoEntity prestamoEntity) {
        EjemplarEntity ejemplarEntity = prestamoEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        return ejemplarEntity.getId();
    }

    private String entityEjemplarEntityCodigo(PrestamoEntity prestamoEntity) {
        EjemplarEntity ejemplarEntity = prestamoEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        return ejemplarEntity.getCodigo();
    }

    private Long entityEjemplarEntityLibroEntityId(PrestamoEntity prestamoEntity) {
        EjemplarEntity ejemplarEntity = prestamoEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        LibroEntity libroEntity = ejemplarEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getId();
    }

    private String entityEjemplarEntityLibroEntityTitulo(PrestamoEntity prestamoEntity) {
        EjemplarEntity ejemplarEntity = prestamoEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        LibroEntity libroEntity = ejemplarEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getTitulo();
    }

    private Long entityClienteEntityId(PrestamoEntity prestamoEntity) {
        ClienteEntity clienteEntity = prestamoEntity.getClienteEntity();
        if ( clienteEntity == null ) {
            return null;
        }
        return clienteEntity.getId();
    }

    private String entityClienteEntityNombre(PrestamoEntity prestamoEntity) {
        ClienteEntity clienteEntity = prestamoEntity.getClienteEntity();
        if ( clienteEntity == null ) {
            return null;
        }
        return clienteEntity.getNombre();
    }

    private Long entitySedePrestamoEntityId(PrestamoEntity prestamoEntity) {
        SedeEntity sedePrestamoEntity = prestamoEntity.getSedePrestamoEntity();
        if ( sedePrestamoEntity == null ) {
            return null;
        }
        return sedePrestamoEntity.getId();
    }

    private String entitySedePrestamoEntityNombre(PrestamoEntity prestamoEntity) {
        SedeEntity sedePrestamoEntity = prestamoEntity.getSedePrestamoEntity();
        if ( sedePrestamoEntity == null ) {
            return null;
        }
        return sedePrestamoEntity.getNombre();
    }

    private Long entitySedeDevolucionEntityId(PrestamoEntity prestamoEntity) {
        SedeEntity sedeDevolucionEntity = prestamoEntity.getSedeDevolucionEntity();
        if ( sedeDevolucionEntity == null ) {
            return null;
        }
        return sedeDevolucionEntity.getId();
    }

    private String entitySedeDevolucionEntityNombre(PrestamoEntity prestamoEntity) {
        SedeEntity sedeDevolucionEntity = prestamoEntity.getSedeDevolucionEntity();
        if ( sedeDevolucionEntity == null ) {
            return null;
        }
        return sedeDevolucionEntity.getNombre();
    }
}
