package com.controlefinanceiro.dto;

import com.controlefinanceiro.enums.TipoCategoria;
import com.controlefinanceiro.enums.TipoConta;
import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor(onConstructor_ = @JsonCreator)
@AllArgsConstructor
@Builder
public class ContaRequest {

    @NotBlank(message = "Nome não pode ser vazio")
    @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres")
    private String nome;

    @NotNull(message = "O tipo não pode ser nulo")
    private TipoConta tipo;

    @Builder.Default
    @NotNull(message = "O saldo inicial não pode ter valor null")
    @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    public void setNome(String nome) {
        this.nome = nome == null ? null : nome.trim();
    }
}
