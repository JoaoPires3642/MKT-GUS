package com.mktgus.autoatendimento.repository.pedido;

import com.mktgus.autoatendimento.Model.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}