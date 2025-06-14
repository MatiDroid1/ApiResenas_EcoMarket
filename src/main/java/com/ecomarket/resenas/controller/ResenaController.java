package com.ecomarket.resenas.controller;

import com.ecomarket.resenas.DTO.ResenaResponseDTO;
import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.service.ResenaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/v1/resenas")
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    /* */
    @GetMapping("/{id}/detallada")
    public ResponseEntity<?> getResenaDetallada(@PathVariable Long id) {
        ResenaResponseDTO dto = resenaService.obtenerConDetalles(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/detalladahetoas")
    public ResponseEntity<?> getResenaDetalladahtoas(@PathVariable Long id) {
        ResenaResponseDTO dto = resenaService.obtenerConDetalles(id);
        if (dto == null)
            return ResponseEntity.notFound().build();

        EntityModel<ResenaResponseDTO> model = EntityModel.of(dto);
        model.add(linkTo(methodOn(ResenaController.class).getResenaDetallada(id)).withRel("links"));
        model.add(linkTo(methodOn(ResenaController.class).listar()).withRel("todas-las-resenas"));
        // Link al producto (en otro microservicio)
        model.add(Link.of("http://localhost:8081/api/v1/productos/" + dto.getProductoId())
                .withRel("producto"));

        // Link al usuario (en otro microservicio)
        model.add(Link.of("http://localhost:8080/api/v1/usuarios/id/" + dto.getClienteId())
                .withRel("cliente"));
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Resena resena) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.guardar(resena));
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(resenaService.listar());
    }
}