package com.mktgus.autoatendimento.interfaces.api.mapper;

import com.mktgus.autoatendimento.domain.model.Product;
import com.mktgus.autoatendimento.interfaces.api.response.ProductResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductApiMapper {
    ProductResponse toResponse(Product product);
}
