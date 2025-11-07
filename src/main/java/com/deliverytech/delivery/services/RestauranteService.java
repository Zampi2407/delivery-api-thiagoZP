package com.deliverytech.delivery.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery.entity.Restaurante;
import com.deliverytech.delivery.entity.RestauranteDTO;
import com.deliverytech.delivery.exceptions.BusinessException;
import com.deliverytech.delivery.repository.PedidoRepository;
import com.deliverytech.delivery.repository.ProdutoRepository;
import com.deliverytech.delivery.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Cadastrar novo restaurante
     */
    public Restaurante cadastrar(Restaurante restaurante) {
        if (restauranteRepository.findByNome(restaurante.getNome()).isPresent()) {
            throw new BusinessException("Restaurante já cadastrado: " + restaurante.getNome());
        }

        validarDadosRestaurante(restaurante);
        restaurante.setAtivo(true);

        return restauranteRepository.save(restaurante);
    }

    /**
     * Buscar por ID (entidade)
     */
    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(Long id) {
        return restauranteRepository.findById(id);
    }

    /**
     * Buscar por ID (DTO)
     */
    @Transactional(readOnly = true)
    public Optional<RestauranteDTO> findById(Long id) {
        return restauranteRepository.findById(id)
            .map(restaurante -> new RestauranteDTO(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getCategoria(),
                restaurante.getEndereco(),
                restaurante.getTelefone(),
                restaurante.getTaxaEntrega(),
                restaurante.getAvaliacao(),
                restaurante.getAtivo()
            ));
    }

    /**
     * Listar restaurantes ativos
     */
    @Transactional(readOnly = true)
    public List<RestauranteDTO> listarAtivos() {
        List<Restaurante> ativos = restauranteRepository.findByAtivoTrue();
        if (ativos.isEmpty()) {
            throw new BusinessException("Nenhum restaurante ativo encontrado");
        }

        return ativos.stream()
            .map(r -> new RestauranteDTO(
                r.getId(), r.getNome(), r.getCategoria(), r.getEndereco(),
                r.getTelefone(), r.getTaxaEntrega(), r.getAvaliacao(), r.getAtivo()))
            .toList();
    }

    /**
     * Buscar por categoria
     */
    @Transactional(readOnly = true)
    public List<RestauranteDTO> buscarPorCategoria(String categoria) {
        List<Restaurante> encontrados = restauranteRepository.findByCategoria(categoria);
        if (encontrados.isEmpty()) {
            throw new BusinessException("Nenhum restaurante encontrado para a categoria: " + categoria);
        }

        return encontrados.stream()
            .map(r -> new RestauranteDTO(
                r.getId(), r.getNome(), r.getCategoria(), r.getEndereco(),
                r.getTelefone(), r.getTaxaEntrega(), r.getAvaliacao(), r.getAtivo()))
            .toList();
    }

    /**
     * Atualizar restaurante
     */
    public Restaurante atualizar(Long id, Restaurante restauranteAtualizado) {
        Restaurante restaurante = buscarPorId(id)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + id));

        if (!restaurante.getNome().equals(restauranteAtualizado.getNome()) &&
            restauranteRepository.findByNome(restauranteAtualizado.getNome()).isPresent()) {
            throw new BusinessException("Nome já cadastrado: " + restauranteAtualizado.getNome());
        }

        restaurante.setNome(restauranteAtualizado.getNome());
        restaurante.setCategoria(restauranteAtualizado.getCategoria());
        restaurante.setEndereco(restauranteAtualizado.getEndereco());
        restaurante.setTelefone(restauranteAtualizado.getTelefone());
        restaurante.setTaxaEntrega(restauranteAtualizado.getTaxaEntrega());

        return restauranteRepository.save(restaurante);
    }

    /**
     * Inativar restaurante
     */
    public void inativar(Long id) {
        Restaurante restaurante = buscarPorId(id)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + id));

        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);
    }

    /**
     * Deletar restaurante
     */
    public void deletar(Long id) {
        Restaurante restaurante = buscarPorId(id)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + id));

        restauranteRepository.delete(restaurante);
    }

    /**
     * Calcular taxa de entrega com base no CEP
     */
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = buscarPorId(restauranteId)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + restauranteId));

        BigDecimal taxaBase = restaurante.getTaxaEntrega();

        if (cep.startsWith("01")) {
            return taxaBase.add(BigDecimal.valueOf(5));
        } else if (cep.startsWith("02")) {
            return taxaBase.add(BigDecimal.valueOf(8));
        } else {
            return taxaBase.add(BigDecimal.valueOf(10));
        }
    }

    /**
     * Validações de negócio
     */
    private void validarDadosRestaurante(Restaurante restaurante) {
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }

        if (restaurante.getCategoria() == null || restaurante.getCategoria().trim().isEmpty()) {
            throw new BusinessException("Categoria é obrigatória");
        }

        if (restaurante.getTaxaEntrega() != null &&
            restaurante.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Taxa de entrega não pode ser negativa");
        }
    }
}