package com.henriquegross.ms.cartoes.application.representation;

import com.henriquegross.ms.cartoes.domain.ClienteCartao;
import com.henriquegross.ms.cartoes.infra.repository.ClienteCartaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteCartaoService {

    private final ClienteCartaoRepository repository;

    public List<ClienteCartao> listCartoesByCpf(String cpf){
        return repository.findByCpf(cpf);
    }

}
