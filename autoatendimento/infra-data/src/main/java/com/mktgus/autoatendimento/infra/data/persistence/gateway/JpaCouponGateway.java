package com.mktgus.autoatendimento.infra.data.persistence.gateway;

import com.mktgus.autoatendimento.application.gateway.CouponGateway;
import com.mktgus.autoatendimento.domain.model.Coupon;
import com.mktgus.autoatendimento.infra.data.persistence.mapper.CouponEntityMapper;
import com.mktgus.autoatendimento.infra.data.persistence.repository.CouponRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaCouponGateway implements CouponGateway {
    private final CouponRepository couponRepository;

    public JpaCouponGateway(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public List<Coupon> findAll() {
        return couponRepository.findAll().stream().map(CouponEntityMapper::toDomain).toList();
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return couponRepository.findById(id).map(CouponEntityMapper::toDomain);
    }
}
