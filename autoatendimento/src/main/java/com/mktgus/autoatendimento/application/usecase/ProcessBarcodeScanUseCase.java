package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.domain.gateway.ScannedProductNotifier;
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
        } catch (NotFoundException exception) {
            scannedProductNotifier.publishError("Produto nao encontrado para o codigo de barras: " + input.barcode());
            throw exception;
        }
    }
}
