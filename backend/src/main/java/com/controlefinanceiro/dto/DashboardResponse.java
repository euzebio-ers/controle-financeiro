package com.controlefinanceiro.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private BigDecimal saldoTotal;
    private BigDecimal receitasMes;
    private BigDecimal despesasMes;
    private List<DashboardAtrasadaResponse> atrasadas;

}
