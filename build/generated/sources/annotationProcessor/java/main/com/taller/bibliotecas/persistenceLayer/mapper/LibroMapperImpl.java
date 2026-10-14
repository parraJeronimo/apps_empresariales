package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.LibroCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.CategoriaEntity;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
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
public class LibroMapperImpl implements LibroMapper {

    @Override
    public LibroDTO toDTO(LibroEntity entity) {
        if ( entity == null ) {
            return null;
        }

        LibroDTO libroDTO = new LibroDTO();

        libroDTO.setCategoriaId( entityCategoriaEntityId( entity ) );
        libroDTO.setCategoriaNombre( entityCategoriaEntityNombre( entity ) );
        libroDTO.setId( entity.getId() );
        libroDTO.setTitulo( entity.getTitulo() );
        libroDTO.setAutor( entity.getAutor() );
        libroDTO.setIsbn( entity.getIsbn() );
        libroDTO.setEditorial( entity.getEditorial() );
        libroDTO.setAnioPublicacion( entity.getAnioPublicacion() );
        libroDTO.setDescripcion( entity.getDescripcion() );
        libroDTO.setCreatedAt( entity.getCreatedAt() );
        libroDTO.setUpdatedAt( entity.getUpdatedAt() );

        return libroDTO;
    }

    @Override
    public List<LibroDTO> toDTOList(List<LibroEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<LibroDTO> list = new ArrayList<LibroDTO>( entities.size() );
        for ( LibroEntity libroEntity : entities ) {
            list.add( toDTO( libroEntity ) );
        }

        return list;
    }

    @Override
    public LibroEntity toEntity(LibroCreateDTO createDTO) {
        if ( createDTO == null ) {
            return null;
        }

        LibroEntity libroEntity = new LibroEntity();

        libroEntity.setTitulo( createDTO.getTitulo() );
        libroEntity.setAutor( createDTO.getAutor() );
        libroEntity.setIsbn( createDTO.getIsbn() );
        libroEntity.setEditorial( createDTO.getEditorial() );
        libroEntity.setAnioPublicacion( createDTO.getAnioPublicacion() );
        libroEntity.setDescripcion( createDTO.getDescripcion() );

        return libroEntity;
    }

    @Override
    public void updateEntityFromDTO(LibroUpdateDTO updateDTO, LibroEntity entity) {
        if ( updateDTO == null ) {
            return;
        }

        if ( updateDTO.getTitulo() != null ) {
            entity.setTitulo( updateDTO.getTitulo() );
        }
        if ( updateDTO.getAutor() != null ) {
            entity.setAutor( updateDTO.getAutor() );
        }
        if ( updateDTO.getIsbn() != null ) {
            entity.setIsbn( updateDTO.getIsbn() );
        }
        if ( updateDTO.getEditorial() != null ) {
            entity.setEditorial( updateDTO.getEditorial() );
        }
        if ( updateDTO.getAnioPublicacion() != null ) {
            entity.setAnioPublicacion( updateDTO.getAnioPublicacion() );
        }
        if ( updateDTO.getDescripcion() != null ) {
            entity.setDescripcion( updateDTO.getDescripcion() );
        }
    }

    private Long entityCategoriaEntityId(LibroEntity libroEntity) {
        CategoriaEntity categoriaEntity = libroEntity.getCategoriaEntity();
        if ( categoriaEntity == null ) {
            return null;
        }
        return categoriaEntity.getId();
    }

    private String entityCategoriaEntityNombre(LibroEntity libroEntity) {
        CategoriaEntity categoriaEntity = libroEntity.getCategoriaEntity();
        if ( categoriaEntity == null ) {
            return null;
        }
        return categoriaEntity.getNombre();
    }
}
