package com.mktgus.autoatendimento.controller.pessoa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mktgus.autoatendimento.Model.pessoa.Cliente;
import com.mktgus.autoatendimento.repository.cliente.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pessoa")
@CrossOrigin(origins = "http://localhost:3000")  // Altere para a URL correta do seu frontend
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/verificar-cpf")
    public ResponseEntity<?> verificarCpf(@RequestBody Map<String, String> requestBody) {
        String cpfStr = requestBody.get("cpf");
        try {
            Long cpf = Long.parseLong(cpfStr.replaceAll("\\D", ""));

            Cliente cliente = clienteRepository.findById(cpf).orElseGet(() -> createNewClient(cpf));
            cliente.setCpf(cpf);

            return ResponseEntity.ok(cliente.getPontos());

        } catch (NumberFormatException e) {
            logger.error("Erro ao processar CPF: " + cpfStr, e);
            return ResponseEntity.badRequest().body("CPF inválido.");
        } catch (Exception e) {
            logger.error("Erro interno ao verificar CPF: " + cpfStr, e);
            return ResponseEntity.status(500).body("Erro interno do servidor.");
        }
    }

    private Cliente createNewClient(Long cpf) {
        Cliente novoCliente = new Cliente();
        novoCliente.setCpf(cpf);
        novoCliente.setPontos(0);  // Aqui você pode parametrizar esse valor
        clienteRepository.save(novoCliente);
        return novoCliente;
    }
}