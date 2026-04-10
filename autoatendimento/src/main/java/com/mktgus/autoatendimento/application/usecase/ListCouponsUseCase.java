package com.mktgus.autoatendimento.application.usecase;

import com.mktgus.autoatendimento.domain.gateway.CouponGateway;
import com.mktgus.autoatendimento.domain.model.Coupon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListCouponsUseCase {
    private final CouponGateway couponGateway;

    public ListCouponsUseCase(CouponGateway couponGateway) {
        this.couponGateway = couponGateway;
    }

    public List<Coupon> execute() {
        return couponGateway.findAll();
    }
}
