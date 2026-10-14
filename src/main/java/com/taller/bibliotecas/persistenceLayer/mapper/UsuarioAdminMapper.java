package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminDTO;
import com.taller.bibliotecas.businessLayer.dto.UsuarioAdminUpdateDTO;
import com.taller.bibliotecas.persistenceLayer.entity.UsuarioAdminEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface UsuarioAdminMapper {

    @Mapping(target = "sedeId", source = "sedeEntity.id")
    @Mapping(target = "sedeNombre", source = "sedeEntity.nombre")
    UsuarioAdminDTO toDTO(UsuarioAdminEntity entity);

    List<UsuarioAdminDTO> toDTOList(List<UsuarioAdminEntity> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sedeEntity", ignore = true)
    UsuarioAdminEntity toEntity(UsuarioAdminCreateDTO createDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sedeEntity", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UsuarioAdminUpdateDTO updateDTO, @MappingTarget UsuarioAdminEntity entity);
}
