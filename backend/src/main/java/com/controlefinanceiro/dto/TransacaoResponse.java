package com.controlefinanceiro.dto;

import com.controlefinanceiro.enums.StatusTransacaoResponse;
import com.controlefinanceiro.enums.TipoCategoria;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransacaoResponse {

    @EqualsAndHashCode.Include
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate vencimento;
    private TipoCategoria tipo;
    private StatusTransacaoResponse status;
    private Long contaId;
    private String contaNome;
    private Long categoriaId;
    private String categoriaNome;
}
