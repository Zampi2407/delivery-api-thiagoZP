package com.deliverytech.delivery.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.deliverytech.delivery.dto.ClienteResponseDTO;
import com.deliverytech.delivery.dto.ClienteResquetDTO;
import com.deliverytech.delivery.exceptions.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery.entity.Cliente;
import com.deliverytech.delivery.repository.ClienteRepository;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Cadastrar novo cliente
     */
    public ClienteResponseDTO cadastrar(ClienteResquetDTO dto) {
        validarDadosCliente(dto);

        if (clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());
        cliente.setAtivo(true);
        cliente.setDataCadastro(LocalDateTime.now());

        return new ClienteResponseDTO(clienteRepository.save(cliente));
    }

    /**
     * Buscar cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Buscar cliente por email
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Listar todos os clientes ativos
     */
    @Transactional(readOnly = true)
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    /**
     * Atualizar dados do cliente
     */
    public Cliente atualizar(Long id, ClienteResquetDTO dto) {
        validarDadosCliente(dto);

        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new BusinessException("Cliente não encontrado: " + id));

        if (!cliente.getEmail().equals(dto.getEmail()) &&
            clienteRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        cliente.setNome(dto.getNome());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefone(dto.getTelefone());
        cliente.setEndereco(dto.getEndereco());

        return clienteRepository.save(cliente);
    }

    /**
     * Alternar status ativo/inativo
     */
    public Cliente ativarDesativar(Long id) {
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new BusinessException("Cliente não encontrado: " + id));

        cliente.setAtivo(!cliente.getAtivo());
        return clienteRepository.save(cliente);
    }

    /**
     * Inativar cliente (soft delete)
     */
    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id)
            .orElseThrow(() -> new BusinessException("Cliente não encontrado: " + id));

        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    /**
     * Buscar clientes por nome
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Validações de negócio
     */
    private void validarDadosCliente(ClienteResquetDTO cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email é obrigatório");
        }

        if (cliente.getNome().length() < 2) {
            throw new BusinessException("Nome deve ter pelo menos 2 caracteres");
        }
    }
}
