package com.henriquegross.ms.cartoes.application.representation;


import com.henriquegross.ms.cartoes.domain.BandeiraCartao;
import com.henriquegross.ms.cartoes.domain.Cartao;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartaoSaveRequest {

    private String nome;
    private BandeiraCartao bandeira;
    private BigDecimal renda;
    private BigDecimal limite;

    public Cartao toModel(){
        return new Cartao(nome, bandeira, renda, limite);
    }

}
