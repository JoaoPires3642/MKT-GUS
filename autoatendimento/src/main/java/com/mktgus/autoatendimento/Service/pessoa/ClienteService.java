package com.mktgus.autoatendimento.Service.pessoa;

import com.mktgus.autoatendimento.Model.pessoa.Cliente;
import com.mktgus.autoatendimento.repository.cliente.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Verifica se o CPF existe e retorna o cliente
    public Optional<Cliente> verificarCpf(Long cpf) {
        return clienteRepository.findById(cpf); // Alterado de findByCpf para findById
    }

    public boolean atualizarPontos(String cpf, int pointsBalance) {
        Optional<Cliente> optionalCliente = clienteRepository.findByCpf(Long.valueOf(cpf));
        if (optionalCliente.isPresent()) {
            Cliente cliente = optionalCliente.get();
            cliente.setPontos(pointsBalance); // Define o novo saldo diretamente
            clienteRepository.save(cliente);
            System.out.println("Pontos atualizados para CPF " + cpf + ": " + pointsBalance);
            return true;
        }
        System.out.println("Cliente não encontrado para CPF: " + cpf);
        return false;
    }

    // Adiciona pontos ao cliente (caso use futuramente)
    public void adicionarPontos(Long cpf, int pontos) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(cpf); // Alterado de findByCpf para findById
        optionalCliente.ifPresent(cliente -> {
            cliente.setPontos(cliente.getPontos() + pontos);
            clienteRepository.save(cliente);
        });
    }
}