package com.deliverytech.delivery.dto;

import com.deliverytech.delivery.entity.Restaurante;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestauranteResponseDTO {

    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private BigDecimal taxaEntrega;
    private Boolean ativo;

    public RestauranteResponseDTO(Restaurante restaurante) {
        this.id = restaurante.getId();
        this.nome = restaurante.getNome();
        this.categoria = restaurante.getCategoria();
        this.endereco = restaurante.getEndereco();
        this.taxaEntrega = restaurante.getTaxaEntrega();
        this.ativo = restaurante.getAtivo();
    }
}