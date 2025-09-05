package com.mktgus.autoatendimento.Model.pedido;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ItemPedido {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Um pedido pode ter Vário Itens Pedido
    @JoinColumn( name = "id_pedido", referencedColumnName = "id")
    private Pedido pedido;

    @Column(nullable = false)
    private int quantidade; //pegar do Front?

    @Column(nullable = false)
    private double valorItemTotal;


    //// Atributos que virão do DTO
    private String codigoEan;
    private String nomeProduto;
    private double valorProduto;
    private boolean produtoMaiorDeIdade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValorItemTotal() {
        return valorItemTotal;
    }

    public void setValorItemTotal(double valorItemTotal) {
        this.valorItemTotal = valorItemTotal;
    }

    public String getCodigoEan() {
        return codigoEan;
    }

    public void setCodigoEan(String codigoEan) {
        this.codigoEan = codigoEan;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public double getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(double valorProduto) {
        this.valorProduto = valorProduto;
    }

    public boolean isProdutoMaiorDeIdade() {
        return produtoMaiorDeIdade;
    }

    public void setProdutoMaiorDeIdade(boolean produtoMaiorDeIdade) {
        this.produtoMaiorDeIdade = produtoMaiorDeIdade;
    }
}