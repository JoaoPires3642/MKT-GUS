package com.mktgus.autoatendimento.web.ui.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mktgus.autoatendimento.application.mapper.ProductApiMapper;
import com.mktgus.autoatendimento.application.response.ProductResponse;
import com.mktgus.autoatendimento.application.usecase.FindProductByBarcodeUseCase;
import com.mktgus.autoatendimento.application.usecase.ListScannedProductsUseCase;
import com.mktgus.autoatendimento.application.usecase.ProcessBarcodeScanUseCase;
import com.mktgus.autoatendimento.application.usecase.RegisterScannedProductUseCase;
import com.mktgus.autoatendimento.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductApiMapper productApiMapper;

    @MockBean
    private FindProductByBarcodeUseCase findProductByBarcodeUseCase;

    @MockBean
    private ProcessBarcodeScanUseCase processBarcodeScanUseCase;

    @MockBean
    private ListScannedProductsUseCase listScannedProductsUseCase;

    @MockBean
    private RegisterScannedProductUseCase registerScannedProductUseCase;

    @Test
    void shouldFindProductByBarcodeUsingGetRoute() throws Exception {
        Product product = new Product("9999999999999", "Produto Homologacao Checkout", null, 19.9, false, null);
        ProductResponse response = new ProductResponse("9999999999999", "Produto Homologacao Checkout", null, 19.9, false, null);
        when(findProductByBarcodeUseCase.execute(any())).thenReturn(product);
        when(productApiMapper.toResponse(product)).thenReturn(response);

        mockMvc.perform(get("/produtos/buscar/9999999999999").header("X-Session-Id", "sessao-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ean").value("9999999999999"))
                .andExpect(jsonPath("$.name").value("Produto Homologacao Checkout"));

        verify(registerScannedProductUseCase).execute(eq("sessao-1"), eq(product));
    }

    @Test
    void shouldFindProductByBarcodeUsingPostRoute() throws Exception {
        Product product = new Product("9999999999999", "Produto Homologacao Checkout", null, 19.9, false, null);
        ProductResponse response = new ProductResponse("9999999999999", "Produto Homologacao Checkout", null, 19.9, false, null);
        when(processBarcodeScanUseCase.execute(any())).thenReturn(product);
        when(productApiMapper.toResponse(product)).thenReturn(response);

        String body = objectMapper.writeValueAsString(new BarcodeRequestFixture("9999999999999"));

        mockMvc.perform(post("/produtos/buscar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ean").value("9999999999999"))
                .andExpect(jsonPath("$.price").value(19.9));
    }

    private record BarcodeRequestFixture(String barcode) {
    }
}
