package com.mktgus.autoatendimento.infrastructure.persistence.mapper;

import com.mktgus.autoatendimento.infrastructure.persistence.entity.CouponEntity;

public final class CouponEntityMapper {
    private CouponEntityMapper() {
    }

    public static com.mktgus.autoatendimento.domain.model.Coupon toDomain(CouponEntity entity) {
        return new com.mktgus.autoatendimento.domain.model.Coupon(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getValorDesconto(),
                entity.isDescontoEmPorcentual(),
                entity.getCusto(),
                entity.getMinPurchase(),
                entity.getMaxDiscount()
        );
    }
}
