package com.deliverytech.delivery.services;

import com.deliverytech.delivery.dto.PedidoResumoDTO;
import com.deliverytech.delivery.dto.VendasRestauranteDTO;
import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.exceptions.BusinessException;
import com.deliverytech.delivery.repository.ClienteRepository;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
     * Criar novo pedido com validações e transação
     */
    @Transactional
    public Pedido criarPedido(PedidoDTO dto) {
        // 1. Validar cliente existe e está ativo
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new BusinessException("Cliente não encontrado: " + dto.getClienteId()));
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente inativo não pode fazer pedidos");
        }

        // 2. Validar restaurante existe e está ativo
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + dto.getRestauranteId()));
        if (!restaurante.getAtivo()) {
            throw new BusinessException("Restaurante não está disponível");
        }

        // 3. Validar todos os produtos existem, estão disponíveis e pertencem ao restaurante
        List<Long> itemIds;
        try {
            ObjectMapper mapper = new ObjectMapper();
            itemIds = mapper.readValue(dto.getItens(), new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new BusinessException("Formato inválido para itens: " + dto.getItens());
        }

        List<Produto> produtos = itemIds.stream()
            .map(id -> produtoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado: " + id)))
            .peek(produto -> {
                if (!produto.getDisponivel()) {
                    throw new BusinessException("Produto indisponível: " + produto.getNome());
                }
                if (!produto.getRestaurante().getId().equals(dto.getRestauranteId())) {
                    throw new BusinessException("Produto " + produto.getNome() + " não pertence ao restaurante selecionado");
                }
            })
            .collect(Collectors.toList());

        // 4. Calcular total do pedido
        BigDecimal totalProdutos = produtos.stream()
            .map(Produto::getPreco)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotal = totalProdutos.add(restaurante.getTaxaEntrega());

        // 5. Criar pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(StatusPedido.CONFIRMADO);
        pedido.setDataPedido(dto.getDataPedido());
        pedido.setNumeroPedido(dto.getNumeroPedido());
        pedido.setValorTotal(valorTotal);
        pedido.setObservacoes(dto.getObservacoes());
        pedido.setItens(produtos);

        // 6. Salvar pedido
        return pedidoRepository.save(pedido);
    }

    /**
     * Buscar pedido por ID
     */
    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Pedido não encontrado: " + id));
    }

    /**
     * Listar pedidos por cliente
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
    }

    /**
     * Atualizar status do pedido com validação de transição
     */
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
    Pedido pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(() -> new BusinessException("Pedido não encontrado: " + pedidoId));

    StatusPedido statusAtual = pedido.getStatus();

    if (!statusAtual.podeTransitarPara(novoStatus)) {
        throw new BusinessException("Transição de status não permitida: " +
            statusAtual.getDescricao() + " → " + novoStatus.getDescricao());
    }

    pedido.setStatus(novoStatus);
    return pedidoRepository.save(pedido);
}

    /**
     * Cancelar pedido
     */
    public void cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Pedido não encontrado: " + id));

        if (pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new BusinessException("Pedido já entregue não pode ser cancelado");
        }

        pedidoRepository.delete(pedido);
    }

    /**
     * Calcular total do pedido sem salvar
     */
    public BigDecimal calcularTotal(PedidoDTO dto) {
        List<Long> itemIds;
        try {
            ObjectMapper mapper = new ObjectMapper();
            itemIds = mapper.readValue(dto.getItens(), new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            throw new BusinessException("Formato inválido para itens: " + dto.getItens());
        }

        List<Produto> produtos = itemIds.stream()
            .map(id -> produtoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado: " + id)))
            .collect(Collectors.toList());

        return produtos.stream()
            .map(Produto::getPreco)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 🔹 Relatórios e consultas customizadas

    public List<VendasRestauranteDTO> gerarRelatorioVendas() {
        return pedidoRepository.gerarRelatorioVendas();
    }

    public List<Pedido> pedidosComValorAcima(BigDecimal valor) {
        return pedidoRepository.pedidosComValorAcima(valor);
    }

    public List<Pedido> relatorioPorPeriodoEStatus(LocalDateTime inicio, LocalDateTime fim, StatusPedido status) {
        return pedidoRepository.relatorioPorPeriodoEStatus(inicio, fim, status);
    }

    public List<PedidoResumoDTO> buscarPedidosPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return pedidoRepository.buscarPedidosPorPeriodo(inicio, fim);
    }

    public List<Object[]> produtosMaisVendidos() {
        return pedidoRepository.produtosMaisVendidos();
    }

    public List<Object[]> rankingClientesPorPedidos() {
        return pedidoRepository.rankingClientesPorPedidos();
    }
}