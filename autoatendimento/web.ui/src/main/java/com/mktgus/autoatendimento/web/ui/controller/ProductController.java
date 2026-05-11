package com.mktgus.autoatendimento.web.ui.controller;

import com.mktgus.autoatendimento.application.mapper.ProductApiMapper;
import com.mktgus.autoatendimento.application.product.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.response.ProductResponse;
import com.mktgus.autoatendimento.application.usecase.FindProductByBarcodeUseCase;
import com.mktgus.autoatendimento.application.usecase.ListScannedProductsUseCase;
import com.mktgus.autoatendimento.application.usecase.RegisterScannedProductUseCase;
import com.mktgus.autoatendimento.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Product", description = "Operações relacionadas a produtos")
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductApiMapper productApiMapper;
    private final FindProductByBarcodeUseCase findProductByBarcodeUseCase;
    private final ListScannedProductsUseCase listScannedProductsUseCase;
    private final RegisterScannedProductUseCase registerScannedProductUseCase;

    public ProductController(
            ProductApiMapper productApiMapper,
            FindProductByBarcodeUseCase findProductByBarcodeUseCase,
            ListScannedProductsUseCase listScannedProductsUseCase,
            RegisterScannedProductUseCase registerScannedProductUseCase
    ) {
        this.productApiMapper = productApiMapper;
        this.findProductByBarcodeUseCase = findProductByBarcodeUseCase;
        this.listScannedProductsUseCase = listScannedProductsUseCase;
        this.registerScannedProductUseCase = registerScannedProductUseCase;
    }

    @Operation(summary = "Listar produtos escaneados na sessão atual")
    @GetMapping("/listar")
    public List<ProductResponse> listarProdutos(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId
    ) {
        return listScannedProductsUseCase.execute(sessionId).stream().map(productApiMapper::toResponse).toList();
    }

    @Operation(summary = "Buscar produto por código de barras (GET)")
    @GetMapping("/buscar/{barcode}")
    public ResponseEntity<ProductResponse> buscarProdutoPorCodigo(
            @PathVariable String barcode,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId
    ) {
        Product produto = findProductByBarcodeUseCase.execute(new FindProductByBarcodeInput(barcode));
        registerScannedProductUseCase.execute(sessionId, produto);
        return ResponseEntity.ok(productApiMapper.toResponse(produto));
    }

    @Operation(summary = "Buscar produto por código de barras (POST)")
    @PostMapping("/buscar")
    public ResponseEntity<ProductResponse> buscarProdutoPorCodigoPost(
            @RequestBody BarcodeRequest barcodeRequest,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId
    ) {
        LOGGER.info("Recebida busca de produto via frontend para barcode {}", barcodeRequest.barcode());
        Product produto = findProductByBarcodeUseCase.execute(new FindProductByBarcodeInput(barcodeRequest.barcode()));
        registerScannedProductUseCase.execute(sessionId, produto);
        return ResponseEntity.ok(productApiMapper.toResponse(produto));
    }

    @Schema(name = "BarcodeRequest", description = "Requisição para busca de produto por código de barras")
    public record BarcodeRequest(String barcode) {
    }
}
