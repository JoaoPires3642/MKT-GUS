package com.mktgus.autoatendimento.application.request;

import com.fasterxml.jackson.annotation.JsonAlias;

public record CustomerPointsRequest(
        String cpf,
        @JsonAlias({"pontosNecessarios", "pointsBalance"})
        int requiredPoints
) {}
