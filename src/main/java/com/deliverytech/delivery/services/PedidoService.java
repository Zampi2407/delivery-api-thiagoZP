package com.deliverytech.delivery.services;

import java.util.List;
import java.util.stream.Collectors;

import com.deliverytech.delivery.entity.PedidoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Criar novo pedido
     */
    public Pedido criarPedido(PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + dto.getClienteId()));

        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + dto.getRestauranteId()));

        if (!cliente.getAtivo()) {
            throw new IllegalArgumentException("Cliente inativo não pode fazer pedidos");
        }

        if (!restaurante.getAtivo()) {
            throw new IllegalArgumentException("Restaurante não está disponível");
        }
ObjectMapper mapper = new ObjectMapper();
List<Long> itemIds;

try {
    itemIds = mapper.readValue(dto.getItens(), new TypeReference<List<Long>>() {});
} catch (Exception e) {
    throw new IllegalArgumentException("Formato inválido para itens: " + dto.getItens());
}

List<Produto> produtos = itemIds.stream()
    .map(id -> produtoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id)))
    .toList();


Pedido pedido = new Pedido();

pedido.setCliente(cliente);
pedido.setRestaurante(restaurante);
pedido.setStatus(StatusPedido.PENDENTE);
pedido.setDataPedido(dto.getDataPedido());
pedido.setNumeroPedido(dto.getNumeroPedido());
pedido.setValorTotal(dto.getValorTotal());
pedido.setObservacoes(dto.getObservacoes());
pedido.setItens(produtos);


        return pedidoRepository.save(pedido);
    }

    /**
     * Listar pedidos por cliente
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
    }

    /**
     * Atualizar status do pedido
     */
    public Pedido atualizarStatus(Long pedidoId, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + pedidoId));

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new IllegalArgumentException("Pedido já finalizado: " + pedidoId);
        }

        pedido.setStatus(status);
        return pedidoRepository.save(pedido);
    }
}
