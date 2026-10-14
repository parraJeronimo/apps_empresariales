package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.EjemplarCreateDTO;
import com.taller.bibliotecas.businessLayer.dto.EjemplarDTO;
import com.taller.bibliotecas.persistenceLayer.entity.EjemplarEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface EjemplarMapper {

    @Mapping(target = "libroId", source = "libroEntity.id")
    @Mapping(target = "libroTitulo", source = "libroEntity.titulo")
    @Mapping(target = "libroIsbn", source = "libroEntity.isbn")
    @Mapping(target = "sedePropietariaId", source = "sedePropietariaEntity.id")
    @Mapping(target = "sedePropietariaNombre", source = "sedePropietariaEntity.nombre")
    @Mapping(target = "sedeActualId", source = "sedeActualEntity.id")
    @Mapping(target = "sedeActualNombre", source = "sedeActualEntity.nombre")
    EjemplarDTO toDTO(EjemplarEntity entity);

    List<EjemplarDTO> toDTOList(List<EjemplarEntity> entities);

    /** Solo mapea el codigo; libro y sedes los asigna el DAO con referencias a las entidades existentes */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "libroEntity", ignore = true)
    @Mapping(target = "sedePropietariaEntity", ignore = true)
    @Mapping(target = "sedeActualEntity", ignore = true)
    EjemplarEntity toEntity(EjemplarCreateDTO createDTO);
}
