package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.CategoriaCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaDTO;
import com.taller.bibliotecas.businessLayer.dto.CategoriaUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.CategoriaEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface CategoriaMapper {

    @Mapping(target = "categoriaPadreId", source = "categoriaPadreEntity.id")
    @Mapping(target = "categoriaPadreNombre", source = "categoriaPadreEntity.nombre")
    CategoriaDTO toDTO(CategoriaEntity entity);

    List<CategoriaDTO> toDTOList(List<CategoriaEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoriaPadreEntity", ignore = true)
    CategoriaEntity toEntity(CategoriaCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categoriaPadreEntity", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CategoriaUpdateDTO updateDTO, @MappingTarget CategoriaEntity entity);
}
