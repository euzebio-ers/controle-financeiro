package com.controlefinanceiro.dto;

import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.enums.TipoConta;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContaResponse {

    @EqualsAndHashCode.Include
    private Long id;
    private String nome;
    private TipoConta tipo;
    private BigDecimal saldoInicial;
    private BigDecimal saldoAtual;
}
