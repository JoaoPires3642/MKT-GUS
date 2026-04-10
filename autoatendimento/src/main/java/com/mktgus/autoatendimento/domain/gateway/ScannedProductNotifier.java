package com.mktgus.autoatendimento.domain.gateway;

import com.mktgus.autoatendimento.domain.model.Product;

public interface ScannedProductNotifier {
    void publishProduct(Product product);

    void publishError(String message);
}
