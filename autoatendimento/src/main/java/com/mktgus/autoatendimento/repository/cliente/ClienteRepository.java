package com.mktgus.autoatendimento.repository.cliente;
import com.mktgus.autoatendimento.Model.pessoa.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(Long cpf); // <- Adicione isto
}