package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.persistenceLayer.entity.ClienteEntity;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
import com.taller.bibliotecas.persistenceLayer.entity.ReservaEntity;
import com.taller.bibliotecas.persistenceLayer.entity.SedeEntity;
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
public class ReservaMapperImpl implements ReservaMapper {

    @Override
    public ReservaDTO toDTO(ReservaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ReservaDTO reservaDTO = new ReservaDTO();

        reservaDTO.setClienteId( entityClienteEntityId( entity ) );
        reservaDTO.setClienteNombre( entityClienteEntityNombre( entity ) );
        reservaDTO.setLibroId( entityLibroEntityId( entity ) );
        reservaDTO.setLibroTitulo( entityLibroEntityTitulo( entity ) );
        reservaDTO.setSedeId( entitySedeEntityId( entity ) );
        reservaDTO.setSedeNombre( entitySedeEntityNombre( entity ) );
        reservaDTO.setId( entity.getId() );
        reservaDTO.setEstado( entity.getEstado() );
        reservaDTO.setMotivoRechazo( entity.getMotivoRechazo() );
        reservaDTO.setFechaSolicitud( entity.getFechaSolicitud() );
        reservaDTO.setFechaRespuesta( entity.getFechaRespuesta() );
        reservaDTO.setCreatedAt( entity.getCreatedAt() );
        reservaDTO.setUpdatedAt( entity.getUpdatedAt() );

        return reservaDTO;
    }

    @Override
    public List<ReservaDTO> toDTOList(List<ReservaEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ReservaDTO> list = new ArrayList<ReservaDTO>( entities.size() );
        for ( ReservaEntity reservaEntity : entities ) {
            list.add( toDTO( reservaEntity ) );
        }

        return list;
    }

    private Long entityClienteEntityId(ReservaEntity reservaEntity) {
        ClienteEntity clienteEntity = reservaEntity.getClienteEntity();
        if ( clienteEntity == null ) {
            return null;
        }
        return clienteEntity.getId();
    }

    private String entityClienteEntityNombre(ReservaEntity reservaEntity) {
        ClienteEntity clienteEntity = reservaEntity.getClienteEntity();
        if ( clienteEntity == null ) {
            return null;
        }
        return clienteEntity.getNombre();
    }

    private Long entityLibroEntityId(ReservaEntity reservaEntity) {
        LibroEntity libroEntity = reservaEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getId();
    }

    private String entityLibroEntityTitulo(ReservaEntity reservaEntity) {
        LibroEntity libroEntity = reservaEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getTitulo();
    }

    private Long entitySedeEntityId(ReservaEntity reservaEntity) {
        SedeEntity sedeEntity = reservaEntity.getSedeEntity();
        if ( sedeEntity == null ) {
            return null;
        }
        return sedeEntity.getId();
    }

    private String entitySedeEntityNombre(ReservaEntity reservaEntity) {
        SedeEntity sedeEntity = reservaEntity.getSedeEntity();
        if ( sedeEntity == null ) {
            return null;
        }
        return sedeEntity.getNombre();
    }
}
