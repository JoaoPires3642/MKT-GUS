package com.mktgus.autoatendimento.infrastructure.persistence.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;


@Getter
@Setter
@Entity
public class OrderEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "cpf") //id_cliente em Pedido, recebe o id Cliente CPF
    private CustomerEntity cliente;

    @ManyToOne
    @JoinColumn(name = "id_cupom", referencedColumnName = "id")
    private CouponEntity cupom;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private double valorTotal;

    public OrderEntity() {
    }

    public CustomerEntity getCliente() {
        return cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CouponEntity getCupom() {
        return cupom;
    }

    public void setCupom(CouponEntity cupom) {
        this.cupom = cupom;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}
