package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.product.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.gateway.ScannedProductNotifier;
import com.mktgus.autoatendimento.domain.model.Product;
import org.springframework.stereotype.Service;

@Service
public class ProcessBarcodeScanUseCase {
    private final FindProductByBarcodeUseCase findProductByBarcodeUseCase;
    private final ScannedProductNotifier scannedProductNotifier;

    public ProcessBarcodeScanUseCase(
            FindProductByBarcodeUseCase findProductByBarcodeUseCase,
            ScannedProductNotifier scannedProductNotifier
    ) {
        this.findProductByBarcodeUseCase = findProductByBarcodeUseCase;
        this.scannedProductNotifier = scannedProductNotifier;
    }

    public Product execute(FindProductByBarcodeInput input) {
        try {
            Product product = findProductByBarcodeUseCase.execute(input);
            scannedProductNotifier.publishProduct(product);
            return product;
        } catch (NotFoundException exception) { //Crítica que ocorre no front
            scannedProductNotifier.publishError("Produto nao encontrado para o codigo de barras: " + input.barcode());
            throw exception;
        }
    }
}
