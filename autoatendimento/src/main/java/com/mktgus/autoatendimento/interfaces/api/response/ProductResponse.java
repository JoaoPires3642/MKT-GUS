package com.mktgus.autoatendimento.interfaces.api.response;


import jakarta.validation.constraints.NotBlank;



public record ProductResponse(

        @NotBlank(message = "O código não pode ser Nulo")
        String ean,

        @NotBlank(message = "The product name cannot be null")
        String name,

        String imageUrl,

        double price,

        boolean adultOnly
) {

}
