package com.mktgus.autoatendimento.controller.pessoa;


import com.mktgus.autoatendimento.Model.pessoa.*;
import com.mktgus.autoatendimento.repository.funcionario.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "http://localhost:3000")
public class FuncionarioController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping("/verificar-matricula")
    public ResponseEntity<?> verificarMatricula(@RequestBody MatriculaRequest request) {
        try {
            boolean exists = funcionarioRepository.existsByMatricula(Long.parseLong(request.getMatricula()));
            if (exists) {
                return ResponseEntity.ok().body(new MatriculaResponse(true, "Funcionário encontrado."));
            } else {
                return ResponseEntity.badRequest().body(new MatriculaResponse(false, "Matrícula inválida."));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new MatriculaResponse(false, "Matrícula inválida."));
        }
    }
}

// Classe auxiliar para a requisição
class MatriculaRequest {
    private String matricula;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}

// Classe auxiliar para a resposta
class MatriculaResponse {
    private boolean valid;
    private String message;

    public MatriculaResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}