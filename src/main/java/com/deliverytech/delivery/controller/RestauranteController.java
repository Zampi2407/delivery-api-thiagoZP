package com.deliverytech.delivery.controller;

import com.deliverytech.delivery.dto.RestauranteRequestDTO;
import com.deliverytech.delivery.dto.RestauranteResponseDTO;
import com.deliverytech.delivery.exceptions.BusinessException;
import com.deliverytech.delivery.services.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    /**
     * Cadastrar restaurante
     */
    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody RestauranteRequestDTO dto) {
        try {
            RestauranteResponseDTO restauranteSalvo = restauranteService.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(restauranteSalvo);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    /**
     * Buscar restaurante por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            RestauranteResponseDTO restaurante = restauranteService.buscarPorId(id);
            return ResponseEntity.ok(restaurante);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    /**
     * Listar restaurantes disponíveis
     */
    @GetMapping
    public ResponseEntity<?> listarDisponiveis() {
        try {
            List<RestauranteResponseDTO> restaurantes = restauranteService.listarDisponiveis();
            if (restaurantes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(restaurantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    /**
     * Buscar restaurantes por categoria
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<?> buscarPorCategoria(@PathVariable String categoria) {
        try {
            List<RestauranteResponseDTO> restaurantes = restauranteService.buscarPorCategoria(categoria);
            return ResponseEntity.ok(restaurantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    /**
     * Atualizar restaurante
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody RestauranteRequestDTO dto) {
        try {
            RestauranteResponseDTO atualizado = restauranteService.atualizar(id, dto);
            return ResponseEntity.ok(atualizado);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    /**
     * Calcular taxa de entrega por CEP
     */
    @GetMapping("/{id}/taxa-entrega/{cep}")
    public ResponseEntity<?> calcularTaxaEntrega(@PathVariable Long id, @PathVariable String cep) {
        try {
            BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
            return ResponseEntity.ok(taxa);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    /**
     * Inativar restaurante
     */
    @PutMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            restauranteService.inativar(id);
            return ResponseEntity.ok("Restaurante inativado com sucesso");
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }
}