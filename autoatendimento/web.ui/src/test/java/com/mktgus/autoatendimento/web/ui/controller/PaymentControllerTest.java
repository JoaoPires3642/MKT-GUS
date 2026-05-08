package com.mktgus.autoatendimento.web.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mktgus.autoatendimento.application.mapper.PaymentApiMapper;
import com.mktgus.autoatendimento.application.payment.PaymentStatusOutput;
import com.mktgus.autoatendimento.application.payment.StartPaymentInput;
import com.mktgus.autoatendimento.application.payment.StartPaymentOutput;
import com.mktgus.autoatendimento.application.request.StartPaymentRequest;
import com.mktgus.autoatendimento.application.response.PaymentResponse;
import com.mktgus.autoatendimento.application.usecase.ConfirmPaymentUseCase;
import com.mktgus.autoatendimento.application.usecase.GetPaymentStatusUseCase;
import com.mktgus.autoatendimento.application.usecase.StartPaymentUseCase;
import com.mktgus.autoatendimento.domain.model.PaymentMethod;
import com.mktgus.autoatendimento.domain.model.PaymentStatus;
import com.mktgus.autoatendimento.domain.model.PaymentTransaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@ContextConfiguration(classes = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentApiMapper paymentApiMapper;

    @MockBean
    private StartPaymentUseCase startPaymentUseCase;

    @MockBean
    private GetPaymentStatusUseCase getPaymentStatusUseCase;

    @MockBean
    private ConfirmPaymentUseCase confirmPaymentUseCase;

    @Test
    void shouldStartPayment() throws Exception {
        StartPaymentRequest request = new StartPaymentRequest(PaymentMethod.PIX, 19.9);
        StartPaymentInput input = new StartPaymentInput(PaymentMethod.PIX, 19.9);
        StartPaymentOutput output = new StartPaymentOutput(transaction(PaymentStatus.PROCESSING));
        PaymentResponse response = response(PaymentStatus.PROCESSING);

        when(paymentApiMapper.toInput(any(StartPaymentRequest.class))).thenReturn(input);
        when(startPaymentUseCase.execute(input)).thenReturn(output);
        when(paymentApiMapper.toResponse(output)).thenReturn(response);

        mockMvc.perform(post("/pagamentos/iniciar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PROCESSING"))
                .andExpect(jsonPath("$.method").value("PIX"));
    }

    @Test
    void shouldGetPaymentStatus() throws Exception {
        PaymentStatusOutput output = new PaymentStatusOutput(transaction(PaymentStatus.PAID));
        PaymentResponse response = response(PaymentStatus.PAID);

        when(getPaymentStatusUseCase.execute(1L)).thenReturn(output);
        when(paymentApiMapper.toResponse(output)).thenReturn(response);

        mockMvc.perform(get("/pagamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void shouldConfirmPayment() throws Exception {
        PaymentStatusOutput output = new PaymentStatusOutput(transaction(PaymentStatus.PAID));
        PaymentResponse response = response(PaymentStatus.PAID);

        when(confirmPaymentUseCase.execute(eq(1L))).thenReturn(output);
        when(paymentApiMapper.toResponse(output)).thenReturn(response);

        mockMvc.perform(post("/pagamentos/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    private static PaymentTransaction transaction(PaymentStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentTransaction(1L, "fake", "fake-ref", PaymentMethod.PIX, status, 19.9, null, now.plusMinutes(15), status == PaymentStatus.PAID ? now : null, now, now, null);
    }

    private static PaymentResponse response(PaymentStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentResponse(1L, "fake", "fake-ref", PaymentMethod.PIX, status, 19.9, null, now.plusMinutes(15), status == PaymentStatus.PAID ? now : null);
    }
}
