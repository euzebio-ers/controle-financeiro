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

    @NotBlank(message = "Descrição não pode ser vazia")
    @Size(max = 150, message = "A descrição não pode ter mais de 150 caracteres")
    private String descricao;

    @NotNull(message = "O valor não pode ser nulo")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que 0")
    @Digits(integer = 10, fraction = 2, message = "O valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
    private BigDecimal valor;

    @NotNull(message = "O vencimento não pode ser nulo")
    private LocalDate vencimento;

    @NotNull(message = "O tipo não pode ser nulo")
    private TipoCategoria tipo;

    @NotNull(message = "contaId não pode ser nulo")
    private Long contaId;

    @NotNull(message = "categoriaId não pode ser nulo")
    private Long categoriaId;

    public void setDescricao(String descricao) {
        this.descricao = descricao == null ? null : descricao.trim();
    }
}
