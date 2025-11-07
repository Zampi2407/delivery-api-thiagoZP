package com.deliverytech.delivery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestauranteRequestDTO {

    @NotBlank(message = "O nome do restaurante é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(min = 3, max = 50, message = "A categoria deve ter entre 3 e 50 caracteres")
    private String categoria;

    @NotBlank(message = "O endereço é obrigatório")
    @Size(min = 5, max = 150, message = "O endereço deve ter entre 5 e 150 caracteres")
    private String endereco;

    @NotNull(message = "A taxa de entrega é obrigatória")
    @DecimalMin(value = "0.00", message = "A taxa de entrega deve ser zero ou maior")
    private BigDecimal taxaEntrega;
}