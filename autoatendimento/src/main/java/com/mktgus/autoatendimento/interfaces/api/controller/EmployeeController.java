package com.mktgus.autoatendimento.interfaces.api.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.mktgus.autoatendimento.application.model.VerifyEmployeeRegistrationInput;
import com.mktgus.autoatendimento.application.usecase.VerifyEmployeeRegistrationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Employee", description = "Operações relacionadas a funcionários")
public class EmployeeController {
    private final VerifyEmployeeRegistrationUseCase verifyEmployeeRegistrationUseCase;

    public EmployeeController(VerifyEmployeeRegistrationUseCase verifyEmployeeRegistrationUseCase) {
        this.verifyEmployeeRegistrationUseCase = verifyEmployeeRegistrationUseCase;
    }

    @Operation(summary = "Verificar matrícula de funcionário")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "400", description = "Matrícula inválida",
            content = @Content(schema = @Schema(ref = "#/components/schemas/ApiMessage")))
    @PostMapping("/verificar-matricula")
    public ResponseEntity<RegistrationResponse> verificarMatricula(@RequestBody Map<String, String> request) {
        String registration = request.getOrDefault("registration", request.get("matricula"));
        boolean exists = verifyEmployeeRegistrationUseCase.execute(new VerifyEmployeeRegistrationInput(registration));
        if (exists) {
            return ResponseEntity.ok(new RegistrationResponse(true, "Employee found."));
        }

        return ResponseEntity.badRequest().body(new RegistrationResponse(false, "Invalid registration."));
    }

    @Schema(name = "RegistrationResponse", description = "Resposta da verificação de matrícula")
    public record RegistrationResponse(boolean valid, String message) {
    }
}
