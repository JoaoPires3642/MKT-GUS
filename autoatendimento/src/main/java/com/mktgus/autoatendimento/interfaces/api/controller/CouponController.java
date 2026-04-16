package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.usecase.ListCouponsUseCase;
import com.mktgus.autoatendimento.interfaces.api.mapper.CouponApiMapper;
import com.mktgus.autoatendimento.interfaces.api.response.CouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cupons")
@CrossOrigin(origins = {"http://localhost:3000", "/swagger-ui", "/api-docs"})
@Tag(name = "Coupon", description = "Operações relacionadas a cupons de desconto")
public class CouponController {
    private final CouponApiMapper couponApiMapper;
    private final ListCouponsUseCase listCouponsUseCase;

    public CouponController(CouponApiMapper couponApiMapper, ListCouponsUseCase listCouponsUseCase) {
        this.couponApiMapper = couponApiMapper;
        this.listCouponsUseCase = listCouponsUseCase;
    }

    @Operation(summary = "Listar todos os cupons de desconto disponíveis")
    @ApiResponse(responseCode = "200", description = "Lista de cupons retornada")
    @GetMapping
    public List<CouponResponse> listarCupons() {
        return couponApiMapper.toResponseList(listCouponsUseCase.execute());
    }
}
