package com.mktgus.autoatendimento.web.ui.controller;

import com.mktgus.autoatendimento.application.gateway.EmployeeInfo;
import com.mktgus.autoatendimento.application.usecase.VerifyEmployeeRegistrationUseCase;
import com.mktgus.autoatendimento.application.verification.VerifyEmployeeRegistrationInput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

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
    @PostMapping("/verificar-matricula")
    public ResponseEntity<RegistrationResponse> verificarMatricula(@RequestBody Map<String, String> request) {
        String registration = request.getOrDefault("registration", request.get("matricula"));
        Optional<EmployeeInfo> employeeInfo = verifyEmployeeRegistrationUseCase.execute(
                new VerifyEmployeeRegistrationInput(registration)
        );
        if (employeeInfo.isPresent()) {
            return ResponseEntity.ok(new RegistrationResponse(
                    true, "Employee found.", employeeInfo.get().name()
            ));
        }
        return ResponseEntity.badRequest().body(new RegistrationResponse(
                false, "Invalid registration.", null
        ));
    }

    @Schema(name = "RegistrationResponse", description = "Resposta da verificação de matrícula")
    public record RegistrationResponse(boolean valid, String message, String name) {
    }
}
