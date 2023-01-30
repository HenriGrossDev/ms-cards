package com.henriquegross.msavaliador.infra.clients;

import com.henriquegross.msavaliador.domain.model.Cartao;
import com.henriquegross.msavaliador.domain.model.CartaoCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscartoes", path = "/cartoes")
public interface CartoesResourceClient {

    @GetMapping(params = "cpf")
    ResponseEntity<List<CartaoCliente>> getCartoesByCliente(@RequestParam("cpf") String cpf);

    @GetMapping(value="/renda")
     ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda);

}

