package com.mktgus.autoatendimento.web.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mktgus.autoatendimento.application.cart.CartSnapshot;
import com.mktgus.autoatendimento.application.usecase.RecoverCartUseCase;
import com.mktgus.autoatendimento.application.usecase.SaveCartUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@ContextConfiguration(classes = CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SaveCartUseCase saveCartUseCase;

    @MockBean
    private RecoverCartUseCase recoverCartUseCase;

    @Test
    void shouldSaveCartWithValidPayload() throws Exception {
        String body = objectMapper.writeValueAsString(new SaveCartRequestFixture(
                "52998224725",
                List.of(new CartItemRequestFixture("9999999999999", "Produto Homologacao Checkout", 19.9, 1, false))
        ));

        mockMvc.perform(post("/api/carrinho/salvar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(saveCartUseCase).execute(any());
    }

    @Test
    void shouldRecoverSavedCart() throws Exception {
        CartSnapshot snapshot = new CartSnapshot(
                "52998224725",
                List.of(new CartSnapshot.CartItemSnapshot("9999999999999", "Produto Homologacao Checkout", 19.9, 1, false)),
                LocalDateTime.of(2026, 5, 2, 18, 0)
        );
        when(recoverCartUseCase.execute("52998224725")).thenReturn(Optional.of(snapshot));

        mockMvc.perform(get("/api/carrinho/recuperar").param("cpf", "52998224725"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.items[0].ean").value("9999999999999"));
    }

    private record SaveCartRequestFixture(String cpf, List<CartItemRequestFixture> items) {
    }

    private record CartItemRequestFixture(String ean, String productName, double unitPrice, int quantity,
                                          boolean adultOnly) {
    }
}
