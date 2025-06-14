package com.ecomarket.resenas.model;

import jakarta.persistence.*;
import lombok.*;
import java.sql.Timestamp;

@Entity
@Table(name = "RESENAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resenaId;

    private Long productoId;
    private Long clienteId;
    private Long pedidoId;

    private String tipo; // reseña o reclamo
    private Integer calificacion;
    private String comentario;
    private Timestamp fechaCreacion;
    private String estado;
}
