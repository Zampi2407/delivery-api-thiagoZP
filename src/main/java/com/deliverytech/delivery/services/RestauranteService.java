package com.deliverytech.delivery.services;

import java.math.BigDecimal;
import java.util.List;

import com.deliverytech.delivery.dto.RestauranteRequestDTO;
import com.deliverytech.delivery.dto.RestauranteResponseDTO;
import com.deliverytech.delivery.entity.Restaurante;
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
    public RestauranteResponseDTO cadastrar(RestauranteRequestDTO dto) {
        if (restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new BusinessException("Restaurante já cadastrado: " + dto.getNome());
        }

        validarDadosRestaurante(dto);

        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());
        restaurante.setAtivo(true);

        return new RestauranteResponseDTO(restauranteRepository.save(restaurante));
    }

    /**
     * Buscar por ID
     */
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarPorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + id));
        return new RestauranteResponseDTO(restaurante);
    }

    /**
     * Listar restaurantes ativos
     */
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> listarDisponiveis() {
        return restauranteRepository.findByAtivoTrue().stream()
            .map(RestauranteResponseDTO::new)
            .toList();
    }

    /**
     * Buscar por categoria
     */
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarPorCategoria(String categoria) {
        return restauranteRepository.findByCategoria(categoria).stream()
            .map(RestauranteResponseDTO::new)
            .toList();
    }

    /**
     * Atualizar restaurante
     */
    public RestauranteResponseDTO atualizar(Long id, RestauranteRequestDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + id));

        if (!restaurante.getNome().equals(dto.getNome()) &&
            restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new BusinessException("Nome já cadastrado: " + dto.getNome());
        }

        validarDadosRestaurante(dto);

        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());

        return new RestauranteResponseDTO(restauranteRepository.save(restaurante));
    }

    /**
     * Inativar restaurante
     */
    public void inativar(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + id));

        restaurante.setAtivo(false);
        restauranteRepository.save(restaurante);
    }

    /**
     * Deletar restaurante
     */
    public void deletar(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + id));

        restauranteRepository.delete(restaurante);
    }

    /**
     * Calcular taxa de entrega com base no CEP
     */
    public BigDecimal calcularTaxaEntrega(Long restauranteId, String cep) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
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
    private void validarDadosRestaurante(RestauranteRequestDTO dto) {
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }

        if (dto.getCategoria() == null || dto.getCategoria().trim().isEmpty()) {
            throw new BusinessException("Categoria é obrigatória");
        }

        if (dto.getTaxaEntrega() != null &&
            dto.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Taxa de entrega não pode ser negativa");
        }
    }
}