package com.taller.bibliotecas.persistenceLayer.mapper;

import com.taller.bibliotecas.businessLayer.dto.TrasladoDTO;
import com.taller.bibliotecas.persistenceLayer.entity.TrasladoEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface TrasladoMapper {

    @Mapping(target = "ejemplarId", source = "ejemplarEntity.id")
    @Mapping(target = "ejemplarCodigo", source = "ejemplarEntity.codigo")
    @Mapping(target = "libroTitulo", source = "ejemplarEntity.libroEntity.titulo")
    @Mapping(target = "sedeOrigenId", source = "sedeOrigenEntity.id")
    @Mapping(target = "sedeOrigenNombre", source = "sedeOrigenEntity.nombre")
    @Mapping(target = "sedeDestinoId", source = "sedeDestinoEntity.id")
    @Mapping(target = "sedeDestinoNombre", source = "sedeDestinoEntity.nombre")
    TrasladoDTO toDTO(TrasladoEntity entity);

    List<TrasladoDTO> toDTOList(List<TrasladoEntity> entities);
}
