package com.mktgus.autoatendimento.infra.data.external.microservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mktgus.autoatendimento.application.gateway.EmployeeGateway;
import com.mktgus.autoatendimento.application.gateway.EmployeeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@ConditionalOnProperty(name = "employee.provider", havingValue = "mercado", matchIfMissing = true)
public class MercadoEmployeeGateway implements EmployeeGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(MercadoEmployeeGateway.class);
    private static final String LOCAL_EMPLOYEE_URL = "http://localhost:9090/funcionarios/{registration}";
    private static final String DOCKER_HOST_EMPLOYEE_URL = "http://host.docker.internal:9090/funcionarios/{registration}";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean existsByRegistration(Long registration) {
        return findByRegistration(registration).isPresent();
    }

    @Override
    public Optional<EmployeeInfo> findByRegistration(Long registration) {
        ResponseEntity<String> response = fetchEmployee(registration);
        if (response == null) {
            return Optional.empty();
        }
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            String name = root.get("name").asText();
            return Optional.of(new EmployeeInfo(registration, name));
        } catch (Exception e) {
            LOGGER.warn("Erro ao parsear resposta do funcionario {}", registration, e);
            return Optional.empty();
        }
    }

    private ResponseEntity<String> fetchEmployee(Long registration) {
        try {
            LOGGER.info("Consultando funcionario {} em localhost:9090", registration);
            return restTemplate.getForEntity(
                    LOCAL_EMPLOYEE_URL, String.class, registration.toString()
            );
        } catch (ResourceAccessException exception) {
            LOGGER.info(
                    "Microservico nao acessivel via localhost. Tentando Docker host para funcionario {}",
                    registration
            );
            try {
                return restTemplate.getForEntity(
                        DOCKER_HOST_EMPLOYEE_URL, String.class, registration.toString()
                );
            } catch (RestClientException e) {
                LOGGER.warn("Funcionario {} nao encontrado em nenhum endpoint", registration, e);
                return null;
            }
        }
    }
}
