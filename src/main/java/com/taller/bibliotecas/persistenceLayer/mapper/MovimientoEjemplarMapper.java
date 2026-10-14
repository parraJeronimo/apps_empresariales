package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.MovimientoEjemplarDTO;
import com.taller.bibliotecas.persistenceLayer.entity.MovimientoEjemplarEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface MovimientoEjemplarMapper {

    @Mapping(target = "ejemplarId", source = "ejemplarEntity.id")
    @Mapping(target = "ejemplarCodigo", source = "ejemplarEntity.codigo")
    @Mapping(target = "sedeOrigenId", source = "sedeOrigenEntity.id")
    @Mapping(target = "sedeOrigenNombre", source = "sedeOrigenEntity.nombre")
    @Mapping(target = "sedeDestinoId", source = "sedeDestinoEntity.id")
    @Mapping(target = "sedeDestinoNombre", source = "sedeDestinoEntity.nombre")
    MovimientoEjemplarDTO toDTO(MovimientoEjemplarEntity entity);

    List<MovimientoEjemplarDTO> toDTOList(List<MovimientoEjemplarEntity> entities);
}
