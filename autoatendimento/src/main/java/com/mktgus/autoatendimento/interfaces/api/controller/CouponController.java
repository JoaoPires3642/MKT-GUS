package com.mktgus.autoatendimento.interfaces.api.controller;

import com.mktgus.autoatendimento.application.usecase.ListCouponsUseCase;
import com.mktgus.autoatendimento.interfaces.api.mapper.CouponApiMapper;
import com.mktgus.autoatendimento.interfaces.api.response.CouponResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cupons")
@CrossOrigin(origins = "http://localhost:3000")
public class CouponController {
    private final CouponApiMapper couponApiMapper;
    private final ListCouponsUseCase listCouponsUseCase;

    public CouponController(CouponApiMapper couponApiMapper, ListCouponsUseCase listCouponsUseCase) {
        this.couponApiMapper = couponApiMapper;
        this.listCouponsUseCase = listCouponsUseCase;
    }

    @GetMapping
    public List<CouponResponse> listarCupons() {
        return couponApiMapper.toResponseList(listCouponsUseCase.execute());
    }
}
