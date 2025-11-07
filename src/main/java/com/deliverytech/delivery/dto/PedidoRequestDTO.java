package com.deliverytech.delivery.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoRequestDTO {

    @NotBlank(message = "Número do pedido é obrigatório")
    private String numeroPedido;

    @NotNull(message = "Data do pedido é obrigatória")
    private LocalDateTime dataPedido;

    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor total deve ser maior que zero")
    private BigDecimal valorTotal;

    @NotBlank(message = "Observações são obrigatórias")
    private String observacoes;

    @NotNull(message = "ID do cliente é obrigatório")
    private Long clienteId;

    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;

    @NotNull(message = "Itens do pedido são obrigatórios")
    @Valid
    private List<ItemPedidoDTO> itens;
}