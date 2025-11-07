package com.deliverytech.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery.entity.Produto;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar produto por restaurante ID
    List<Produto> findByRestauranteId(Long restauranteId);

    // Buscar produtos disponíveis
    List<Produto> findByDisponivelTrue();

    // Buscar produtos por categoria
    List<Produto> findByCategoria(String categoria);

    // Buscar produtos com preço menor ou igual ao valor informado
    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);

    List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId);
    
    List<Produto> findByCategoriaIgnoreCase(String categoria);
}