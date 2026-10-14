package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.CategoriaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.CategoriaEntity;
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
public class CategoriaMapperImpl implements CategoriaMapper {

    @Override
    public CategoriaDTO toDTO(CategoriaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        CategoriaDTO categoriaDTO = new CategoriaDTO();

        categoriaDTO.setCategoriaPadreId( entityCategoriaPadreEntityId( entity ) );
        categoriaDTO.setCategoriaPadreNombre( entityCategoriaPadreEntityNombre( entity ) );
        categoriaDTO.setId( entity.getId() );
        categoriaDTO.setNombre( entity.getNombre() );
        categoriaDTO.setDescripcion( entity.getDescripcion() );
        categoriaDTO.setCreatedAt( entity.getCreatedAt() );
        categoriaDTO.setUpdatedAt( entity.getUpdatedAt() );

        return categoriaDTO;
    }

    @Override
    public List<CategoriaDTO> toDTOList(List<CategoriaEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CategoriaDTO> list = new ArrayList<CategoriaDTO>( entities.size() );
        for ( CategoriaEntity categoriaEntity : entities ) {
            list.add( toDTO( categoriaEntity ) );
        }

        return list;
    }

    @Override
    public CategoriaEntity toEntity(CategoriaCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        CategoriaEntity categoriaEntity = new CategoriaEntity();

        categoriaEntity.setNombre( createDTO.getNombre() );
        categoriaEntity.setDescripcion( createDTO.getDescripcion() );

        return categoriaEntity;
    }

    @Override
    public void updateEntityFromDTO(CategoriaUpdateDTO updateDTO, CategoriaEntity entity) {
        if ( updateDTO == null ) {
            return;
        }

        if ( updateDTO.getNombre() != null ) {
            entity.setNombre( updateDTO.getNombre() );
        }
        if ( updateDTO.getDescripcion() != null ) {
            entity.setDescripcion( updateDTO.getDescripcion() );
        }
    }

    private Long entityCategoriaPadreEntityId(CategoriaEntity categoriaEntity) {
        CategoriaEntity categoriaPadreEntity = categoriaEntity.getCategoriaPadreEntity();
        if ( categoriaPadreEntity == null ) {
            return null;
        }
        return categoriaPadreEntity.getId();
    }

    private String entityCategoriaPadreEntityNombre(CategoriaEntity categoriaEntity) {
        CategoriaEntity categoriaPadreEntity = categoriaEntity.getCategoriaPadreEntity();
        if ( categoriaPadreEntity == null ) {
            return null;
        }
        return categoriaPadreEntity.getNombre();
    }
}
