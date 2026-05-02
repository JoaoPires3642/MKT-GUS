package com.mktgus.autoatendimento.application.mapper;

import com.mktgus.autoatendimento.application.response.ProductResponse;
import com.mktgus.autoatendimento.domain.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductApiMapper {
    ProductResponse toResponse(Product product);
}
