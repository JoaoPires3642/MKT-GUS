package com.mktgus.autoatendimento.repository.cupom;

import com.mktgus.autoatendimento.Model.cupom.Cupom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CupomRepository extends JpaRepository<Cupom, Long> {
}