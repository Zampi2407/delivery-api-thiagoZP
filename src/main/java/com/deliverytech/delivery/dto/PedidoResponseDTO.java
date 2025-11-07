package com.deliverytech.delivery.dto;

import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.enums.StatusPedido;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PedidoResponseDTO {

    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private String observacoes;
    private Long clienteId;
    private Long restauranteId;
    private List<Long> itens;

    public PedidoResponseDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.numeroPedido = pedido.getNumeroPedido();
        this.dataPedido = pedido.getDataPedido();
        this.status = pedido.getStatus();
        this.valorTotal = pedido.getValorTotal();
        this.observacoes = pedido.getObservacoes();
        this.clienteId = pedido.getCliente().getId();
        this.restauranteId = pedido.getRestaurante().getId();
        this.itens = pedido.getItens().stream()
            .map(produto -> produto.getId())
            .collect(Collectors.toList());
    }
}