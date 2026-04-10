package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.model.VerifyEmployeeRegistrationInput;
import com.mktgus.autoatendimento.application.usecase.VerifyEmployeeRegistrationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeController {
    private final VerifyEmployeeRegistrationUseCase verifyEmployeeRegistrationUseCase;

    public EmployeeController(VerifyEmployeeRegistrationUseCase verifyEmployeeRegistrationUseCase) {
        this.verifyEmployeeRegistrationUseCase = verifyEmployeeRegistrationUseCase;
    }

    @PostMapping("/verificar-matricula")
    public ResponseEntity<RegistrationResponse> verificarMatricula(@RequestBody RegistrationRequest request) {
        boolean exists = verifyEmployeeRegistrationUseCase.execute(new VerifyEmployeeRegistrationInput(request.registration()));
        if (exists) {
            return ResponseEntity.ok(new RegistrationResponse(true, "Employee found."));
        }

        return ResponseEntity.badRequest().body(new RegistrationResponse(false, "Invalid registration."));
    }

    public record RegistrationRequest(String registration) {
    }

    public record RegistrationResponse(boolean valid, String message) {
    }
}
