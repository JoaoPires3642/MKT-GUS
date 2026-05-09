package com.mktgus.autoatendimento.application.response;

import jakarta.validation.constraints.NotBlank;

public record ProductResponse(
        @NotBlank(message = "O codigo nao pode ser Nulo")
        String ean,

        @NotBlank(message = "The product name cannot be null")
        String name,

        String imageUrl,

        double price,

        boolean adultOnly,

        String description
) {
}
