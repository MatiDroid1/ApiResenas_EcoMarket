package com.ecomarket.resenas.service;

import com.ecomarket.resenas.DTO.*;
import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.key.productos}")
    private String productosApiKey;

    @Value("${api.key.usuarios}")
    private String usuariosApiKey;

    public ResenaService(ResenaRepository resenaRepo, RestTemplate restTemplate) {
        this.resenaRepo = resenaRepo;
        this.restTemplate = restTemplate;
    }


    public List<Resena> listar() {
        return resenaRepo.findAll();
    }

    public Resena guardar(Resena r) {
        r.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        r.setEstado("pendiente");
        return resenaRepo.save(r);
    }

    public ResenaResponseDTO obtenerConDetalles(Long id) {
    Resena r = resenaRepo.findById(id).orElse(null);
    if (r == null) return null;

    // Llamar a microservicio productos
    HttpHeaders headersProducto = new HttpHeaders();
    headersProducto.set("X-API-KEY", productosApiKey);
    ProductoDTO producto = restTemplate.exchange(
            "http://localhost:8081/api/v1/productos/" + r.getProductoId(),
            HttpMethod.GET,
            new HttpEntity<>(headersProducto),
            ProductoDTO.class
    ).getBody();

    // Llamar a microservicio usuarios (por ID)
    HttpHeaders headersUsuario = new HttpHeaders();
    headersUsuario.set("X-API-KEY", usuariosApiKey);
    UsuarioDTO usuario = restTemplate.exchange(
            "http://localhost:8080/api/v1/usuarios/id/" + r.getClienteId(),
            HttpMethod.GET,
            new HttpEntity<>(headersUsuario),
            UsuarioDTO.class
    ).getBody();

    return new ResenaResponseDTO(
            r.getResenaId(),
            r.getProductoId(),
            producto != null ? producto.getNombre() : "Producto no disponible",
            r.getClienteId(),
            usuario != null ? usuario.getNombre() : "Cliente no disponible",
            r.getPedidoId(),
            r.getTipo(),
            r.getCalificacion(),
            r.getComentario(),
            r.getFechaCreacion(),
            r.getEstado()
    );
}

}