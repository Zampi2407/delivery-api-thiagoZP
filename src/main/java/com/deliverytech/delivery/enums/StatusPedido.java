package com.deliverytech.delivery.enums;

public enum StatusPedido {
    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    PREPARANDO("Preparando"),
    SAIU_PARA_ENTREGA("Saiu para Entrega"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeTransitarPara(StatusPedido novoStatus) {
        return switch (this) {
            case PENDENTE -> novoStatus == CONFIRMADO || novoStatus == CANCELADO;
            case CONFIRMADO -> novoStatus == PREPARANDO || novoStatus == CANCELADO;
            case PREPARANDO -> novoStatus == SAIU_PARA_ENTREGA || novoStatus == CANCELADO;
            case SAIU_PARA_ENTREGA -> novoStatus == ENTREGUE;
            case ENTREGUE, CANCELADO -> false;
        };
    }
}