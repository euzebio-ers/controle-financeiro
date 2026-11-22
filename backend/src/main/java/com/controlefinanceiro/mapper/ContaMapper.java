package com.controlefinanceiro.mapper;

import com.controlefinanceiro.dto.ContaRequest;
import com.controlefinanceiro.dto.ContaResponse;
import com.controlefinanceiro.enums.StatusTransacao;
import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.model.Conta;
import com.controlefinanceiro.model.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ContaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transacoes", ignore = true)
    Conta toConta(ContaRequest request);

    @Mapping(source = "conta", target = "saldoAtual", qualifiedByName = "getSaldoAtual")
    ContaResponse toResponse(Conta conta);

    @Named("getSaldoAtual")
    default BigDecimal getSaldoAtual(Conta conta) {
        List<Transacao> transacoes = conta.getTransacoes().stream()
                .filter(t -> t.getStatus() == StatusTransacao.PAGA)
                .toList();

        BigDecimal receitas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoCategoria.RECEITA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesas = transacoes.stream()
                .filter(t -> t.getTipo() == TipoCategoria.DESPESA)
                .map(Transacao::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return receitas.subtract(despesas).add(conta.getSaldoInicial());
    }
}
