package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.EjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
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
public class EjemplarMapperImpl implements EjemplarMapper {

    @Override
    public EjemplarDTO toDTO(EjemplarEntity entity) {
        if ( entity == null ) {
            return null;
        }

        EjemplarDTO ejemplarDTO = new EjemplarDTO();

        ejemplarDTO.setLibroId( entityLibroEntityId( entity ) );
        ejemplarDTO.setLibroTitulo( entityLibroEntityTitulo( entity ) );
        ejemplarDTO.setLibroIsbn( entityLibroEntityIsbn( entity ) );
        ejemplarDTO.setSedePropietariaId( entitySedePropietariaEntityId( entity ) );
        ejemplarDTO.setSedePropietariaNombre( entitySedePropietariaEntityNombre( entity ) );
        ejemplarDTO.setSedeActualId( entitySedeActualEntityId( entity ) );
        ejemplarDTO.setSedeActualNombre( entitySedeActualEntityNombre( entity ) );
        ejemplarDTO.setId( entity.getId() );
        ejemplarDTO.setCodigo( entity.getCodigo() );
        ejemplarDTO.setEstado( entity.getEstado() );
        ejemplarDTO.setCreatedAt( entity.getCreatedAt() );
        ejemplarDTO.setUpdatedAt( entity.getUpdatedAt() );

        return ejemplarDTO;
    }

    @Override
    public List<EjemplarDTO> toDTOList(List<EjemplarEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<EjemplarDTO> list = new ArrayList<EjemplarDTO>( entities.size() );
        for ( EjemplarEntity ejemplarEntity : entities ) {
            list.add( toDTO( ejemplarEntity ) );
        }

        return list;
    }

    @Override
    public EjemplarEntity toEntity(EjemplarCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        EjemplarEntity ejemplarEntity = new EjemplarEntity();

        ejemplarEntity.setCodigo( createDTO.getCodigo() );

        return ejemplarEntity;
    }

    private Long entityLibroEntityId(EjemplarEntity ejemplarEntity) {
        LibroEntity libroEntity = ejemplarEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getId();
    }

    private String entityLibroEntityTitulo(EjemplarEntity ejemplarEntity) {
        LibroEntity libroEntity = ejemplarEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getTitulo();
    }

    private String entityLibroEntityIsbn(EjemplarEntity ejemplarEntity) {
        LibroEntity libroEntity = ejemplarEntity.getLibroEntity();
        if ( libroEntity == null ) {
            return null;
        }
        return libroEntity.getIsbn();
    }

    private Long entitySedePropietariaEntityId(EjemplarEntity ejemplarEntity) {
        SedeEntity sedePropietariaEntity = ejemplarEntity.getSedePropietariaEntity();
        if ( sedePropietariaEntity == null ) {
            return null;
        }
        return sedePropietariaEntity.getId();
    }

    private String entitySedePropietariaEntityNombre(EjemplarEntity ejemplarEntity) {
        SedeEntity sedePropietariaEntity = ejemplarEntity.getSedePropietariaEntity();
        if ( sedePropietariaEntity == null ) {
            return null;
        }
        return sedePropietariaEntity.getNombre();
    }

    private Long entitySedeActualEntityId(EjemplarEntity ejemplarEntity) {
        SedeEntity sedeActualEntity = ejemplarEntity.getSedeActualEntity();
        if ( sedeActualEntity == null ) {
            return null;
        }
        return sedeActualEntity.getId();
    }

    private String entitySedeActualEntityNombre(EjemplarEntity ejemplarEntity) {
        SedeEntity sedeActualEntity = ejemplarEntity.getSedeActualEntity();
        if ( sedeActualEntity == null ) {
            return null;
        }
        return sedeActualEntity.getNombre();
    }
}
