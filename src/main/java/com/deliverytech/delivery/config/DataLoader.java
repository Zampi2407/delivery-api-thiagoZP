package com.deliverytech.delivery.config;

import com.deliverytech.delivery.entity.*;
import com.deliverytech.delivery.enums.StatusPedido;
import com.deliverytech.delivery.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            ClienteRepository clienteRepo,
            RestauranteRepository restauranteRepo,
            ProdutoRepository produtoRepo,
            PedidoRepository pedidoRepo
    ) {
        return args -> {
            // 1️⃣ Criar clientes
            Cliente c1 = new Cliente("Thiago", "thiago@email.com", "11999999999", "Rua A", true);
            Cliente c2 = new Cliente("Ana", "ana@email.com", "11988888888", "Rua B", true);
            Cliente c3 = new Cliente("Carlos", "carlos@email.com", "11977777777", "Rua C", false);
            clienteRepo.saveAll(List.of(c1, c2, c3));

            // 2️⃣ Criar restaurantes
            Restaurante r1 = new Restaurante(null, "Pizza Place", "Italiana", "Rua X", "1111-1111", new BigDecimal("5.00"), new BigDecimal("4.5"), true, null);
            Restaurante r2 = new Restaurante(null, "Sushi House", "Japonesa", "Rua Y", "2222-2222", new BigDecimal("8.00"), new BigDecimal("4.8"), true, null);
            restauranteRepo.saveAll(List.of(r1, r2));

            // 3️⃣ Criar produtos
            Produto p1 = new Produto(null, "Pizza Margherita", "Clássica com queijo", new BigDecimal("30.00"), "Italiana", true, r1);
            Produto p2 = new Produto(null, "Pizza Calabresa", "Com cebola e calabresa", new BigDecimal("35.00"), "Italiana", true, r1);
            Produto p3 = new Produto(null, "Sushi Salmão", "8 peças", new BigDecimal("25.00"), "Japonesa", true, r2);
            Produto p4 = new Produto(null, "Temaki", "Cone de salmão", new BigDecimal("20.00"), "Japonesa", false, r2);
            Produto p5 = new Produto(null, "Guaraná", "350ml", new BigDecimal("6.00"), "Bebida", true, r1);
            produtoRepo.saveAll(List.of(p1, p2, p3, p4, p5));

            // 4️⃣ Criar pedidos
            Pedido pedido1 = new Pedido();
            pedido1.setNumeroPedido("PED001");
            pedido1.setDataPedido(LocalDateTime.now());
            pedido1.setStatus(StatusPedido.CONFIRMADO);
            pedido1.setValorTotal(p1.getPreco().add(p5.getPreco()));
            pedido1.setCliente(c1);
            pedido1.setRestaurante(r1);
            pedido1.setItens(List.of(p1, p5));
            pedido1.setObservacoes("Sem cebola");

            Pedido pedido2 = new Pedido();
            pedido2.setNumeroPedido("PED002");
            pedido2.setDataPedido(LocalDateTime.now().minusDays(1));
            pedido2.setStatus(StatusPedido.PREPARANDO);
            pedido2.setValorTotal(p3.getPreco());
            pedido2.setCliente(c2);
            pedido2.setRestaurante(r2);
            pedido2.setItens(List.of(p3));
            pedido2.setObservacoes("Com molho extra");

            pedidoRepo.saveAll(List.of(pedido1, pedido2));

            // 5️⃣ Testar consultas derivadas
            System.out.println("\n🔍 Clientes ativos:");
            clienteRepo.findByAtivoTrue().forEach(System.out::println);

            System.out.println("\n🔍 Restaurantes com taxa <= 6:");
            restauranteRepo.findByTaxaEntregaLessThanEqual(new BigDecimal("6.00")).forEach(System.out::println);

            System.out.println("\n🔍 Produtos disponíveis:");
            produtoRepo.findByDisponivelTrue().forEach(System.out::println);

            System.out.println("\n🔍 Pedidos do cliente Thiago:");
            pedidoRepo.findByClienteIdOrderByDataPedidoDesc(c1.getId()).forEach(System.out::println);

            System.out.println("\n✅ Dados de teste inseridos com sucesso!");
        };
    }
}
