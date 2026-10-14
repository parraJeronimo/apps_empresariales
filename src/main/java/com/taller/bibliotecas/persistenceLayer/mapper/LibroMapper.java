package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.LibroCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroDTO;
import com.taller.bibliotecas.businessLayer.dto.LibroUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.LibroEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface LibroMapper {

    @Mapping(target = "categoriaId", source = "categoriaEntity.id")
    @Mapping(target = "categoriaNombre", source = "categoriaEntity.nombre")
    LibroDTO toDTO(LibroEntity entity);

    List<LibroDTO> toDTOList(List<LibroEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ejemplares", ignore = true)
    @Mapping(target = "categoriaEntity", ignore = true)
    LibroEntity toEntity(LibroCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ejemplares", ignore = true)
    @Mapping(target = "categoriaEntity", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(LibroUpdateDTO updateDTO, @MappingTarget LibroEntity entity);
}
