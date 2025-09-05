package com.mktgus.autoatendimento.repository.funcionario;

import com.mktgus.autoatendimento.Model.pessoa.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    boolean existsByMatricula(Long matricula);
}