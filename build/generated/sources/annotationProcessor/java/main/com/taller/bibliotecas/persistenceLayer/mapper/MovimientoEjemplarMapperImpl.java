package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.entity.MovimientoEjemplarEntity;
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
public class MovimientoEjemplarMapperImpl implements MovimientoEjemplarMapper {

    @Override
    public MovimientoEjemplarDTO toDTO(MovimientoEjemplarEntity entity) {
        if ( entity == null ) {
            return null;
        }

        MovimientoEjemplarDTO movimientoEjemplarDTO = new MovimientoEjemplarDTO();

        movimientoEjemplarDTO.setEjemplarId( entityEjemplarEntityId( entity ) );
        movimientoEjemplarDTO.setEjemplarCodigo( entityEjemplarEntityCodigo( entity ) );
        movimientoEjemplarDTO.setSedeOrigenId( entitySedeOrigenEntityId( entity ) );
        movimientoEjemplarDTO.setSedeOrigenNombre( entitySedeOrigenEntityNombre( entity ) );
        movimientoEjemplarDTO.setSedeDestinoId( entitySedeDestinoEntityId( entity ) );
        movimientoEjemplarDTO.setSedeDestinoNombre( entitySedeDestinoEntityNombre( entity ) );
        movimientoEjemplarDTO.setId( entity.getId() );
        movimientoEjemplarDTO.setTipo( entity.getTipo() );
        movimientoEjemplarDTO.setFechaMovimiento( entity.getFechaMovimiento() );
        movimientoEjemplarDTO.setDetalle( entity.getDetalle() );

        return movimientoEjemplarDTO;
    }

    @Override
    public List<MovimientoEjemplarDTO> toDTOList(List<MovimientoEjemplarEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<MovimientoEjemplarDTO> list = new ArrayList<MovimientoEjemplarDTO>( entities.size() );
        for ( MovimientoEjemplarEntity movimientoEjemplarEntity : entities ) {
            list.add( toDTO( movimientoEjemplarEntity ) );
        }

        return list;
    }

    private Long entityEjemplarEntityId(MovimientoEjemplarEntity movimientoEjemplarEntity) {
        EjemplarEntity ejemplarEntity = movimientoEjemplarEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        return ejemplarEntity.getId();
    }

    private String entityEjemplarEntityCodigo(MovimientoEjemplarEntity movimientoEjemplarEntity) {
        EjemplarEntity ejemplarEntity = movimientoEjemplarEntity.getEjemplarEntity();
        if ( ejemplarEntity == null ) {
            return null;
        }
        return ejemplarEntity.getCodigo();
    }

    private Long entitySedeOrigenEntityId(MovimientoEjemplarEntity movimientoEjemplarEntity) {
        SedeEntity sedeOrigenEntity = movimientoEjemplarEntity.getSedeOrigenEntity();
        if ( sedeOrigenEntity == null ) {
            return null;
        }
        return sedeOrigenEntity.getId();
    }

    private String entitySedeOrigenEntityNombre(MovimientoEjemplarEntity movimientoEjemplarEntity) {
        SedeEntity sedeOrigenEntity = movimientoEjemplarEntity.getSedeOrigenEntity();
        if ( sedeOrigenEntity == null ) {
            return null;
        }
        return sedeOrigenEntity.getNombre();
    }

    private Long entitySedeDestinoEntityId(MovimientoEjemplarEntity movimientoEjemplarEntity) {
        SedeEntity sedeDestinoEntity = movimientoEjemplarEntity.getSedeDestinoEntity();
        if ( sedeDestinoEntity == null ) {
            return null;
        }
        return sedeDestinoEntity.getId();
    }

    private String entitySedeDestinoEntityNombre(MovimientoEjemplarEntity movimientoEjemplarEntity) {
        SedeEntity sedeDestinoEntity = movimientoEjemplarEntity.getSedeDestinoEntity();
        if ( sedeDestinoEntity == null ) {
            return null;
        }
        return sedeDestinoEntity.getNombre();
    }
}
