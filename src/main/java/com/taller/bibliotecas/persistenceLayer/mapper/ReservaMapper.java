package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.ReservaDTO;
import com.taller.bibliotecas.persistenceLayer.entity.ReservaEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface ReservaMapper {

    @Mapping(target = "clienteId", source = "clienteEntity.id")
    @Mapping(target = "clienteNombre", source = "clienteEntity.nombre")
    @Mapping(target = "libroId", source = "libroEntity.id")
    @Mapping(target = "libroTitulo", source = "libroEntity.titulo")
    @Mapping(target = "sedeId", source = "sedeEntity.id")
    @Mapping(target = "sedeNombre", source = "sedeEntity.nombre")
    ReservaDTO toDTO(ReservaEntity entity);

    List<ReservaDTO> toDTOList(List<ReservaEntity> entities);
}
