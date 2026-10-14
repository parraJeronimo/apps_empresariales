package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
import com.taller.bibliotecas.persistenceLayer.entity.SedeEntity;
import com.taller.bibliotecas.persistenceLayer.entity.TrasladoEntity;
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
public class TrasladoMapperImpl implements TrasladoMapper {

    @Override
    public TrasladoDTO toDTO(TrasladoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        TrasladoDTO trasladoDTO = new TrasladoDTO();

        trasladoDTO.setEjemplarId( entityEjemplarEntityId( entity ) );
        trasladoDTO.setEjemplarCodigo( entityEjemplarEntityCodigo( entity ) );
        trasladoDTO.setLibroTitulo( entityEjemplarEntityLibroEntityTitulo( entity ) );
        trasladoDTO.setSedeOrigenId( entitySedeOrigenEntityId( entity ) );
        trasladoDTO.setSedeOrigenNombre( entitySedeOrigenEntityNombre( entity ) );
        trasladoDTO.setSedeDestinoId( entitySedeDestinoEntityId( entity ) );
        trasladoDTO.setSedeDestinoNombre( entitySedeDestinoEntityNombre( entity ) );
        trasladoDTO.setId( entity.getId() );
        trasladoDTO.setEstado( entity.getEstado() );
        trasladoDTO.setFechaSolicitud( entity.getFechaSolicitud() );
        trasladoDTO.setFechaRecepcion( entity.getFechaRecepcion() );
        trasladoDTO.setObservaciones( entity.getObservaciones() );
        trasladoDTO.setCreatedAt( entity.getCreatedAt() );
        trasladoDTO.setUpdatedAt( entity.getUpdatedAt() );

        return trasladoDTO;
    }

    @Override
    public List<TrasladoDTO> toDTOList(List<TrasladoEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<TrasladoDTO> list = new ArrayList<TrasladoDTO>( entities.size() );
        for ( TrasladoEntity trasladoEntity : entities ) {
            list.add( toDTO( trasladoEntity ) );
        }

        return list;
    }

    private Long entityEjemplarEntityId(TrasladoEntity trasladoEntity) {
        EjemplarEntity ejemplarEntity = trasladoEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        return ejemplarEntity.getId();
    }

    private String entityEjemplarEntityCodigo(TrasladoEntity trasladoEntity) {
        EjemplarEntity ejemplarEntity = trasladoEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        return ejemplarEntity.getCodigo();
    }

    private String entityEjemplarEntityLibroEntityTitulo(TrasladoEntity trasladoEntity) {
        EjemplarEntity ejemplarEntity = trasladoEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        LibroEntity libroEntity = ejemplarEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getTitulo();
    }

    private Long entitySedeOrigenEntityId(TrasladoEntity trasladoEntity) {
        SedeEntity sedeOrigenEntity = trasladoEntity.getSedeOrigenEntity();
        if ( sedeOrigenEntity == null ) {
            return null;
        }
        return sedeOrigenEntity.getId();
    }

    private String entitySedeOrigenEntityNombre(TrasladoEntity trasladoEntity) {
        SedeEntity sedeOrigenEntity = trasladoEntity.getSedeOrigenEntity();
        if ( sedeOrigenEntity == null ) {
            return null;
        }
        return sedeOrigenEntity.getNombre();
    }

    private Long entitySedeDestinoEntityId(TrasladoEntity trasladoEntity) {
        SedeEntity sedeDestinoEntity = trasladoEntity.getSedeDestinoEntity();
        if ( sedeDestinoEntity == null ) {
            return null;
        }
        return sedeDestinoEntity.getId();
    }

    private String entitySedeDestinoEntityNombre(TrasladoEntity trasladoEntity) {
        SedeEntity sedeDestinoEntity = trasladoEntity.getSedeDestinoEntity();
        if ( sedeDestinoEntity == null ) {
            return null;
        }
        return sedeDestinoEntity.getNombre();
    }
}
