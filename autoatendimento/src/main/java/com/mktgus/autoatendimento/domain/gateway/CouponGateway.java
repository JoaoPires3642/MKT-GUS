package com.mktgus.autoatendimento.domain.gateway;

import com.mktgus.autoatendimento.domain.model.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponGateway {
    List<Coupon> findAll();

    Optional<Coupon> findById(Long id);
}
