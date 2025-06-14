package com.ecomarket.resenas.DTO;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaResponseDTO {
    private Long resenaId;
    private Long productoId;
    private String nombreProducto;
    private Long clienteId;
    private String nombreCliente;
    private Long pedidoId;
    private String tipo;
    private Integer calificacion;
    private String comentario;
    private Timestamp fechaCreacion;
    private String estado;
}
