package com.controlefinanceiro.dto;

import com.controlefinanceiro.enums.TipoCategoria;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoRequest {

    @NotBlank
    @Size(max = 150)
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.01")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal valor;

    @NotNull
    private LocalDate vencimento;

    @NotNull
    private TipoCategoria tipo;

    @NotNull
    private Long contaId;

    @NotNull
    private Long categoriaId;
}
