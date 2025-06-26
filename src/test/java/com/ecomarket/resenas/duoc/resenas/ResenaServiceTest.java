package com.ecomarket.resenas.duoc.resenas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.ecomarket.resenas.DTO.ProductoDTO;
import com.ecomarket.resenas.DTO.ResenaResponseDTO;
import com.ecomarket.resenas.DTO.UsuarioDTO;
import com.ecomarket.resenas.model.Resena;
import com.ecomarket.resenas.repository.ResenaRepository;
import com.ecomarket.resenas.service.ResenaService;

public class ResenaServiceTest {
    @Mock
    private ResenaRepository resenaRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ResenaService resenaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        resenaService = new ResenaService(resenaRepo, restTemplate);

        // Usamos Reflection para setear los valores privados con @Value
        ReflectionTestUtils.setField(resenaService, "productosApiKey", "test-productos-key");
        ReflectionTestUtils.setField(resenaService, "usuariosApiKey", "test-usuarios-key");
    }

    @Test
    void testObtenerConDetalles() {
        // Datos mock de reseña
        Resena resena = new Resena(1L, 100L, 200L, 300L, "reseña", 5, "Muy bueno",
                new Timestamp(System.currentTimeMillis()), "pendiente");

        when(resenaRepo.findById(1L)).thenReturn(Optional.of(resena));

        // Mock de ProductoDTO
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setNombre("Lechuga Fresca");

        // Mock de UsuarioDTO
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNombre("Matías Pérez");

        // Simular llamadas REST
        when(restTemplate.exchange(
                eq("http://localhost:8081/api/v1/productos/100"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ProductoDTO.class))).thenReturn(new ResponseEntity<>(productoDTO, HttpStatus.OK));

        when(restTemplate.exchange(
                eq("http://localhost:8080/api/v1/usuarios/id/200"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UsuarioDTO.class))).thenReturn(new ResponseEntity<>(usuarioDTO, HttpStatus.OK));

        // Ejecutar el método
        ResenaResponseDTO resultado = resenaService.obtenerConDetalles(1L);

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getResenaId());
        assertEquals("Lechuga Fresca", resultado.getNombreProducto());
        assertEquals("Matías Pérez", resultado.getNombreCliente());
        assertEquals("Muy bueno", resultado.getComentario());
        assertEquals("reseña", resultado.getTipo());

        // Verificar interacciones
        verify(resenaRepo).findById(1L);
        verify(restTemplate, times(1)).exchange(
                contains("productos/100"), eq(HttpMethod.GET), any(), eq(ProductoDTO.class));
        verify(restTemplate, times(1)).exchange(
                contains("usuarios/id/200"), eq(HttpMethod.GET), any(), eq(UsuarioDTO.class));
    }
}
