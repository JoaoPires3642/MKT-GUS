package com.mktgus.autoatendimento.infra.utils.controller;

import com.mktgus.autoatendimento.application.product.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.usecase.FindProductByBarcodeUseCase;
import com.mktgus.autoatendimento.application.usecase.ProcessBarcodeScanUseCase;
import com.mktgus.autoatendimento.application.mapper.ProductApiMapper;
import com.mktgus.autoatendimento.domain.model.Product;
import com.mktgus.autoatendimento.application.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Product", description = "Operações relacionadas a produtos")
public class ProductController {
    private final List<Product> scannedProducts = new CopyOnWriteArrayList<>();

    private final ProductApiMapper productApiMapper;
    private final FindProductByBarcodeUseCase findProductByBarcodeUseCase;
    private final ProcessBarcodeScanUseCase processBarcodeScanUseCase;

    public ProductController(
            ProductApiMapper productApiMapper,
            FindProductByBarcodeUseCase findProductByBarcodeUseCase,
            ProcessBarcodeScanUseCase processBarcodeScanUseCase
    ) {
        this.productApiMapper = productApiMapper;
        this.findProductByBarcodeUseCase = findProductByBarcodeUseCase;
        this.processBarcodeScanUseCase = processBarcodeScanUseCase;
    }

    @Operation(summary = "Listar produtos escaneados na sessão atual")
    @GetMapping("/listar")
    public List<ProductResponse> listarProdutos() {
        return scannedProducts.stream().map(productApiMapper::toResponse).toList();
    }

    @Operation(summary = "Buscar produto por código de barras (GET)")
    @GetMapping("/buscar/{barcode}")
    public ResponseEntity<ProductResponse> buscarProdutoPorCodigo(@PathVariable String barcode) {
        Product produto = findProductByBarcodeUseCase.execute(new FindProductByBarcodeInput(barcode));
        scannedProducts.add(produto);
        return ResponseEntity.ok(productApiMapper.toResponse(produto));
    }

    @Operation(summary = "Buscar produto por código de barras (POST)")
    @PostMapping("/buscar")
    public ResponseEntity<ProductResponse> buscarProdutoPorCodigoPost(@RequestBody BarcodeRequest barcodeRequest) {
        Product produto = processBarcodeScanUseCase.execute(new FindProductByBarcodeInput(barcodeRequest.barcode()));
        scannedProducts.add(produto);
        return ResponseEntity.ok(productApiMapper.toResponse(produto));
    }

    @Schema(name = "BarcodeRequest", description = "Requisição para busca de produto por código de barras")
    public record BarcodeRequest(String barcode) {}
}
