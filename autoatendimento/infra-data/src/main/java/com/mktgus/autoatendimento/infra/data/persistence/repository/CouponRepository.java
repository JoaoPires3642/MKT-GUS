package com.mktgus.autoatendimento.infra.data.persistence.repository;

import com.mktgus.autoatendimento.infra.data.persistence.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
}
