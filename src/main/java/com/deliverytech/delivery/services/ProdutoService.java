package com.deliverytech.delivery.services;

import java.math.BigDecimal;
import java.util.List;

import com.deliverytech.delivery.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery.entity.Produto;
import com.deliverytech.delivery.entity.ProdutoDTO;
import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    /**
     * Cadastrar novo produto com validação de restaurante
     */
    public ProdutoDTO cadastrarProduto(ProdutoDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + dto.getRestauranteId()));

        validarDadosProduto(dto);

        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);

        Produto salvo = produtoRepository.save(produto);
        return toDTO(salvo);
    }

    /**
     * Listar todos os produtos
     */
    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    /**
     * Buscar produtos disponíveis por restaurante
     */
    public List<ProdutoDTO> buscarProdutosPorRestaurante(Long restauranteId) {
        return produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId).stream()
            .map(this::toDTO)
            .toList();
    }

    /**
     * Excluir produto
     */
    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Produto não encontrado: " + id));
        produtoRepository.delete(produto);
    }

    /**
     * Buscar produto por ID com validação de disponibilidade
     */
    public ProdutoDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Produto não encontrado: " + id));

        if (!produto.getDisponivel()) {
            throw new BusinessException("Produto está inativo: " + id);
        }

        return toDTO(produto);
    }

    /**
     * Atualizar produto com validações
     */
    @Transactional
    public ProdutoDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Produto não encontrado: " + id));

        validarDadosProduto(dto);

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        produto.setDisponivel(dto.getDisponivel());

        if (dto.getRestauranteId() != null && !dto.getRestauranteId().equals(produto.getRestaurante().getId())) {
            Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + dto.getRestauranteId()));
            produto.setRestaurante(restaurante);
        }

        Produto atualizado = produtoRepository.save(produto);
        return toDTO(atualizado);
    }

    /**
     * Alterar disponibilidade do produto
     */
    public ProdutoDTO alterarDisponibilidade(Long id, boolean disponivel) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Produto não encontrado: " + id));

        produto.setDisponivel(disponivel);
        Produto atualizado = produtoRepository.save(produto);

        return toDTO(atualizado);
    }

    /**
     * Buscar produtos por categoria
     */
    public List<ProdutoDTO> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoriaIgnoreCase(categoria).stream()
            .map(this::toDTO)
            .toList();
    }

    /**
     * Validações de negócio
     */
    private void validarDadosProduto(ProdutoDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new BusinessException("Nome do produto é obrigatório");
        }
        if (dto.getDescricao() == null || dto.getDescricao().isBlank()) {
            throw new BusinessException("Descrição do produto é obrigatória");
        }
        if (dto.getPreco() == null || dto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }
        if (dto.getCategoria() == null || dto.getCategoria().isBlank()) {
            throw new BusinessException("Categoria é obrigatória");
        }
    }

    /**
     * Conversão para DTO
     */
    private ProdutoDTO toDTO(Produto produto) {
        return new ProdutoDTO(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getPreco(),
            produto.getCategoria(),
            produto.getDisponivel(),
            produto.getRestaurante().getId()
        );
    }
}