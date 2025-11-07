package com.deliverytech.delivery.dto;

import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.enums.StatusPedido;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PedidoResumoDTO {

    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private StatusPedido status;
    private BigDecimal valorTotal;

    public PedidoResumoDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.numeroPedido = pedido.getNumeroPedido();
        this.dataPedido = pedido.getDataPedido();
        this.status = pedido.getStatus();
        this.valorTotal = pedido.getValorTotal();
    }
}