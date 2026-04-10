package com.mktgus.autoatendimento.interfaces.api.mapper;

import com.mktgus.autoatendimento.domain.model.Coupon;
import com.mktgus.autoatendimento.interfaces.api.response.CouponResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponApiMapper {
    CouponResponse toResponse(Coupon coupon);

    List<CouponResponse> toResponseList(List<Coupon> coupons);
}
