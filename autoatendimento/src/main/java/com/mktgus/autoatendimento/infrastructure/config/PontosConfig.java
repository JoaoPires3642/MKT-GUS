package com.mktgus.autoatendimento.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pontos")
public class PontosConfig {

    private double valorPorPonto = 5.0;
    private int pontosPorBloco = 10;

    public double getValorPorPonto() {
        return valorPorPonto;
    }

    public void setValorPorPonto(double valorPorPonto) {
        this.valorPorPonto = valorPorPonto;
    }

    public int getPontosPorBloco() {
        return pontosPorBloco;
    }

    public void setPontosPorBloco(int pontosPorBloco) {
        this.pontosPorBloco = pontosPorBloco;
    }
}