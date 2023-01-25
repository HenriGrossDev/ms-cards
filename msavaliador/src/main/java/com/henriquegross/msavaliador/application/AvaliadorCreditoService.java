package com.henriquegross.msavaliador.application;


import com.henriquegross.msavaliador.application.ex.DadosClienteNotFoundException;
import com.henriquegross.msavaliador.application.ex.ErroComunicacaoMicroservicesException;
import com.henriquegross.msavaliador.domain.model.CartaoCliente;
import com.henriquegross.msavaliador.domain.model.DadosCliente;
import com.henriquegross.msavaliador.domain.model.SituacaoCliente;
import com.henriquegross.msavaliador.infra.clients.CartoesResourceClient;
import com.henriquegross.msavaliador.infra.clients.ClienteResourceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {


    private final ClienteResourceClient clientesClient;
    private final CartoesResourceClient cartoesClient;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException {
        try{
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);
            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }

    }
}
