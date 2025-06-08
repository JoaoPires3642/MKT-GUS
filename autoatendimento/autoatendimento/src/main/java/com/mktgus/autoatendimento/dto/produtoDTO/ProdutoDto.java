package com.mktgus.autoatendimento.dto.produtoDTO;


import jakarta.validation.constraints.NotBlank;



public record ProdutoDto(

        @NotBlank(message = "O código não pode ser Nulo")
        String ean,

        @NotBlank(message = "O nome não pode ser Nulo")
        String nome,

        String urlImagem,

        double valor,

        @NotBlank
        boolean produtoMaiorDeIdade
) {

}