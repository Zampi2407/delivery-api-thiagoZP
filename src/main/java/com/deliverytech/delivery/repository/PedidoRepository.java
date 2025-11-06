package com.deliverytech.delivery.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery.entity.Pedido;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.dto.VendasRestauranteDTO;
import com.deliverytech.delivery.dto.PedidoResumoDTO;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // 🔹 Métodos derivados
    List<Pedido> findByClienteIdOrderByDataPedidoDesc(Long clienteId);

    List<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByStatusOrderByDataPedidoDesc(StatusPedido status);

    List<Pedido> findTop10ByOrderByDataPedidoDesc();

    List<Pedido> findByDataPedidoBetween(LocalDateTime inicio, LocalDateTime fim);

    // 🔹 Consultas com @Query

    @Query("SELECT r.nome AS nome, SUM(p.valorTotal) AS total FROM Pedido p JOIN p.restaurante r GROUP BY r.nome")
    List<VendasRestauranteDTO> gerarRelatorioVendas();

    @Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valorMinimo")
    List<Pedido> pedidosComValorAcima(@Param("valorMinimo") BigDecimal valorMinimo);

    @Query("SELECT p FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim AND p.status = :status")
    List<Pedido> relatorioPorPeriodoEStatus(@Param("inicio") LocalDateTime inicio,
                                            @Param("fim") LocalDateTime fim,
                                            @Param("status") StatusPedido status);

    @Query("SELECT p.numeroPedido AS numeroPedido, p.valorTotal AS valorTotal, p.status AS status FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim")
    List<PedidoResumoDTO> buscarPedidosPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                  @Param("fim") LocalDateTime fim);

    // 🔹 Consultas nativas (opcional)

    @Query(value = """
        SELECT produto_id, COUNT(*) AS quantidade
        FROM pedido_produto
        GROUP BY produto_id
        ORDER BY quantidade DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> produtosMaisVendidos();

    @Query(value = """
        SELECT cliente_id, COUNT(*) AS total_pedidos
        FROM pedidos
        GROUP BY cliente_id
        ORDER BY total_pedidos DESC
        """, nativeQuery = true)
    List<Object[]> rankingClientesPorPedidos();
}
