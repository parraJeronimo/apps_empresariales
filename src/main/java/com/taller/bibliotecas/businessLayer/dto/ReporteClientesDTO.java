package com.taller.bibliotecas.businessLayer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Reporte general de clientes")
public class ReporteClientesDTO {

    @Schema(description = "Total de clientes registrados")
    private Long totalClientes;

    @Schema(description = "Clientes con al menos un prestamo activo")
    private Integer clientesConPrestamosActivos;

    @Schema(description = "Clientes con al menos un prestamo vencido")
    private Integer clientesConPrestamosVencidos;

    @Schema(description = "Detalle de los clientes con prestamos vencidos")
    private List<ClienteDTO> clientesConRetraso;
}
