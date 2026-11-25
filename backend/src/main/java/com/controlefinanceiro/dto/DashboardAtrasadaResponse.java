package com.controlefinanceiro.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAtrasadaResponse {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate vencimento;

}
