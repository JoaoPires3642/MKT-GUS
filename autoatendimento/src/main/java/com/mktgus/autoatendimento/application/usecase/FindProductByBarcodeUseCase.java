package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.application.model.FindProductByBarcodeInput;
import com.mktgus.autoatendimento.application.exception.NotFoundException;
import com.mktgus.autoatendimento.application.exception.ValidationException;
import com.mktgus.autoatendimento.domain.gateway.ProductCatalogGateway;
import com.mktgus.autoatendimento.domain.model.Product;
import org.springframework.stereotype.Service;

@Service
public class FindProductByBarcodeUseCase {
    private final ProductCatalogGateway productCatalogGateway;

    public FindProductByBarcodeUseCase(ProductCatalogGateway productCatalogGateway) {
        this.productCatalogGateway = productCatalogGateway;
    }

    public Product execute(FindProductByBarcodeInput input) {
        if (input.barcode() == null || input.barcode().isBlank()) {
            throw new ValidationException("Codigo de barras invalido.");
        }

        return productCatalogGateway.findByBarcode(input.barcode())
                .orElseThrow(() -> new NotFoundException("Produto nao encontrado para o codigo informado."));
    }
}
