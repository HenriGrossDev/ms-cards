package com.henriquegross.msavaliador.application;


import com.henriquegross.msavaliador.application.ex.DadosClienteNotFoundException;
import com.henriquegross.msavaliador.application.ex.ErroComunicacaoMicroservicesException;
import com.henriquegross.msavaliador.application.ex.ErroSolicitacaoCartaoException;
import com.henriquegross.msavaliador.domain.model.*;
import com.henriquegross.msavaliador.infra.clients.CartoesResourceClient;
import com.henriquegross.msavaliador.infra.clients.ClienteResourceClient;
import com.henriquegross.msavaliador.infra.mqueue.SoclicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {


    private final ClienteResourceClient clientesClient;
    private final CartoesResourceClient cartoesClient;
    private final SoclicitacaoEmissaoCartaoPublisher emissaoCartaoPublisher;

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

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClienteNotFoundException, ErroComunicacaoMicroservicesException{
        try{
           ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
           ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAte(renda);

           List<Cartao> cartoes = cartoesResponse.getBody();
           List<CartaoAprovado> listaCartoesAprovados = cartoes.stream().map(cartao -> {

               DadosCliente dadosCliente = dadosClienteResponse.getBody();

               BigDecimal limiteBasico = cartao.getLimiteBasico();
               BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
               BigDecimal fator = idadeBD.divide(BigDecimal.valueOf(10));
               BigDecimal limiteAprovado = fator.multiply(limiteBasico);

               CartaoAprovado aprovado = new CartaoAprovado();
               aprovado.setCartao(cartao.getNome());
               aprovado.setBandeira(cartao.getBandeira());
               aprovado.setLimiteAprovado(limiteAprovado);

               return aprovado;

           }).collect(Collectors.toList());

           return new RetornoAvaliacaoCliente(listaCartoesAprovados);

        }catch (FeignException.FeignClientException e){
            int status = e.status();
            if(HttpStatus.NOT_FOUND.value() == status){
                throw new DadosClienteNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(),status);
        }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados){
        try{
            emissaoCartaoPublisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        }catch (Exception e){
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }


}
