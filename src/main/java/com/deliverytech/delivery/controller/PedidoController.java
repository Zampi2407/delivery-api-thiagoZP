package com.deliverytech.delivery.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.deliverytech.delivery.dto.PedidoResumoDTO;
import com.deliverytech.delivery.dto.VendasRestauranteDTO;
import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.entity.PedidoDTO;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.services.PedidoService;
import com.deliverytech.delivery.repository.PedidoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Criar novo pedido
     */
    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoDTO dto) {
        try {
            Pedido pedido = pedidoService.criarPedido(dto);
            return ResponseEntity.ok(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }

    /**
     * Listar pedidos por cliente
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> listarPorCliente(@PathVariable Long clienteId) {
        List<Pedido> pedidos = pedidoService.listarPorCliente(clienteId);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Atualizar status do pedido
     */
    @PutMapping("/{pedidoId}/{status}")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long pedidoId,
                                             @PathVariable StatusPedido status) {
        try {
            Pedido pedido = pedidoService.atualizarStatus(pedidoId, status);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }

    // 🔹 Consultas customizadas e relatórios

    @GetMapping("/relatorio-vendas")
    public ResponseEntity<List<VendasRestauranteDTO>> relatorioVendas() {
        return ResponseEntity.ok(pedidoRepository.gerarRelatorioVendas());
    }

    @GetMapping("/acima-de/{valor}")
    public ResponseEntity<List<Pedido>> pedidosAcimaDe(@PathVariable BigDecimal valor) {
        return ResponseEntity.ok(pedidoRepository.pedidosComValorAcima(valor));
    }

    @GetMapping("/por-periodo-e-status")
    public ResponseEntity<List<Pedido>> pedidosPorPeriodoEStatus(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fim") LocalDateTime fim,
            @RequestParam("status") StatusPedido status) {
        return ResponseEntity.ok(pedidoRepository.relatorioPorPeriodoEStatus(inicio, fim, status));
    }

    @GetMapping("/resumo-por-periodo")
    public ResponseEntity<List<PedidoResumoDTO>> resumoPorPeriodo(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fim") LocalDateTime fim) {
        return ResponseEntity.ok(pedidoRepository.buscarPedidosPorPeriodo(inicio, fim));
    }

    @GetMapping("/produtos-mais-vendidos")
    public ResponseEntity<List<Object[]>> produtosMaisVendidos() {
        return ResponseEntity.ok(pedidoRepository.produtosMaisVendidos());
    }

    @GetMapping("/ranking-clientes")
    public ResponseEntity<List<Object[]>> rankingClientes() {
        return ResponseEntity.ok(pedidoRepository.rankingClientesPorPedidos());
    }
}
