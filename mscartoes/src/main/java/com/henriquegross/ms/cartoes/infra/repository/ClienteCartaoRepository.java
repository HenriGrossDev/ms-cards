package com.henriquegross.ms.cartoes.infra.repository;

import ch.qos.logback.core.net.server.Client;
import com.henriquegross.ms.cartoes.domain.ClienteCartao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteCartaoRepository extends JpaRepository<ClienteCartao, Long> {
    List<ClienteCartao> findByCpf(String cpf);


}
