package com.controlefinanceiro.dto;

import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.enums.TipoConta;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaRequest {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotNull
    private TipoConta tipo;

    @Builder.Default
    @NotNull
    @Digits(integer = 10, fraction = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;
}
