package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.PrestamoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.PrestamoEntity;
import org.mapstruct.*;

import java.util.List;

/** Solo convierte Entity -> DTO; el DAO arma la entidad al crear un prestamo. */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface PrestamoMapper {

    @Mapping(target = "ejemplarId", source = "ejemplarEntity.id")
    @Mapping(target = "ejemplarCodigo", source = "ejemplarEntity.codigo")
    @Mapping(target = "libroId", source = "ejemplarEntity.libroEntity.id")
    @Mapping(target = "libroTitulo", source = "ejemplarEntity.libroEntity.titulo")
    @Mapping(target = "clienteId", source = "clienteEntity.id")
    @Mapping(target = "clienteNombre", source = "clienteEntity.nombre")
    @Mapping(target = "sedePrestamoId", source = "sedePrestamoEntity.id")
    @Mapping(target = "sedePrestamoNombre", source = "sedePrestamoEntity.nombre")
    @Mapping(target = "sedeDevolucionId", source = "sedeDevolucionEntity.id")
    @Mapping(target = "sedeDevolucionNombre", source = "sedeDevolucionEntity.nombre")
    @Mapping(target = "vencido", ignore = true)
    PrestamoDTO toDTO(PrestamoEntity entity);

    List<PrestamoDTO> toDTOList(List<PrestamoEntity> entities);
}
