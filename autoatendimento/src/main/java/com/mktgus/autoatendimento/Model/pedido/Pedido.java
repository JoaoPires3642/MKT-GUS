package com.mktgus.autoatendimento.Model.pedido;


import com.mktgus.autoatendimento.Model.cupom.*;
import com.mktgus.autoatendimento.Model.pessoa.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Pedido {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "cpf") //id_cliente em Pedido, recebe o id Cliente CPF
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_cupom", referencedColumnName = "id")
    private Cupom cupom;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private double valorTotal;

    public Cliente getCliente() {
        return cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cupom getCupom() {
        return cupom;
    }

    public void setCupom(Cupom cupom) {
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