package com.mktgus.autoatendimento.application.mapper;

import com.mktgus.autoatendimento.application.response.CouponResponse;
import com.mktgus.autoatendimento.domain.model.Coupon;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CouponApiMapper {
    CouponResponse toResponse(Coupon coupon);

    List<CouponResponse> toResponseList(List<Coupon> coupons);
}
