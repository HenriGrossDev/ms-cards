package com.henriquegross.msclients.infra.repository;

import com.henriquegross.msclients.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByCpf(String cpf);
}
