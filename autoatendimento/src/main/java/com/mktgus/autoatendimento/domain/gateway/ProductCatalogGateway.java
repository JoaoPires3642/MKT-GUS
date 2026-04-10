package com.mktgus.autoatendimento.domain.gateway;

import com.mktgus.autoatendimento.domain.model.Product;

import java.util.Optional;

public interface ProductCatalogGateway {
    Optional<Product> findByBarcode(String barcode);
}
