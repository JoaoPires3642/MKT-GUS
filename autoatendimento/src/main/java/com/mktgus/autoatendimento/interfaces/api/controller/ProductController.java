package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.model.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.usecase.FindProductByBarcodeUseCase;
import com.mktgus.autoatendimento.application.usecase.ProcessBarcodeScanUseCase;
import com.mktgus.autoatendimento.domain.model.Product;
import com.mktgus.autoatendimento.interfaces.api.mapper.ProductApiMapper;
import com.mktgus.autoatendimento.interfaces.api.response.ProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("/listar")
    public List<ProductResponse> listarProdutos() {
        return scannedProducts.stream().map(productApiMapper::toResponse).toList();
    }

    @GetMapping("/buscar/{barcode}")
    public ResponseEntity<ProductResponse> buscarProdutoPorCodigo(@PathVariable String barcode) {
        Product produto = findProductByBarcodeUseCase.execute(new FindProductByBarcodeInput(barcode));
        scannedProducts.add(produto);
        return ResponseEntity.ok(productApiMapper.toResponse(produto));
    }

    @PostMapping("/buscar")
    public ResponseEntity<ProductResponse> buscarProdutoPorCodigoPost(@RequestBody BarcodeRequest barcodeRequest) {
        Product produto = processBarcodeScanUseCase.execute(new FindProductByBarcodeInput(barcodeRequest.barcode()));
        scannedProducts.add(produto);
        return ResponseEntity.ok(productApiMapper.toResponse(produto));
    }

    public record BarcodeRequest(String barcode) {
    }
}
