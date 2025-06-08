package com.mktgus.autoatendimento.controller.pontos;

import com.mktgus.autoatendimento.Service.pessoa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pontos")
@CrossOrigin(origins = "http://localhost:3000")
public class PontosController {

    @Autowired
    private ClienteService clienteService;

    // Classe para respostas JSON
    private static class ResponseMessage {
        private String message;

        public ResponseMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @PostMapping("/finalizar-compra")
    public ResponseEntity<?> finalizarCompra(@RequestBody PontosRequest request) {
        try {
            Long cpf = Long.parseLong(request.cpf());
            int pontosNecessarios = request.pontosNecessarios();
            System.out.println("Processando CPF: " + cpf + ", Pontos: " + pontosNecessarios);

            // Validar pontos negativos
            if (pontosNecessarios < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseMessage("Saldo de pontos não pode ser negativo."));
            }

            boolean sucesso = clienteService.atualizarPontos(String.valueOf(cpf), pontosNecessarios);

            if (sucesso) {
                return ResponseEntity.ok(new ResponseMessage("Compra finalizada com sucesso!"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseMessage("Pontos insuficientes ou CPF não encontrado."));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Erro de validação: CPF ou pontos inválidos."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseMessage("Erro de validação: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseMessage("Erro ao finalizar a compra: " + e.getMessage()));
        }
    }
}

// DTO para a requisição
record PontosRequest(String cpf, int pontosNecessarios) {}