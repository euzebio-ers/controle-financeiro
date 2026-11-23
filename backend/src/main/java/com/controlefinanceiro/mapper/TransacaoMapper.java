package com.controlefinanceiro.mapper;

import com.controlefinanceiro.dto.TransacaoRequest;
import com.controlefinanceiro.dto.TransacaoResponse;
import com.controlefinanceiro.enums.StatusTransacao;
import com.controlefinanceiro.enums.StatusTransacaoResponse;
import com.controlefinanceiro.model.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TransacaoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "dataPagamento", ignore = true)
    @Mapping(target = "conta", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    Transacao toTransacao(TransacaoRequest request);

    @Mapping(source = "transacao", target = "status", qualifiedByName = "getStatusResponse")
    @Mapping(source = "conta.id", target = "contaId")
    @Mapping(source = "conta.nome", target = "contaNome")
    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nome", target = "categoriaNome")
    TransacaoResponse toResponse(Transacao transacao);

    @Named("getStatusResponse")
    default StatusTransacaoResponse getStatusResponse(Transacao transacao) {
        if (transacao.getStatus() == StatusTransacao.PENDENTE) {
            if (transacao.getVencimento().isBefore(LocalDate.now())) {
                return StatusTransacaoResponse.ATRASADA;
            }
            return StatusTransacaoResponse.PENDENTE;
        }
        return StatusTransacaoResponse.PAGA;
    }


}
