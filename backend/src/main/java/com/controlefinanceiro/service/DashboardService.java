package com.controlefinanceiro.service;

import com.controlefinanceiro.dto.ContaResponse;
import com.controlefinanceiro.dto.DashboardAtrasadaResponse;
import com.controlefinanceiro.dto.DashboardResponse;
import com.controlefinanceiro.dto.TransacaoResponse;
import com.controlefinanceiro.enums.StatusTransacaoResponse;
import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.mapper.DashboardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ContaService contaService;
    private final TransacaoService transacaoService;
    private final DashboardMapper mapper;

    public DashboardResponse get() {
        DashboardResponse dashboard = new DashboardResponse();

        BigDecimal saldoTotal = contaService.findAll("").stream()
                .map(ContaResponse::getSaldoAtual)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal receitasMes = transacaoService.findAll("", TipoCategoria.RECEITA,
                        StatusTransacaoResponse.PAGA, YearMonth.now())
                .stream()
                .map(TransacaoResponse::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesasMes = transacaoService.findAll("", TipoCategoria.DESPESA,
                        StatusTransacaoResponse.PAGA, YearMonth.now())
                .stream()
                .map(TransacaoResponse::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardAtrasadaResponse> atrasadas = transacaoService.findAll("",
                        null, StatusTransacaoResponse.ATRASADA, null)
                .stream()
                .map(mapper::toAtrasadaResponse)
                .toList();

        dashboard.setSaldoTotal(saldoTotal);
        dashboard.setReceitasMes(receitasMes);
        dashboard.setDespesasMes(despesasMes);
        dashboard.setAtrasadas(atrasadas);

        return dashboard;
    }

}
